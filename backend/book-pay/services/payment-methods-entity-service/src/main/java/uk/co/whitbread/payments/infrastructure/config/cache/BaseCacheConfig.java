package uk.co.whitbread.payments.infrastructure.config.cache;

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

    @Bean(name = "cacheManager30Minutes")
    public CacheManager dummyCacheManager30Minutes() {
      return new NoOpCacheManager();
    }

    @Bean(name = "cacheManager30MinutesNoTyping")
    public CacheManager dummyCacheManager30MinutesNoTyping() {
      return new NoOpCacheManager();
    }

    @Bean(name = "rawCacheManager")
    public CacheManager dummyRawCacheManager() {
      return new NoOpCacheManager();
    }
  }
}