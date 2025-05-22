package org.example.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.PostDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final String key;
  private final ObjectMapper objectMapper;

  public CacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
    this.key = "post";
  }

  public void setCache(PostDto postDto) {
    redisTemplate.opsForValue().set(key + postDto.getId(), postDto);
  }

  public PostDto getCache(Long id) {
    if (Boolean.TRUE.equals(redisTemplate.hasKey(key + id))) {
      return objectMapper.convertValue(redisTemplate.opsForValue().get(key + id), PostDto.class);
    } else {
      return null;
    }
  }

  public void deleteCache(Long id) {
    if (Boolean.TRUE.equals(redisTemplate.hasKey(key + id))) {
      redisTemplate.delete(key + id);
    }
  }

  public boolean hasCache(Long id) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key + id));
  }

}
