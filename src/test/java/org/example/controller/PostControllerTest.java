package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dto.PostDto;
import org.example.service.PostService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
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

    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void testCreate() throws Exception {
    // given
    String uri = "/post";
    PostDto requestDto = container.dto();
    requestDto.setId(null);

    PostDto responseDto = container.dto();

    when(service.create(requestDto)).thenReturn(responseDto);

    // then
    mockMvc.perform(post(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(responseDto.getId()))
            .andExpect(jsonPath("$.content").value(responseDto.getContent()));
  }

  @Test
  void testCreateWithAuthorNull() throws Exception {
    String uri = "/post";
    PostDto requestDto = container.dto();
    requestDto.setAuthorId(null);

    mockMvc.perform(post(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateWithContentNull() throws Exception {
    String uri = "/post";
    PostDto requestDto = container.dto();
    requestDto.setContent(null);

    mockMvc.perform(post(uri)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }
}