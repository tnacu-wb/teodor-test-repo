package uk.co.whitbread.shared.cdh.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
public class RedisConfig {

  private final String springCacheRedisKeyPrefix;
  private final boolean useRedisKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefix;

  @Autowired
  public RedisConfig(@Value(value = "${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
      @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
  }

  @PostConstruct
  private void onPostConstruct() {
    cacheKeyPrefix = getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
  }

  @Bean
  public CacheErrorHandler errorHandler() {
    return new RedisCacheErrorHandler();
  }

  @Bean
  public ObjectMapper redisObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  @Primary
  @Bean(name = "cacheManager")
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, 
      ObjectMapper redisObjectMapper) {
    RedisSerializer<Object> serializer = redisValueSerializer(redisObjectMapper);
    
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(serializer)))
        .build();
  }

  @Bean(name = "cacheManager1HourCdh")
  public CacheManager cacheManager1Hour(RedisConnectionFactory redisConnectionFactory,
      ObjectMapper redisObjectMapper) {
    Duration expiration = Duration.ofHours(1);
    RedisSerializer<Object> serializer = redisValueSerializer(redisObjectMapper);
    
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(serializer)))
        .build();
  }

  private RedisSerializer<Object> redisValueSerializer(ObjectMapper redisObjectMapper) {
    return new RedisSerializer<>() {
      @Override
      public byte[] serialize(Object value) throws SerializationException {
        try {
          return redisObjectMapper.writeValueAsBytes(value);
        } catch (IOException exception) {
          throw new SerializationException("Unable to serialize Redis cache value", exception);
        }
      }

      @Override
      public Object deserialize(byte[] bytes) throws SerializationException {
        try {
          return bytes == null ? null : redisObjectMapper.readValue(bytes, Object.class);
        } catch (IOException exception) {
          throw new SerializationException("Unable to deserialize Redis cache value", exception);
        }
      }
    };
  }

  private static CacheKeyPrefix getCacheKeyPrefix(String prefix, boolean isPrefixingEnabled) {
    return (isPrefixingEnabled && prefix != null && !prefix.trim().isEmpty())
        ? cacheName -> prefix.trim() + "::" + cacheName + "::"
        : CacheKeyPrefix.simple();
  }

  @Slf4j
  private static class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
      log.error("Unable to get from cache {} for key {}: {}", cache.getName(),
          exception.getMessage(), key, exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key,
        Object value) {
      log.error("Unable to put into cache {} for key {}: {}", cache.getName(),
          exception.getMessage(), key, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
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
