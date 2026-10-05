package uk.co.whitbread.marketing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;

@Slf4j
@Configuration
@EnableCaching
@Profile("!disable-caching")
public class CacheConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
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
