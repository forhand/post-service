package org.example.util.container;

import org.example.dto.PostDto;
import org.example.entity.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostContainer {
  private Long postId;
  private String content;
  private Long authorId;
  private boolean published;
  private boolean deleted;
  private LocalDateTime publishedAt;
  private LocalDateTime scheduledAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long numberViews;



  public PostContainer() {
    Long id = 0L;
    postId = ++id;
    content = "content";
    authorId = ++id;
    published = false;
    createdAt = LocalDateTime.now();
    updatedAt = createdAt.plusMinutes(30);
    publishedAt = null;
    scheduledAt = createdAt.plusDays(1);
    deleted = false;
    numberViews = 0L;
  }

  public PostDto dto() {
    return PostDto.builder()
            .id(postId)
            .content(content)
            .authorId(authorId)
            .scheduledAt(scheduledAt)
            .numberViews(numberViews)
            .build();
  }

  public Post entity() {
    return Post.builder()
            .id(postId)
            .content(content)
            .authorId(authorId)
            .publishedAt(publishedAt)
            .scheduledAt(scheduledAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .numberViews(numberViews)
            .build();
  }

  public Long postId() {
    return postId;
  }

  public Long authorId() {
    return authorId;
  }

  public String content() {
    return content;
  }

  public LocalDateTime publishedAt() {
    return publishedAt;
  }

  public boolean published() {
    return published;
  }

  public boolean deleted() {
    return deleted;
  }

  public LocalDateTime updatedAt() {
    return updatedAt;
  }

  public List<PostDto> dtos() {
    PostDto dto1 = dto();
    dto1.setPublished(true);
    PostDto dto2 = dto();
    dto2.setPublished(true);
    dto2.setId(dto1.getId()+1);
    return new ArrayList<>(List.of(dto1, dto2));
  }

  public List<Post> drafts() {
    Post post1 = entity();
    Post post2 = entity();
    post2.setId(post1.getId()+1);
    return new ArrayList<>(List.of(post1, post2));
  }

  public List<Post> posts() {
    Post post1 = entity();
    post1.setPublished(true);
    Post post2 = entity();
    post2.setPublished(true);
    post2.setId(post1.getId()+1);
    return new ArrayList<>(List.of(post1, post2));
  }

  public Post publishedPost() {
    return Post.builder()
            .id(postId)
            .content(content)
            .authorId(authorId)
            .published(true)
            .publishedAt(LocalDateTime.now())
            .scheduledAt(null)
            .createdAt(createdAt)
            .updatedAt(this.publishedAt)
            .numberViews(numberViews)
            .build();
  }
}

