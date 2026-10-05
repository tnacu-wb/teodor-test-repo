package uk.co.whitbread.reservation.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import uk.co.whitbread.reservation.domain.model.cache.HotelReservationCache;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
@Import({DataRedisAutoConfiguration.class})
public class RedisConfig implements CachingConfigurer {

  private static final JsonMapper RESERVATION_REDIS_OBJECT_MAPPER = JsonMapper.builder()
      .changeDefaultVisibility(vc -> vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
      .findAndAddModules()
      .build();

  private static final JsonMapper REDIS_OBJECT_MAPPER = JsonMapper.builder()
      .changeDefaultVisibility(vc -> vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
      .activateDefaultTyping(
          BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
          DefaultTyping.NON_FINAL)
      .findAndAddModules()
      .build();
  private final String springCachePrefixForContentEntityService;
  private final String springCacheRedisKeyPrefix;
  private final boolean useRedisKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefixForContentEntityService;

  public RedisConfig(@Value(value = "${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
                     @Value(value = "Content-Entity-Service") String springCachePrefixForContentEntityService,
                     @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
    this.springCachePrefixForContentEntityService = springCachePrefixForContentEntityService;
  }

  @PostConstruct
  private void onPostConstruct() {
    cacheKeyPrefix = getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
    cacheKeyPrefixForContentEntityService = getCacheKeyPrefix(
        springCachePrefixForContentEntityService, useRedisKeyPrefix);
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
                SerializationPair.fromSerializer(
                    new GenericJacksonJsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager5Minutes")
  public CacheManager cacheManager5Minutes(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofMinutes(5);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJacksonJsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "reservationCacheManager30Minutes")
  public CacheManager reservationCacheManager30Minutes(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofMinutes(30);
    JacksonJsonRedisSerializer<HotelReservationCache> reservationCacheSerializer =
        new JacksonJsonRedisSerializer<>(
            RESERVATION_REDIS_OBJECT_MAPPER, HotelReservationCache.class);

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(
                    reservationCacheSerializer)))
        .build();
  }

  @Bean(name = "cacheManager1Hour")
  public CacheManager cacheManager1Hour(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofHours(1);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(
                    new GenericJacksonJsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager1Day")
  public CacheManager cacheManager1Day(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofDays(1);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(
                    new GenericJacksonJsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager7Days")
  public CacheManager cacheManager7Days(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofDays(7);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(
                    new GenericJacksonJsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManagerForContentEntityService")
  public CacheManager cacheManagerForContentEntityService(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefixForContentEntityService)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJacksonJsonRedisSerializer(REDIS_OBJECT_MAPPER))))
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
          exception.getMessage(), key,
          exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
      log.error("Unable to clean cache {}: {}", cache.getName(), exception.getMessage(), exception);
    }
  }
}
