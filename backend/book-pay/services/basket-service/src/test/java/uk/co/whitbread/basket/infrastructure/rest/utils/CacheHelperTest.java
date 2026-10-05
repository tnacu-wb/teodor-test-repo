package uk.co.whitbread.basket.infrastructure.rest.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;

@ExtendWith(MockitoExtension.class)
class CacheHelperTest {

  @Mock
  private CacheManager rawCacheManager;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private Cache cache;

  private CacheHelper cacheHelper;

  @BeforeEach
  void setUp() {
    cacheHelper = new CacheHelper(rawCacheManager, objectMapper);
  }

  @Test
  void getCacheValue_whenCacheNotFound_shouldReturnNull() {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    when(rawCacheManager.getCache(cacheName)).thenReturn(null);

    // Act
    ReservationByBasketRefResponse result =
        cacheHelper.getCacheValue(cacheName, key, ReservationByBasketRefResponse.class);

    // Assert
    assertNull(result);
    verify(rawCacheManager).getCache(cacheName);
    verifyNoInteractions(objectMapper);
  }

  @Test
  void getCacheValue_whenCachedValueNotFound_shouldReturnNull() {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    when(rawCacheManager.getCache(cacheName)).thenReturn(cache);
    when(cache.get(key, String.class)).thenReturn(null);

    // Act
    ReservationByBasketRefResponse result =
        cacheHelper.getCacheValue(cacheName, key, ReservationByBasketRefResponse.class);

    // Assert
    assertNull(result);
    verify(rawCacheManager).getCache(cacheName);
    verify(cache).get(key, String.class);
    verifyNoInteractions(objectMapper);
  }

  @Test
  void getCacheValue_whenValueIsString_shouldDeserializeUsingObjectMapper() throws JsonProcessingException {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    String jsonString = "{\"reservationByIdList\":[],\"previousTotal\":108.19}";
    ReservationByBasketRefResponse expectedObject = new ReservationByBasketRefResponse();

    when(rawCacheManager.getCache(cacheName)).thenReturn(cache);
    when(cache.get(key, String.class)).thenReturn(jsonString);
    when(objectMapper.readValue(jsonString, ReservationByBasketRefResponse.class)).thenReturn(expectedObject);

    // Act
    ReservationByBasketRefResponse result =
        cacheHelper.getCacheValue(cacheName, key, ReservationByBasketRefResponse.class);

    // Assert
    assertEquals(expectedObject, result);
    verify(rawCacheManager).getCache(cacheName);
    verify(cache).get(key, String.class);
    verify(objectMapper).readValue(jsonString, ReservationByBasketRefResponse.class);
  }

  @Test
  void getCacheValue_whenDeserializationFails_shouldReturnNull() throws JsonProcessingException {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    String invalidJson = "{invalid json}";

    when(rawCacheManager.getCache(cacheName)).thenReturn(cache);
    when(cache.get(key, String.class)).thenReturn(invalidJson);
    when(objectMapper.readValue(invalidJson, ReservationByBasketRefResponse.class))
        .thenThrow(new JsonProcessingException("Invalid JSON") {
        });

    // Act
    ReservationByBasketRefResponse result =
        cacheHelper.getCacheValue(cacheName, key, ReservationByBasketRefResponse.class);

    // Assert
    assertNull(result);
    verify(rawCacheManager).getCache(cacheName);
    verify(cache).get(key, String.class);
    verify(objectMapper).readValue(invalidJson, ReservationByBasketRefResponse.class);
  }
}

