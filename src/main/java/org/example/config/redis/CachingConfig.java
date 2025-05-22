package org.example.config.redis;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class CachingConfig {
  @Value("${spring.cache.post.ttl.duration.seconds:10}")
  private int ttlPost ;

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(ttlPost))
            .enableTimeToIdle();

    return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaults)
            .withInitialCacheConfigurations(Collections.singletonMap("post",
                    RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()))
            .build();
  }
}