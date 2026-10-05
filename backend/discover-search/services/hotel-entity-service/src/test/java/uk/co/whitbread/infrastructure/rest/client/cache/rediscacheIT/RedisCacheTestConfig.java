package uk.co.whitbread.infrastructure.rest.client.cache.rediscacheIT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;

@TestConfiguration
@Profile("test")
@EnableRedisRepositories
public class RedisCacheTestConfig {
  private static final ObjectMapper REDIS_OBJECT_MAPPER = new ObjectMapper();
  private final String springCacheRedisKeyPrefix;
  private final boolean useRedisKeyPrefix;

  public RedisCacheTestConfig() {
    this.springCacheRedisKeyPrefix = "Hotel-Entity-Service";
    this.useRedisKeyPrefix = true;
  }

  static {
    REDIS_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    REDIS_OBJECT_MAPPER.activateDefaultTyping(REDIS_OBJECT_MAPPER.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL);
  }

  private static CacheKeyPrefix getCacheKeyPrefix(String prefix, boolean isPrefixingEnabled) {
    return (isPrefixingEnabled && prefix != null && !prefix.trim().isEmpty())
        ? cacheName -> prefix.trim() + "::" + cacheName + "::"
        : CacheKeyPrefix.simple();
  }

  @Bean
  public RedisProperties redisProperties() {
    return new RedisProperties("localhost", 6379);
  }

  @SneakyThrows
  @Bean(initMethod = "start", destroyMethod = "stop")
  public RedisServer redisServer(RedisProperties redisProperties) {
    return new RedisServer(redisProperties.port());
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory(
      RedisProperties redisProperties) {
    return new LettuceConnectionFactory(redisProperties.host(), redisProperties.port());
  }

  @Bean
  RedisTemplate<String, HotelStatusDto> redisTemplate(
      LettuceConnectionFactory connectionFactory) {

    RedisTemplate<String, HotelStatusDto> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER));
    template.afterPropertiesSet();
    return template;
  }

  @Bean("keyPrefix")
  public CacheKeyPrefix getCacheKeyPrefix() {
    return getCacheKeyPrefix(springCacheRedisKeyPrefix, useRedisKeyPrefix);
  }

  public record RedisProperties(String host, int port) {}
}