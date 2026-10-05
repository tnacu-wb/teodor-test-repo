package uk.co.whitbread.hotel.info.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
@Profile("!disable-caching")
@Slf4j
public class RedisConfiguration {

    @Value("${cache.default.ttl:300}")
    private Long expiration;

    @Autowired
    private Environment environment;

    @Autowired
    private RedisTemplate redisTemplate;

    @Bean("infoKeyGenerator")
    public KeyGenerator infoKeyGenerator() {
        return (target, method, params) ->
                format("hotelInfoCache-%s-%s-%s-%s", params[0], params[1], params[2], params[3]);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        final Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();
        addExpiration("data", cacheNamesConfigurationMap);
        addExpiration("keys", cacheNamesConfigurationMap);
        addExpiration("allHotels", cacheNamesConfigurationMap);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultRedisCacheConfiguration())
                .withInitialCacheConfigurations(cacheNamesConfigurationMap).build();
    }

    private void addExpiration(String name, Map<String, RedisCacheConfiguration> expires) {
        String cacheName = environment.getProperty("hotel.info." + name + ".cache.name");
        Long expirationProperty = environment.getProperty("hotel.info." + name + ".cache.ttl", Long.class);

        if (StringUtils.isNotBlank(cacheName) && expirationProperty != null) {
            expires.put(cacheName, defaultCacheConfig().entryTtl(ofSeconds(expirationProperty)));
        } else {
            log.error("Failed to add cache expiration for cache with name {} ", name);
        }
    }

    private RedisCacheConfiguration defaultRedisCacheConfiguration() {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        ofNullable(expiration).ifPresent(duration -> defaultCacheConfig.entryTtl(ofSeconds(expiration)));

        return defaultCacheConfig;
    }

}
