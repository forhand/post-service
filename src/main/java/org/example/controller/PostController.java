package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.PostService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;
}
