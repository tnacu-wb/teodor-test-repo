package uk.co.whitbread.hotelcountries.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import uk.co.whitbread.hotelcountries.config.properties.CacheConfigProperties;
import uk.co.whitbread.hotelcountries.config.properties.CacheConfigProperty;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.Duration.ofSeconds;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Slf4j
@Configuration
@EnableCaching
@RefreshScope
@Profile("!disable-caching")
@RequiredArgsConstructor
@Import({DataRedisAutoConfiguration.class})
@ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "redis")
public class CacheConfig implements CachingConfigurer {

    private final RedisTemplate redisTemplate;
    private final CacheConfigProperties cacheProperties;

    @Value("${cache.default.ttl}")
    private Long expiration;

    /**
     * This is heavily tied to the number of parameters used for hotelData cache keys,
     * it must never add the final parameter of the PUT method. This is because that parameter
     * is used to pass the Object that is to be cached.
     *
     * @return
     */

    @Bean("hotelCountriesKeyGenerator")
    public KeyGenerator hotelCountriesKeyGenerator() {
        return (target, method, params) ->
                String.format("hotelCountries-%s-%s",
                        params[0],
                        params[1]);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        final Map<String, RedisCacheConfiguration> expires = addExpiration(cacheProperties.getCache());

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultRedisCacheConfiguration())
                .withInitialCacheConfigurations(expires).build();
    }


    private Map<String, RedisCacheConfiguration> addExpiration(Map<String, CacheConfigProperty> cacheProperties) {
        Map<String, RedisCacheConfiguration> expires = new ConcurrentHashMap<>(cacheProperties.size());
        cacheProperties
                .forEach((cacheName, cacheConfigProperty) -> expires.put(cacheName, defaultCacheConfig().entryTtl(ofSeconds(cacheConfigProperty.getTtl()))));
        return expires;
    }

    public static class CustomCacheErrorHandler implements CacheErrorHandler {
        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            ignoreDataAccessException(exception);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            ignoreDataAccessException(exception);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            ignoreDataAccessException(exception);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            ignoreDataAccessException(exception);
        }

        private void ignoreDataAccessException(RuntimeException exception) {
            if (DataAccessException.class.isAssignableFrom(exception.getClass())) {
                log.warn("Cached method failed with {}. Calling service.", exception.getMessage());
            } else {
                throw exception;
            }
        }
    }


    private RedisCacheConfiguration defaultRedisCacheConfiguration() {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        Optional.ofNullable(expiration).ifPresent(duration -> defaultCacheConfig.entryTtl(Duration.ofSeconds(expiration)));

        return defaultCacheConfig;
    }
}
