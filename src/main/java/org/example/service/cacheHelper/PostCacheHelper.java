package org.example.service.cacheHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostCacheHelper extends AbstractCacheHelper<Long>{


  public PostCacheHelper(RedisTemplate<String, Object> redisTemplate,
                         @Value("${spring.cache.post.name}") String cacheName) {
    super(redisTemplate, cacheName);
  }

  @Override
  protected String generateCacheKey(Long key) {
    return cacheName + "::" + key;
  }
}
