package uk.co.whitbread.reservation.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@EnableCaching
public class BaseCacheConfig {

  @Configuration
  @ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "none")
  public static class NoCacheSetup {

    @Primary
    @Bean(name = "cacheManager")
    public CacheManager dummyCacheManager() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager30Min")
    public CacheManager dummyCacheManager30Min() {
      return new NoOpCacheManager();
    }
  }
}

