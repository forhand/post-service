package org.example.util.container;

import org.example.dto.PostDto;
import org.example.entity.Post;

import java.time.LocalDateTime;

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
        publishedAt = createdAt.plusDays(1);
        scheduledAt = createdAt.plusDays(2);
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
                .published(published)
                .publishedAt(publishedAt)
                .scheduledAt(scheduledAt)
                .deleted(deleted)
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
}
