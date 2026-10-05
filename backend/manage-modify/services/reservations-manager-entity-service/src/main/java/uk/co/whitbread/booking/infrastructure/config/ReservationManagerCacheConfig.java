package uk.co.whitbread.booking.infrastructure.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.cfg.MapperBuilder;

@Configuration
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
@Import({DataRedisAutoConfiguration.class})
@EnableCaching
public class ReservationManagerCacheConfig {

  public static final String RESERVATION_MANAGER_SERVICE = "Reservation-Manager-service";

  private CacheKeyPrefix cacheKeyPrefix;

  @PostConstruct
  private void onPostConstruct() {
    this.cacheKeyPrefix = cacheName -> RESERVATION_MANAGER_SERVICE + "::" + cacheName + "::";
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
                    GenericJacksonJsonRedisSerializer.builder()
                        .enableUnsafeDefaultTyping()
                        .customize(MapperBuilder::findAndAddModules)
                        .build())))
        .build();
  }

}
