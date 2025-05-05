package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.dto.PostDto;
import org.example.service.post.PostService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @PostMapping()
  public PostDto create(@RequestBody @NotNull @Valid PostDto postDto) {
    return postService.create(postDto);
  }

  @PatchMapping("/{postId}/publish")
  public PostDto publish(@PathVariable("postId") @Positive Long postId) {
    return postService.publish(postId);
  }

  @DeleteMapping("/{postId}")
  public void delete(@PathVariable("postId") @Positive Long postId) {
    postService.delete(postId);
  }

  @PatchMapping("/{postId}")
  public PostDto update(@PathVariable("postId") @Positive Long postId,
                        @RequestBody @Valid PostDto postDto) {
    return postService.update(postId, postDto);
  }

  @GetMapping("/{postId}")
  public PostDto get(@PathVariable("postId") @Positive Long postId) {
    return postService.get(postId);
  }

  @GetMapping("/all_drafts/{authorId}")
  public List<PostDto> getAllNotPublishedNotDeleted(@PathVariable("authorId") Long authorId) {
    return postService.getAllNotPublishedNotDeleted(authorId);
  }

  @GetMapping("/all_published/{authorId}")
  public List<PostDto> getAllPublishedNotDeleted(@PathVariable("authorId") @Positive Long authorId) {
    return postService.getAllPublishedNotDeleted(authorId);
  }

}
