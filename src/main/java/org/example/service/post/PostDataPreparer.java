package org.example.service.post;

import org.example.dto.PostDto;
import org.example.entity.Post;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostDataPreparer {

  public Post preparePostForPublishing(Post post) {
    post.setPublished(true);
    post.setPublishedAt(LocalDateTime.now());
    post.setScheduledAt(null);
    return post;
  }

  public Post preparePostForDeleting(Post post) {
    post.setPublished(false);
    post.setDeleted(true);
    return post;
  }

  public PostDto preparePostDtoForUpdating(PostDto postDto, Post post) {
    // Если пост уже опубликован, нельзя его запланировать на публикацию
    if (postDto.getScheduledAt() != null
            && post.isPublished()) {
      postDto.setScheduledAt(null);
    }
    return postDto;
  }
}
