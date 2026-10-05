package uk.co.whitbread.contentservice.roomtypes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import uk.co.whitbread.contentservice.roomtypes.config.properties.CacheConfigProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.CacheConfigProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
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
    private final ObjectMapper objectMapper;
    private final CacheConfigProperties cacheProperties;

    @Value("${cache.default.ttl}")
    private Long expiration;


    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        final Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();
        final Map<String, Long> expires = addExpiration(cacheProperties.getCache());

        expires.forEach((cacheName, ttl) -> addExpiration(
                cacheName, ttl, cacheNamesConfigurationMap));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultRedisCacheConfiguration())
                .withInitialCacheConfigurations(cacheNamesConfigurationMap).build();
    }

    private RedisCacheConfiguration defaultRedisCacheConfiguration() {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        ofNullable(expiration).ifPresent(duration -> defaultCacheConfig.entryTtl(ofSeconds(expiration)));

        return defaultCacheConfig;
    }

    private void addExpiration(String cacheName, Long expirationProperty, Map<String, RedisCacheConfiguration> expires) {

        if (StringUtils.isNotBlank(cacheName) && expirationProperty != null) {
            expires.put(cacheName, defaultCacheConfig().entryTtl(ofSeconds(expirationProperty)));
        } else {
            log.error("Failed to add cache expiration for cache with name {} ", cacheName);
        }
    }

    private Map<String, Long> addExpiration(Map<String, CacheConfigProperty> cacheProperties) {
        Map<String, Long> expires = new ConcurrentHashMap<>(cacheProperties.size());
        cacheProperties
                .forEach((cacheName, cacheConfigProperty) -> expires.put(cacheName, cacheConfigProperty.getTtl()));
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
}
