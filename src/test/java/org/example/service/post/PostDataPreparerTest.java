package org.example.service.post;

import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostDataPreparerTest {
  private final PostDataPreparer preparer = new PostDataPreparer();
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
  void testPreparePostForPublishing() {
    Post actual = preparer.preparePostForPublishing(post);

    assertTrue(actual.isPublished());
    assertNull(actual.getScheduledAt());
    assertNotNull(actual.getPublishedAt());
  }

  @Test
  void testPreparePostForDeleting() {
    Post actual = preparer.preparePostForDeleting(post);

    assertFalse(actual.isPublished());
    assertTrue(actual.isDeleted());
  }

  @Test
  void preparePostDtoForUpdating() {
    PostDto actual = preparer.preparePostDtoForUpdating(postDto, post);

    assertEquals(postDto, actual);
  }

  @Test
  void testPreparePostDtoForUpdating_schedulePublishedPost() {
    Post publishedPost = preparer.preparePostForPublishing(post);

    PostDto actual = preparer.preparePostDtoForUpdating(postDto, publishedPost);

    assertNull(actual.getScheduledAt());
  }
}