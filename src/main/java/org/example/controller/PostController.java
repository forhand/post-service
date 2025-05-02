package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.PostDto;
import org.example.handler.exception.PostAlreadyPublishedException;
import org.example.service.PostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @PostMapping()
  public PostDto create(@Valid @RequestBody PostDto postDto) {
    return postService.create(postDto);
  }

  @PostMapping("/publish")
  public PostDto publish(@Valid @RequestBody PostDto postDto) {
    if (postDto.isPublished()) {
      throw new PostAlreadyPublishedException();
    }
    return postService.publish(postDto);
  }

  //TODO: Implement soft delete
  //TODO: Implement update
  //TODO: Implement getPostById
  //TODO: Implement getAllNotPublished
  //TODO: Implement getAllByAuthor
  //TODO: Implement getAllNotDeleted
}
