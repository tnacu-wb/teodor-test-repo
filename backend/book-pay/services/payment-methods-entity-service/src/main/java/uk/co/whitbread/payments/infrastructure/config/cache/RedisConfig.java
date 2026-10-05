package uk.co.whitbread.payments.infrastructure.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
@Import({DataRedisAutoConfiguration.class})
public class RedisConfig {

  private static final ObjectMapper REDIS_OBJECT_MAPPER = new ObjectMapper();
  private static final ObjectMapper REDIS_OBJECT_MAPPER_NO_TYPING = new ObjectMapper();
  private final String springCacheRedisKeyPrefix;
  private final boolean useRedisKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefix;

  static {
    REDIS_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    REDIS_OBJECT_MAPPER.activateDefaultTyping(REDIS_OBJECT_MAPPER.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL);

    REDIS_OBJECT_MAPPER_NO_TYPING.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
  }

  public RedisConfig(@Value("${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
                     @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
  }

  @PostConstruct
  private void onPostConstruct() {
    cacheKeyPrefix = getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
  }

  @Primary
  @Bean(name = "cacheManager")
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager30Minutes")
  public CacheManager cacheManager30Minutes(RedisConnectionFactory redisConnectionFactory) {
    return buildCacheManager(redisConnectionFactory, Duration.ofMinutes(30), REDIS_OBJECT_MAPPER);
  }

  @Bean(name = "cacheManager30MinutesNoTyping")
  public CacheManager cacheManager30MinutesNoTyping(RedisConnectionFactory redisConnectionFactory) {
    return buildCacheManager(redisConnectionFactory, Duration.ofMinutes(30), REDIS_OBJECT_MAPPER_NO_TYPING);
  }

  @Bean(name = "rawCacheManager")
  public CacheManager rawCacheManager(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(new StringRedisSerializer())))
        .build();
  }

  private @NotNull RedisCacheManager buildCacheManager(RedisConnectionFactory redisConnectionFactory,
      Duration expiration, ObjectMapper objectMapper) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))))
        .build();
  }

  private static CacheKeyPrefix getCacheKeyPrefix(String prefix, boolean isPrefixingEnabled) {
    return (isPrefixingEnabled && StringUtils.isNotBlank(prefix))
        ? cacheName -> prefix.trim() + "::" + cacheName + "::"
        : CacheKeyPrefix.simple();
  }

  @Bean
  public CacheErrorHandler errorHandler() {
    return new RedisCacheErrorHandler();
  }

  @Slf4j
  private static class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(@NotNull RuntimeException exception, Cache cache, @NotNull Object key) {
      log.error("Unable to get from cache {} for key {}: {}", cache.getName(),
          exception.getMessage(), key, exception);
    }

    @Override
    public void handleCachePutError(@NotNull RuntimeException exception, Cache cache, @NotNull Object key,
                                    Object value) {
      log.error("Unable to put into cache {} for key {}: {}", cache.getName(),
          exception.getMessage(), key, exception);
    }

    @Override
    public void handleCacheEvictError(@NotNull RuntimeException exception, Cache cache, @NotNull Object key) {
      log.error("Unable to evict from cache {} for key {}: {}", cache.getName(),
          exception.getMessage(), key,
          exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
      log.error("Unable to clean cache {}: {}", cache.getName(), exception.getMessage(), exception);
    }
  }
}