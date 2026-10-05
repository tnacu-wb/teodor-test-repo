package uk.co.whitbread.marketing.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class TestCacheUtil {
    @CacheEvict(cacheNames = "marketingToken", key = "'marketingToken'")
    public void deleteCache() {
    }
}
