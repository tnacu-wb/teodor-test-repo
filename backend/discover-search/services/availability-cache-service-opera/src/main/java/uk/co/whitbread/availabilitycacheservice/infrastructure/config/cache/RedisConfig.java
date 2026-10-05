package uk.co.whitbread.availabilitycacheservice.infrastructure.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
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
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
@Import({DataRedisAutoConfiguration.class})
public class RedisConfig {

  private static final GenericJacksonJsonRedisSerializer REDIS_JSON_SERIALIZER =
      GenericJacksonJsonRedisSerializer.builder()
          .customize(builder -> builder
              .activateDefaultTyping(
                  BasicPolymorphicTypeValidator.builder()
                      .allowIfSubType("uk.co.whitbread.availabilitycacheservice.")
                      .allowIfSubType("uk.co.whitbread.content.")
                      .allowIfSubType("java.util.")
                      .allowIfSubType("java.math.")
                      .build(),
                  DefaultTyping.NON_FINAL)
              .changeDefaultVisibility(vc ->
                  vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
              .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
              .findAndAddModules())
          .build();
  private final String springCacheRedisKeyPrefix;
  private final String springCachePrefixForContentEntityService;
  private final boolean useRedisKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefixForContentEntity;

  public RedisConfig(@Value(value = "${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
      @Value(value = "${spring.cache.redis.content-entity-service-key-prefix:}")
      String springCachePrefixForContentEntityService,
      @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
    this.springCachePrefixForContentEntityService = springCachePrefixForContentEntityService;
  }

  @PostConstruct
  private void onPostConstruct() {
    cacheKeyPrefix = getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
    cacheKeyPrefixForContentEntity = getCacheKeyPrefix(springCachePrefixForContentEntityService, useRedisKeyPrefix);
  }

  @Primary
  @Bean(name = "cacheManager")
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(Duration.ofMinutes(30))  // default TTL prevents unbounded cache growth in Redis
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(REDIS_JSON_SERIALIZER)))
        .build();
  }

  @Bean(name = "cacheManager5Minutes")
  public CacheManager cacheManager5Minutes(RedisConnectionFactory redisConnectionFactory) {
    return buildCacheManager(redisConnectionFactory, Duration.ofMinutes(5));
  }

  @Bean(name = "cacheManager1Hour")
  public CacheManager cacheManager1Hour(RedisConnectionFactory redisConnectionFactory) {
    return buildCacheManager(redisConnectionFactory, Duration.ofHours(1));
  }

  @Bean(name = "cacheManager12Hours")
  public CacheManager cacheManager12Houts(RedisConnectionFactory redisConnectionFactory) {
    return buildCacheManager(redisConnectionFactory, Duration.ofHours(12));
  }

  private @NotNull RedisCacheManager buildCacheManager(RedisConnectionFactory redisConnectionFactory,
      Duration expiration) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(REDIS_JSON_SERIALIZER)))
        .build();
  }

  @Bean(name = "cacheManagerForContentEntityService")
  public CacheManager cacheManagerForContentEntityService(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefixForContentEntity)
            .entryTtl(Duration.ofMinutes(30))  // default TTL prevents unbounded cache growth in Redis
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(REDIS_JSON_SERIALIZER)))
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
