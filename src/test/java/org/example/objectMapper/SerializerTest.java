package org.example.objectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dto.PostDto;
import org.example.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTest {
  private ObjectMapper objectMapper;
  private PostContainer postContainer;
  private PostDto postDto;

 @BeforeEach
  public void setUp() {
   objectMapper = new ObjectMapper();
   objectMapper.registerModule(new JavaTimeModule());

   postContainer = new PostContainer();
   postDto = postContainer.dto();
 }

 @Test
  public void testSerialize() throws JsonProcessingException {
   String json = objectMapper.writeValueAsString(postDto);
   PostDto actualDto = objectMapper.readValue(json, PostDto.class);

   assertEquals(postDto, actualDto);
 }


}
