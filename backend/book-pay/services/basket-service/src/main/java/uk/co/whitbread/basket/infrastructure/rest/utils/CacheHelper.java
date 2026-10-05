package uk.co.whitbread.basket.infrastructure.rest.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheHelper {

  private final CacheManager rawCacheManager;
  private final ObjectMapper objectMapper;

  public CacheHelper(@Qualifier("rawCacheManager") CacheManager rawCacheManager, ObjectMapper objectMapper) {
    this.rawCacheManager = rawCacheManager;
    this.objectMapper = objectMapper;
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
      return objectMapper.readValue(cachedValue, valueType);
    } catch (JsonProcessingException e) {
      log.error("Could not process cached value.", e);
      return null;
    }
  }
}
