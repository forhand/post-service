package org.example.service.post;

import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.handler.exception.AuthorChangeForbiddenException;
import org.example.handler.exception.PostAlreadyPublishedException;
import org.example.handler.exception.ResourceNotFoundException;
import org.example.mapper.PostMapperImpl;
import org.example.repository.PostRepository;
import org.example.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
  @Mock
  private PostRepository postRepository;
  @Spy
  private PostMapperImpl mapper;
  @Mock
  private PostValidator validator;
  @Mock
  private PostDataPreparer preparer;
  @Mock
  private MessageSource messageSource;
  @InjectMocks
  private PostService service;

  private PostContainer container;
  private PostDto dto;
  private Post entity;
  private Long postId;

  @BeforeEach
  void setUp() {
    container = new PostContainer();
    entity = container.entity();
    dto = container.dto();
    postId = container.postId();
  }

  @Test
  void testCreatePost() {
    dto.setId(null);
    Post post = mapper.toEntity(dto);
    when(postRepository.save(post)).thenReturn(entity);

    PostDto actDto = service.create(dto);

    assertNotNull(actDto);
    assertNotNull(actDto.getId());
    assertNotSame(dto, actDto);
    verify(postRepository, times(1)).save(post);
  }

  @Test
  void testCreatePostWithAuthorDoesNotExist() {
    dto.setId(null);
    doThrow(new ResourceNotFoundException("Author not found")).when(validator)
            .validateBeforeCreate(dto);

    assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
  }

  @Test
  void testPublish() {
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    Post postPublished = container.entity();
    postPublished.setPublished(true);
    postPublished.setPublishedAt(LocalDateTime.now());
    postPublished.setScheduledAt(null);
    when(preparer.preparePostForPublishing(entity)).thenReturn(postPublished);
    when(postRepository.save(postPublished)).thenReturn(postPublished);
    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

    PostDto actPostDto = service.publish(postId);

    verify(postRepository, times(1)).save(captor.capture());
    Post postCaptured = captor.getValue();
    assertNotNull(actPostDto);
    assertTrue(postCaptured.isPublished());
    assertEquals(actPostDto.getId(), postCaptured.getId());
  }

  @Test
  void testRepublish() {
    entity.setPublished(true);
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    doThrow(new PostAlreadyPublishedException("The post has already been published")).when(validator).validateBeforePublish(entity);

    assertThrows(PostAlreadyPublishedException.class, () -> service.publish(postId));
  }

  @Test
  void testPublishDeletedPost() {
    entity.setDeleted(true);
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    doThrow(new ResourceNotFoundException("The post not found")).when(validator).validateBeforePublish(entity);

    assertThrows(ResourceNotFoundException.class, () -> service.publish(postId));
  }

  @Test
  void testDelete() {
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    Post deletedEntity = container.entity();
    deletedEntity.setDeleted(true);
    deletedEntity.setPublished(false);
    when(preparer.preparePostForDeleting(entity)).thenReturn(deletedEntity);
    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

    service.delete(postId);

    verify(postRepository, times(1)).save(captor.capture());
    Post postCaptured = captor.getValue();
    assertTrue(postCaptured.isDeleted());
    assertEquals(postId, postCaptured.getId());
    assertFalse(postCaptured.isPublished());
  }

  @Test
  void testDeleteAlreadyDeletedPost() {
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    doThrow(new ResourceNotFoundException("Post not found")).when(validator).ensurePostIsNotDeleted(entity);

    assertThrows(ResourceNotFoundException.class, () -> service.delete(postId));
    verify(postRepository, times(0)).deleteById(postId);
  }

  @Test
  void testUpdate() {
    dto.setContent("updated");
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    when(preparer.preparePostDtoForUpdating(dto, entity)).thenReturn(dto);
    Post entitySaved = container.entity();
    entitySaved.setContent(dto.getContent());
    when(postRepository.save(entitySaved)).thenReturn(entitySaved);

    PostDto actDto = service.update(postId, dto);

    assertNotNull(actDto);
    assertEquals(dto.getContent(), actDto.getContent());
  }

  @Test
  void testUpdateScheduleAtForPublishedPost() {
    dto.setScheduledAt(LocalDateTime.now());
    entity.setPublished(true);
    entity.setScheduledAt(null);
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    PostDto updatedDto = container.dto();
    updatedDto.setScheduledAt(null);
    when(preparer.preparePostDtoForUpdating(dto, entity)).thenReturn(updatedDto);
    when(postRepository.save(entity)).thenReturn(entity);

    PostDto actDto = service.update(postId, dto);

    assertNotNull(actDto);
    assertEquals(updatedDto, actDto);
  }

  @Test
  void testUpdateEarlierSchedule() {
    dto.setScheduledAt(LocalDateTime.now().minusDays(1));
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    PostDto updatedDto = container.dto();
    when(preparer.preparePostDtoForUpdating(dto, entity)).thenReturn(dto);
    Post publishedEntity = container.entity();
    publishedEntity.setScheduledAt(null);
    publishedEntity.setPublished(true);
    when(preparer.preparePostForPublishing(entity)).thenReturn(publishedEntity);
    when(postRepository.save(publishedEntity)).thenReturn(publishedEntity);
    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

    PostDto actDto = service.update(postId, dto);

    verify(postRepository, times(1)).save(captor.capture());
    assertNotNull(actDto);
    Post postCaptured = captor.getValue();
    assertEquals(publishedEntity, postCaptured);
  }

  @Test
  void testUpdateAuthor() {
    dto.setAuthorId(entity.getAuthorId() + 1);
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    doThrow(new AuthorChangeForbiddenException("Changing the author of a post is forbidden")).when(validator).validateBeforeUpdate(dto, entity);

    assertThrows(AuthorChangeForbiddenException.class, () -> service.update(postId, dto));
    verify(postRepository, times(0)).save(mapper.toEntity(dto));
  }

  @Test
  void testUpdateDeletedPost() {
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    doThrow(new ResourceNotFoundException("Post not found")).when(validator).validateBeforeUpdate(dto, entity);

    assertThrows(ResourceNotFoundException.class, () -> service.update(postId, dto));
    verify(postRepository, times(0)).save(mapper.toEntity(dto));
  }

  @Test
  void testGet() {
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));

    PostDto actDto = service.get(postId);

    assertNotNull(actDto);
    assertNotSame(dto, actDto);
    assertEquals(dto.getId(), actDto.getId());
  }

  @Test
  void testGetDeletedPost() {
    entity.setDeleted(true);
    when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
    doThrow(new ResourceNotFoundException("Post not found")).when(validator).ensurePostIsNotDeleted(entity);

    assertThrows(ResourceNotFoundException.class, () -> service.get(postId));
  }

  @Test
  void testGetPostDoesNotExit() {
    when(postRepository.findById(postId)).thenReturn(Optional.empty());
    when(messageSource.getMessage(any(), any(), any())).thenReturn("The post not found");

    assertThrows(ResourceNotFoundException.class, () -> service.get(postId));
  }

  @Test
  void testGetAllNotPublishedNotDeleted() {
    Long authorId = container.authorId();
    List<Post> drafts = container.drafts();
    when(postRepository.findNotPublishedPostsByAuthor(authorId)).thenReturn(drafts);

    List<PostDto> actList = service.getAllNotPublishedNotDeleted(authorId);

    assertNotNull(actList);
    assertEquals(drafts.size(), actList.size());
  }

  @Test
  void testGetAllPublishedNotDeleted() {
    Long authorId = container.authorId();
    List<Post> posts = container.posts();
    when(postRepository.findPublishedPostsByAuthor(authorId)).thenReturn(posts);

    List<PostDto> actList = service.getAllPublishedNotDeleted(authorId);

    assertNotNull(actList);
    assertEquals(posts.size(), actList.size());
  }
}