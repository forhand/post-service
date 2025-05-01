package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "content", nullable = false, length = 4096)
  private String content;

  @Column(name = "author_id")
  private Long authorId;

  @Column(name = "published", nullable = false)
  private boolean published;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "scheduled_at")
  private LocalDateTime scheduledAt;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "number_views")
  private Long numberViews;
}
