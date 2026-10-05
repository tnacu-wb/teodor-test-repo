package uk.co.whitbread.shared.cdh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseCacheConfig {
  @Bean
  public Boolean isCacheEnabled(@Value("${spring.cache.type:NONE}") CacheType cacheType) {
    return !CacheType.NONE.equals(cacheType);
  }

  @ConditionalOnProperty(prefix = "spring", name = "cache.type", havingValue = "none")
  public static class NoCacheSetup {


    @Bean(name = "cacheManager1HourCdh")
    public CacheManager dummyCacheManager1Hour() {
      return new NoOpCacheManager();
    }

  }
}
