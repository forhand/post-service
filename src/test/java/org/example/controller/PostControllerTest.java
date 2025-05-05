package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dto.PostDto;
import org.example.handler.GlobalExceptionHandler;
import org.example.handler.exception.AuthorChangeForbiddenException;
import org.example.handler.exception.PostAlreadyPublishedException;
import org.example.handler.exception.ResourceNotFoundException;
import org.example.service.post.PostService;
import org.example.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
  private final String uri = "/api/posts";
  @Mock
  private PostService service;
  @InjectMocks
  private PostController controller;
  private ObjectMapper objectMapper;
  private MockMvc mockMvc;
  private PostContainer container;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    container = new PostContainer();

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .build();
  }

  @Test
  void testCreate() throws Exception {
    PostDto requestDto = container.dto();
    requestDto.setId(null);
    PostDto responseDto = container.dto();
    when(service.create(requestDto)).thenReturn(responseDto);

    mockMvc.perform(post(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(responseDto.getId()))
            .andExpect(jsonPath("$.content").value(responseDto.getContent()));
  }

  @Test
  void testCreateWithAuthorNull() throws Exception {
    PostDto requestDto = container.dto();
    requestDto.setAuthorId(null);

    mockMvc.perform(post(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateWithContentNull() throws Exception {
    PostDto requestDto = container.dto();
    requestDto.setContent(null);

    mockMvc.perform(post(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  void testPublish() throws Exception {
    String uri = "%s/%d/publish".formatted(this.uri, container.postId());
    PostDto responseDto = container.dto();

    when(service.publish(container.postId())).thenReturn(responseDto);

    mockMvc.perform(patch(uri))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(responseDto.getId()));
  }

  @Test
  void testPublishAlreadyPublished() throws Exception {
    String uri = "%s/%d/publish".formatted(this.uri, container.postId());
    when(service.publish(any())).thenThrow(new PostAlreadyPublishedException(""));

    mockMvc.perform(patch(uri))
            .andExpect(status().isBadRequest());
  }

  @Test
  void testPublishDeletedPost() throws Exception {
    String uri = "%s/%d/publish".formatted(this.uri, container.postId());
    when(service.publish(any())).thenThrow(new ResourceNotFoundException(""));

    mockMvc.perform(patch(uri))
            .andExpect(status().isNotFound());
  }

  @Test
  void testDelete() throws Exception {
    String uri = "%s/%d".formatted(this.uri, container.postId());

    mockMvc.perform(delete(uri))
            .andExpect(status().isOk());
  }

  @Test
  void testUpdate() throws Exception {
    String uri = "%s/%d".formatted(this.uri, container.postId());
    PostDto requestDto = container.dto();
    PostDto responseDto = container.dto();
    requestDto.setContent("updated");
    when(service.update(container.postId(), requestDto)).thenReturn(responseDto);

    mockMvc.perform(patch(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(responseDto.getContent()));
  }

  @Test
  void testUpdateWithChangedAuthor() throws Exception {
    String uri = "%s/%d".formatted(this.uri, container.postId());
    PostDto requestDto = container.dto();
    when(service.update(any(), any())).thenThrow(new AuthorChangeForbiddenException(""));

    mockMvc.perform(patch(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateDeletedPost() throws Exception {
    String uri = "%s/%d".formatted(this.uri, container.postId());
    PostDto requestDto = container.dto();
    when(service.update(any(), any())).thenThrow(new ResourceNotFoundException(""));

    mockMvc.perform(patch(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  void testGet() throws Exception {
    String uri = "%s/%d".formatted(this.uri, container.postId());
    PostDto responseDto = container.dto();
    when(service.get(container.postId())).thenReturn(responseDto);

    mockMvc.perform(get(uri))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(responseDto.getId()));
  }

  @Test
  void testGetPostThrowsNotFoundException() throws Exception {
    String uri = "%s/%d".formatted(this.uri, container.postId());
    when(service.get(container.postId())).thenThrow(new ResourceNotFoundException(""));

    mockMvc.perform(get(uri))
            .andExpect(status().isNotFound());
  }

  @Test
  void getAllNotPublishedNotDeleted() throws Exception {
    String uri = "%s/all_drafts/%d".formatted(this.uri, container.authorId());
    List<PostDto> responseDtos = container.dtos();
    when(service.getAllNotPublishedNotDeleted(any())).thenReturn(responseDtos);

    mockMvc.perform(get(uri))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(responseDtos.size())))
            .andExpect(jsonPath("$.[0].id").value(responseDtos.get(0).getId()));
  }

  @Test
  void getAllPublishedNotDeleted() throws Exception {
    String uri = "%s/all_published/%d".formatted(this.uri, container.authorId());
    List<PostDto> responseDtos = container.dtos();
    when(service.getAllPublishedNotDeleted(any())).thenReturn(responseDtos);

    mockMvc.perform(get(uri))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(responseDtos.size())))
            .andExpect(jsonPath("$.[0].id").value(responseDtos.get(0).getId()));
  }
}