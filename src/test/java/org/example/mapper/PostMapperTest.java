package org.example.mapper;

import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostMapperTest {
  private final PostMapperImpl mapper = new PostMapperImpl();
  private PostContainer container;
  private Post postEntity;
  private PostDto postDto;

  @BeforeEach
  void setUp() {
    container = new PostContainer();
    postDto = container.dto();
    postEntity = container.entity();
  }

  @Test
  void toDto() {
    postEntity.setNumberViews(1L);
    PostDto expected = PostDto.builder()
            .id(postEntity.getId())
            .content(postEntity.getContent())
            .authorId(postEntity.getAuthorId())
            .scheduledAt(postEntity.getScheduledAt())
            .numberViews(postEntity.getNumberViews())
            .build();

    PostDto result = mapper.toDto(postEntity);

    assertEquals(expected, result);
  }

  @Test
  void toEntity() {
    postDto.setNumberViews(1L);
    Post expected = Post.builder()
            .id(null)
            .content(postDto.getContent())
            .authorId(postDto.getAuthorId())
            .scheduledAt(postDto.getScheduledAt())
            .numberViews(0)
            .build();

    Post result = mapper.toEntity(postDto);

    assertEquals(expected, result);
  }

  @Test
  void update() {
    PostDto dto = PostDto.builder()
            .id(postEntity.getId() + 1) // проверка подмены id
            .content("new_content")
            .authorId(postEntity.getAuthorId())
            .scheduledAt(postEntity.getScheduledAt().plusDays(1))
            .numberViews(postEntity.getNumberViews() + 1)
            .build();
    Post expected = container.entity();
    expected.setContent(dto.getContent());
    expected.setScheduledAt(dto.getScheduledAt());

    mapper.update(postEntity, dto);

    assertEquals(expected, postEntity);
  }

  @Test
  void entitiesToDtos() {
    List<Post> posts = container.posts();
    List<PostDto> expected = container.dtos();

    List<PostDto> result = mapper.entitiesToDtos(posts);

    assertEquals(expected, result);
  }
}