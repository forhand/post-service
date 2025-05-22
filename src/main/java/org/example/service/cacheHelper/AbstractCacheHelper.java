package org.example.service.cacheHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCacheHelper<T> {
  protected final RedisTemplate<String, Object> redisTemplate;
  protected final String cacheName;

  // Универсальный метод проверки наличия объекта в кэше
  public boolean existsInCache(T key) {
    String cacheKey = generateCacheKey(key);
    log.info("Checking cache for key: {}. {}", cacheKey, redisTemplate.hasKey(cacheKey));
    log.info("Cache: {}", redisTemplate.keys("*").toString());
    return redisTemplate.hasKey(cacheKey);
  }

  // Переопределяемый метод для формирования уникального ключа
  protected abstract String generateCacheKey(T key);
}
