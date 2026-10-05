package uk.co.whitbread.content.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("noCachePrefix")
public class RedisConfigNoPrefixTest {

  private final String EXPECTED_KEY_PATTERN = "%s::";
  private final String CACHE_NAME = "testCache";

  @Autowired
  @Qualifier("cacheManager")
  private RedisCacheManager cacheManager;

  @Autowired
  @Qualifier("cacheManager1Hour")
  private RedisCacheManager cacheManager1Hour;

  @Autowired
  @Qualifier("cacheManager1Day")
  private RedisCacheManager cacheManager1Day;

  @Autowired
  @Qualifier("cacheManager7Days")
  private RedisCacheManager cacheManager7Days;

  @Test
  public void cacheManager_WhenPrefixingIsDisabled_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  public void cacheManager1Hour_WhenPrefixingIsDisabled_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager1Hour);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  public void cacheManager1Day_WhenPrefixingIsDisabled_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager1Day);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  public void cacheManager7Days_WhenPrefixingIsDisabled_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager7Days);

    assertEquals(expectedPrefix, actualPrefix);
  }

  private String getUsedPrefix(RedisCacheManager cacheManager) {
    Cache cache = cacheManager.getCache(CACHE_NAME);
    assertNotNull(cache);

    RedisCacheConfiguration cacheConfig = cacheManager.getCacheConfigurations().get(CACHE_NAME);
    assertNotNull(cacheConfig);

    return cacheConfig.getKeyPrefixFor(CACHE_NAME);
  }
}