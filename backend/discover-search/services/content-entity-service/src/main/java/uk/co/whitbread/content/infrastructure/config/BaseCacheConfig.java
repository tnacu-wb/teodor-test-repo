package uk.co.whitbread.content.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BaseCacheConfig {

  @Bean
  public Boolean isCacheEnabled(@Value("${spring.cache.type:NONE}") CacheType cacheType) {
    return !CacheType.NONE.equals(cacheType);
  }

  @ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "none")
  public static class NoCacheSetup {

    @Primary
    @Bean(name = "cacheManager")
    public CacheManager dummyCacheManager() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager1Hour")
    public CacheManager dummyCacheManager1Hour() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager1Day")
    public CacheManager dummyCacheManager1Day() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager7Days")
    public CacheManager dummyCacheManager7Days() {
      return new NoOpCacheManager();
    }
  }
}
