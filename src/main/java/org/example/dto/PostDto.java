package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
  private Long id;
  @NotBlank(message = "Content is not empty")
  private String content;
  @NotNull(message = "Author is not empty")
  private Long authorId;
  private LocalDateTime scheduledAt;
  private long numberViews;
}
