package uk.co.whitbread.promo.infrastructure.config;

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

    @Bean(name = "cacheManager5Minutes")
    public CacheManager dummyCacheManager5Minutes() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager10Minutes")
    public CacheManager dummyCacheManager10Minutes() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager30Minutes")
    public CacheManager dummyCacheManager30Minutes() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager12Hours")
    public CacheManager dummyCacheManager12Hours() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManagerForContentEntityService")
    public CacheManager cacheManagerForContentEntityService() {
      return new NoOpCacheManager();
    }
  }
}
