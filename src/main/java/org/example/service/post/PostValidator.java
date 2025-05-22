package org.example.service.post;

import lombok.RequiredArgsConstructor;
import org.example.client.UserServiceClient;
import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.handler.exception.AuthorChangeForbiddenException;
import org.example.handler.exception.PostAlreadyPublishedException;
import org.example.handler.exception.ResourceNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class PostValidator {
  private final UserServiceClient userService;
  private final MessageSource messageSource;

  public void validateBeforeCreate(PostDto postDto) {
    ensureUserExits(postDto.getAuthorId());
  }

  public void validateBeforeUpdate(PostDto dto, Post entity) {
    ensurePostIsNotDeleted(entity);
    ensureAuthorHasNotBeenChanged(dto.getAuthorId(), entity.getAuthorId());


  }

  public void validateBeforePublish(Post post) {
    ensurePostIsNotDeleted(post);
    ensurePostIsNotPublished(post);
  }

  public void ensurePostIsNotDeleted(Post post) {
    if (post.isDeleted()) {
      throw new ResourceNotFoundException(
              messageSource.getMessage("exception.post.not.found", new Object[]{post.getId()}, Locale.getDefault())
      );
    }
  }

  private void ensurePostIsNotPublished(Post post) {
    if (post.isPublished()) {
      throw new PostAlreadyPublishedException(
              messageSource.getMessage("exception.post.republish", null, Locale.getDefault())
      );
    }
  }

  private void ensureAuthorHasNotBeenChanged(Long dtoAuthorId, Long entityAuthorId) {
    if (!dtoAuthorId.equals(entityAuthorId)) {
      throw new AuthorChangeForbiddenException(messageSource.getMessage("exception.post.change.author.forbidden", null, Locale.getDefault()));
    }
  }

  private void ensureUserExits(Long userId) {
    userService.getUser(userId);
  }
}
