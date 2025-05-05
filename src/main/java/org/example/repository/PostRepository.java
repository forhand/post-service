package org.example.repository;

import org.example.entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

  @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true ORDER BY p.publishedAt ASC")
  List<Post> findPublishedPostsByAuthor(@Param("authorId") Long authorId);

  @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = false ORDER BY p.createdAt ASC")
  List<Post> findNotPublishedPostsByAuthor(@Param("authorId") Long authorId);

}
