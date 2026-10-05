package uk.co.whitbread.payments.infrastructure.rest.client.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
@Slf4j
public class CacheHelper {

  private final CacheManager rawCacheManager;
  private final JsonMapper jsonMapper;

  public CacheHelper(@Qualifier("rawCacheManager") CacheManager rawCacheManager, JsonMapper jsonMapper) {
    this.rawCacheManager = rawCacheManager;
    this.jsonMapper = jsonMapper;
  }

  @Nullable
  public <T> T getCacheValue(String cacheName, String key, Class<T> valueType) {
    var cache = rawCacheManager.getCache(cacheName);
    if (cache == null) {
      return null;
    }

    var cachedValue = cache.get(key, String.class);
    if (cachedValue == null) {
      return null;
    }

    try {
      return jsonMapper.readValue(cachedValue, valueType);
    } catch (JacksonException e) {
      log.error("Could not process cached value.", e);
      return null;
    }
  }
}