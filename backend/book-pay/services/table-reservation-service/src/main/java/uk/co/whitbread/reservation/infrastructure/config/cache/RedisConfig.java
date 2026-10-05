package uk.co.whitbread.reservation.infrastructure.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
@Import({DataRedisAutoConfiguration.class})
public class RedisConfig implements CachingConfigurer {

  private static final GenericJacksonJsonRedisSerializer REDIS_JSON_SERIALIZER =
      GenericJacksonJsonRedisSerializer.builder()
          .customize(builder -> builder
              .activateDefaultTyping(
                  BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                  DefaultTyping.NON_FINAL)
              .changeDefaultVisibility(vc ->
                  vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
              .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
              .findAndAddModules())
          .build();

  private final String springCacheRedisKeyPrefix;
  private final boolean useRedisKeyPrefix;
  private final Duration ttl;
  private CacheKeyPrefix cacheKeyPrefix;

  @Autowired
  public RedisConfig(
      @Value(value = "${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
      @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix,
      @Value("${spring.cache.redis.time-to-live:60m}") Duration ttl) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
    this.ttl = ttl;
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
            .entryTtl(ttl)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(REDIS_JSON_SERIALIZER)))
        .build();
  }

  @Bean(name = "cacheManager30Min")
  public CacheManager cacheManager30Min(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(Duration.ofMinutes(30))
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(REDIS_JSON_SERIALIZER)))
        .build();
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new RedisCacheErrorHandler();
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
          exception.getMessage(), key, exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
      log.error("Unable to clean cache {}: {}", cache.getName(), exception.getMessage(),
          exception);
    }
  }
}
