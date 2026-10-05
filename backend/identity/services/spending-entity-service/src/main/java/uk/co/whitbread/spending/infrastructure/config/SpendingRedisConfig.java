package uk.co.whitbread.spending.infrastructure.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SpendingRedisConfig {

  private final String springCacheRedisKeyPrefix;
  private final boolean useRedisKeyPrefix;
  private CacheKeyPrefix cacheKeyPrefix;

  @Autowired
  public SpendingRedisConfig(@Value(value = "${spring.cache.redis.key-prefix:}") String springCacheRedisKeyPrefix,
      @Value("${spring.cache.redis.use-key-prefix:false}") boolean useRedisKeyPrefix) {
    this.springCacheRedisKeyPrefix = springCacheRedisKeyPrefix;
    this.useRedisKeyPrefix = useRedisKeyPrefix;
  }

  @PostConstruct
  protected void onPostConstruct() {
    cacheKeyPrefix = getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
  }

  @Bean(name = "cacheManager1Hour")
  @ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
  public CacheManager cacheManager1Hour(RedisConnectionFactory redisConnectionFactory) {
    Duration expiration = Duration.ofHours(1);
    GenericJacksonJsonRedisSerializer serializer =
        GenericJacksonJsonRedisSerializer.create(configurer -> configurer
            .enableUnsafeDefaultTyping()
            .customize(tools.jackson.databind.cfg.MapperBuilder::findAndAddModules));

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheKeyPrefix)
            .entryTtl(expiration)
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(serializer)))
        .build();
  }

  @Bean(name = "cacheManager1Hour")
  @Primary
  @ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "none")
  public CacheManager noOpCacheManager1Hour() {
    return new NoOpCacheManager();
  }

  private static CacheKeyPrefix getCacheKeyPrefix(String prefix, boolean isPrefixingEnabled) {
    return (isPrefixingEnabled && prefix != null && !prefix.trim().isEmpty())
        ? cacheName -> prefix.trim() + "::" + cacheName + "::"
        : CacheKeyPrefix.simple();
  }
}
