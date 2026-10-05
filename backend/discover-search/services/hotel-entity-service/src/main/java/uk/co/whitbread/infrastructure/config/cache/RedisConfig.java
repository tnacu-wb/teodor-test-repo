package uk.co.whitbread.infrastructure.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
@Import({DataRedisAutoConfiguration.class})
public class RedisConfig extends CachingConfigurerSupport {

  private static final ObjectMapper REDIS_OBJECT_MAPPER = new ObjectMapper();
  private final String springCacheRedisKeyPrefix;
  private final String contentEntityServiceKeyPrefix;
  private final boolean useRedisKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefixForContentEntityService;

  @Autowired
  public RedisConfig(
      @Value(value = "${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
      @Value(value = "${spring.cache.redis.content-entity-service-key-prefix:}") String contentEntityServiceKeyPrefix,
      @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.contentEntityServiceKeyPrefix = contentEntityServiceKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
  }

  static {
    REDIS_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    REDIS_OBJECT_MAPPER.activateDefaultTyping(REDIS_OBJECT_MAPPER.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL);
    REDIS_OBJECT_MAPPER.registerModule(new JavaTimeModule());
  }

  @PostConstruct
  protected void onPostConstruct() {
    cacheKeyPrefix = getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
    cacheKeyPrefixForContentEntityService = getCacheKeyPrefix(contentEntityServiceKeyPrefix, useRedisKeyPrefix);
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
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager10Minutes")
  public CacheManager cacheManager10Minutes(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofMinutes(10);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
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
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager12Hours")
  public CacheManager cacheManager12Hours(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofHours(12);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean(name = "cacheManager24Hours")
  public CacheManager cacheManager24Hours(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofHours(24);
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
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
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }



  @Bean("hotelAvailabilitiesRedisTemplate")
  RedisTemplate<String, HotelAvailabilityResponse> hotelAvailabilitiesRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, HotelAvailabilityResponse> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER));
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER));
    return template;
  }

  @Bean("hotelAvailabilitiesIndexRedisTemplate")
  RedisTemplate<String, String> hotelAvailabilitiesIndexRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    return template;
  }

  @Bean("hotelStatusesRedisTemplate")
  RedisTemplate<String, HotelStatusDto> hotelStatusesRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, HotelStatusDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER));
    template.afterPropertiesSet();
    return template;
  }

  @Bean("keyPrefix")
  public CacheKeyPrefix getCacheKeyPrefix() {
    return getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
  }

  private static CacheKeyPrefix getCacheKeyPrefix(String prefix, boolean isPrefixingEnabled) {
    return (isPrefixingEnabled && prefix != null && !prefix.trim().isEmpty())
        ? cacheName -> prefix.trim() + "::" + cacheName + "::"
        : CacheKeyPrefix.simple();
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new RedisCacheErrorHandler();
  }

  @Slf4j
  private static class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
      log.error("Unable to get from cache {} for key {}: {}", cache.getName(), exception.getMessage(), key, exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
      log.error("Unable to put into cache {} for key {}: {}", cache.getName(), exception.getMessage(), key, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
      log.error("Unable to evict from cache {} for key {}: {}", cache.getName(), exception.getMessage(), key,
          exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
      log.error("Unable to clean cache {}: {}", cache.getName(), exception.getMessage(), exception);
    }
  }
}
