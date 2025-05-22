package org.example.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.handler.exception.ResourceNotFoundException;
import org.example.mapper.PostMapper;
import org.example.repository.PostRepository;
import org.example.service.cacheHelper.PostCacheHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "post")
public class PostService {
  private final PostRepository postRepository;
  private final PostMapper mapper;
  private final PostValidator validator;
  private final PostDataPreparer preparer;
  private final MessageSource messageSource;
  private final PostCacheHelper cacheHelper;

  public PostDto create(PostDto postDto) {
    validator.validateBeforeCreate(postDto);
    Post postEntity = mapper.toEntity(postDto);
    Post createdPost = postRepository.save(postEntity);
    log.info("Created a post: {}", createdPost);
    // todo: publish event to Kafka

    return mapper.toDto(createdPost);
  }

  @Cacheable(key = "#postId", unless = "#result == null")
  public PostDto publish(Long postId) {
    Post post = getById(postId);
    validator.validateBeforePublish(post);
    post = preparer.preparePostForPublishing(post);
    Post publishedPost = postRepository.save(post);
    log.info("Published a post with id: {}", publishedPost.getId());
    return mapper.toDto(post);
  }

  @CacheEvict(key = "#postId")
  public void delete(Long postId) {
    Post post = getById(postId);
    validator.ensurePostIsNotDeleted(post);
    post = preparer.preparePostForDeleting(post);
    postRepository.save(post);
    log.info("Deleted a post with id: {}", postId);
    //todo: publish event to Kafka
  }

  @CachePut(key = "#postId", unless = "#result == null || !#root.target.existsInCache(#postId)")
  public PostDto update(Long postId, PostDto postDto) {
    Post post = getById(postId);
    validator.validateBeforeUpdate(postDto, post);
    postDto = preparer.preparePostDtoForUpdating(postDto, post);
    mapper.update(post, postDto);

    if (scheduleCheck(post)) {
      return publish(post.getId());
    }
    post = postRepository.save(post);
    log.info("Updated a post: {}", post);

    return mapper.toDto(post);
  }

  @Cacheable(key = "#postId", unless = "true")
  public PostDto get(Long postId) {
    Post post = getById(postId);
    validator.ensurePostIsNotDeleted(post);
    log.info("Found a post with id: {}", postId);
    //todo: переделать кэширование на методы сервиса, чтобы можно было публиковать событие о просмотре поста
    return mapper.toDto(post);
  }

  public List<PostDto> getAllNotPublishedNotDeleted(Long userId) {
    return mapper.entitiesToDtos(postRepository.findNotPublishedPostsByAuthor(userId));
  }

  public List<PostDto> getAllPublishedNotDeleted(Long authorId) {
    return mapper.entitiesToDtos(postRepository.findPublishedPostsByAuthor(authorId));
  }

  public boolean existsInCache(Long postId) {
    return cacheHelper.existsInCache(postId);
  }

//  @Scheduled(cron = "${scheduling.publishing_post.cron}")
  public void publishScheduledPosts() {
    for (Post post : postRepository.findReadyToPublish()) {
      // todo: переделать кэширование с аннотаций на методы сервиса
      publish(post.getId());
    }
  }

  private Post getById(Long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(
            messageSource.getMessage("exception.post.not.found", new Object[]{postId}, Locale.getDefault())
    ));
  }

  /*
   Если пост был запланирован на публикацию раньше текущего времени,
   то публикуем его сейчас.
   */
  private boolean scheduleCheck(Post post) {
    return post.getScheduledAt() != null
            && post.getScheduledAt().isBefore(LocalDateTime.now());
  }

}
