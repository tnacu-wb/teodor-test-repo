package uk.co.whitbread.infrastructure.config.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("cachePrefixConfigured")
public class RedisConfigWithPrefixTest {

  private final String KEY_PREFIX = "Hotel-Entity-Service";
  private final String CONTENT_ENTITY_KEY_PREFIX = "Content-Entity-Service";
  private final String EXPECTED_KEY_PATTERN = "%s::%s::";
  private final String CACHE_NAME = "testCache";

  @Autowired
  @Qualifier("cacheManager")
  private RedisCacheManager cacheManager;

  @Autowired
  @Qualifier("cacheManager5Minutes")
  private RedisCacheManager cacheManager5Minutes;

  @Autowired
  @Qualifier("cacheManager10Minutes")
  private RedisCacheManager cacheManager10Minutes;

  @Autowired
  @Qualifier("cacheManager12Hours")
  private RedisCacheManager cacheManager12Hours;

  @Autowired
  @Qualifier("cacheManager24Hours")
  private RedisCacheManager cacheManager24Hours;

  @Autowired
  @Qualifier("cacheManagerForContentEntityService")
  private RedisCacheManager cacheManagerForContentEntityService;

  @Autowired
  @Qualifier("keyPrefix")
  private CacheKeyPrefix keyPrefix;

  @Test
  void cacheManager_WhenPrefixIsUsed_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, KEY_PREFIX, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  void cacheManagerForContentEntityService_WhenPrefixIsUsed_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, CONTENT_ENTITY_KEY_PREFIX,
        CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManagerForContentEntityService);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  void cacheManager5Minutes_WhenPrefixIsUsed_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, KEY_PREFIX, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager5Minutes);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  void cacheManager10Minutes_WhenPrefixIsUsed_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, KEY_PREFIX, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager10Minutes);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  void cacheManager12Hours_WhenPrefixIsUsed_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, KEY_PREFIX, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager12Hours);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  void cacheManager24Hours_WhenPrefixIsUsed_ThenCacheManagerConfiguredCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, KEY_PREFIX, CACHE_NAME);

    String actualPrefix = getUsedPrefix(cacheManager24Hours);

    assertEquals(expectedPrefix, actualPrefix);
  }

  @Test
  void cacheKeyPrefixCorrectly() {
    String expectedPrefix = String.format(EXPECTED_KEY_PATTERN, KEY_PREFIX, CACHE_NAME);
    String result = keyPrefix.compute(CACHE_NAME);
    assertNotNull(result);
    assertEquals(expectedPrefix, result);
  }

  private String getUsedPrefix(RedisCacheManager cacheManager) {
    Cache cache = cacheManager.getCache(CACHE_NAME);
    assertNotNull(cache);

    RedisCacheConfiguration cacheConfig = cacheManager.getCacheConfigurations().get(CACHE_NAME);
    assertNotNull(cacheConfig);

    return cacheConfig.getKeyPrefixFor(CACHE_NAME);
  }
}