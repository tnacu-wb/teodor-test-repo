package uk.co.whitbread.infrastructure.rest.client.cache.rediscacheIT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;
import redis.embedded.RedisServer;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;

@TestConfiguration
@Profile("test")
@EnableCaching
public class CdhAdapterClientCacheTestConfig {

  private static final int REDIS_PORT = 6380;
  private static final ObjectMapper REDIS_OBJECT_MAPPER = new ObjectMapper();

  static {
    REDIS_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    REDIS_OBJECT_MAPPER.activateDefaultTyping(
        REDIS_OBJECT_MAPPER.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL);
  }

  @SneakyThrows
  @Bean(initMethod = "start", destroyMethod = "stop")
  public RedisServer cdhCacheRedisServer() {
    return new RedisServer(REDIS_PORT);
  }

  @Bean
  public LettuceConnectionFactory cdhCacheConnectionFactory(RedisServer cdhCacheRedisServer) {
    return new LettuceConnectionFactory("localhost", REDIS_PORT);
  }

  @Bean(name = "cacheManager24Hours")
  public CacheManager cacheManager24Hours(RedisConnectionFactory cdhCacheConnectionFactory) {
    return RedisCacheManager.builder(cdhCacheConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24))
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Primary
  @Bean(name = "cacheManager5Minutes")
  public CacheManager cacheManager5Minutes(RedisConnectionFactory cdhCacheConnectionFactory) {
    return RedisCacheManager.builder(cdhCacheConnectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER))))
        .build();
  }

  @Bean("isCacheEnabled")
  public Boolean isCacheEnabled() {
    return Boolean.TRUE;
  }

  @Bean("cdhAdapterWebClient")
  public WebClient cdhAdapterWebClient() {
    return Mockito.mock(WebClient.class);
  }

  @Bean
  public CdhAdapterProperties cdhAdapterProperties() {
    CdhAdapterProperties props = new CdhAdapterProperties();
    props.setGetCompanySuppressRatesEndpoint("/companies/{companyId}/suppress-rates");
    props.setCompaniesSearchEndpoint("/v1/cdh/account/company/search");
    return props;
  }
}
