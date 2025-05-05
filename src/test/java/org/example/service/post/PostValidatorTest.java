package org.example.service.post;

import org.example.client.UserServiceClient;
import org.example.dto.PostDto;
import org.example.dto.UserDto;
import org.example.entity.Post;
import org.example.handler.exception.AuthorChangeForbiddenException;
import org.example.handler.exception.PostAlreadyPublishedException;
import org.example.handler.exception.ResourceNotFoundException;
import org.example.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {
  @Mock
  private UserServiceClient userService;
  @Mock
  private MessageSource messageSource;
  @InjectMocks
  private PostValidator validator;
  private PostContainer container;
  private Post post;
  private PostDto postDto;

  @BeforeEach
  void setUp() {
    container = new PostContainer();
    post = container.entity();
    postDto = container.dto();
  }


  @Test
  void validateBeforeCreate() {
    long authorId = container.authorId();
    UserDto userDto = UserDto.builder()
            .id(authorId)
            .build();
    when(userService.getUser(authorId)).thenReturn(userDto);

    assertDoesNotThrow(() -> validator.validateBeforeCreate(postDto));
  }

  @Test
  void validateBeforeCreate_whenUserNotFound() {
    long authorId = container.authorId();
    when(userService.getUser(authorId)).thenThrow(new ResourceNotFoundException("User not found"));

    assertThrows(ResourceNotFoundException.class, () -> validator.validateBeforeCreate(postDto));
  }

  @Test
  void testValidateBeforeUpdate_success() {
    assertDoesNotThrow(() -> validator.validateBeforePublish(post));
  }

  @Test
  void testValidateBeforeUpdate_whenPostDeleted() {
    post.setDeleted(true);

    assertThrows(ResourceNotFoundException.class, () -> validator.validateBeforeUpdate(postDto, post));
  }

  @Test
  void testValidateBeforeUpdate_whenAuthorChanged() {
    postDto.setAuthorId(post.getAuthorId() + 1);

    assertThrows(AuthorChangeForbiddenException.class, () -> validator.validateBeforeUpdate(postDto, post));
  }

  @Test
  void validateBeforePublish_success() {
    assertDoesNotThrow(() -> validator.validateBeforePublish(post));
  }

  @Test
  void ensurePostIsNotDeleted_whenPostIsDeleted() {
    post.setDeleted(true);

    assertThrows(ResourceNotFoundException.class, () -> validator.ensurePostIsNotDeleted(post));
  }

  @Test
  void validateBeforePublish_whenPostPublished() {
    post.setPublished(true);

    assertThrows(PostAlreadyPublishedException.class, () -> validator.validateBeforePublish(post));
  }

  @Test
  void ensurePostIsNotDeleted_whenPostIsNotDeleted() {
    post.setDeleted(false);

    assertDoesNotThrow(() -> validator.ensurePostIsNotDeleted(post));
  }
}