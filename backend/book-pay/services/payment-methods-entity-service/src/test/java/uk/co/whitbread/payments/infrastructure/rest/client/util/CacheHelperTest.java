package uk.co.whitbread.payments.infrastructure.rest.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationListDto;

@ExtendWith(MockitoExtension.class)
class CacheHelperTest {

  @Mock
  private CacheManager rawCacheManager;

  @Mock
  private JsonMapper jsonMapper;

  @Mock
  private Cache cache;

  private CacheHelper cacheHelper;

  @BeforeEach
  void setUp() {
    cacheHelper = new CacheHelper(rawCacheManager, jsonMapper);
  }

  @Test
  void getCacheValue_whenCacheNotFound_shouldReturnNull() {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    when(rawCacheManager.getCache(cacheName)).thenReturn(null);

    // Act
    ReservationListDto result = cacheHelper.getCacheValue(cacheName, key, ReservationListDto.class);

    // Assert
    assertNull(result);
    verify(rawCacheManager).getCache(cacheName);
    verifyNoInteractions(jsonMapper);
  }

  @Test
  void getCacheValue_whenCachedValueNotFound_shouldReturnNull() {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    when(rawCacheManager.getCache(cacheName)).thenReturn(cache);
    when(cache.get(key, String.class)).thenReturn(null);

    // Act
    ReservationListDto result = cacheHelper.getCacheValue(cacheName, key, ReservationListDto.class);

    // Assert
    assertNull(result);
    verify(rawCacheManager).getCache(cacheName);
    verify(cache).get(key, String.class);
    verifyNoInteractions(jsonMapper);
  }

  @Test
  void getCacheValue_whenValueIsString_shouldDeserializeUsingJsonMapper() throws JacksonException {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    String jsonString = "{\"reservationByIdList\":[],\"previousTotal\":108.19}";
    ReservationListDto expectedObject = new ReservationListDto();

    when(rawCacheManager.getCache(cacheName)).thenReturn(cache);
    when(cache.get(key, String.class)).thenReturn(jsonString);
    when(jsonMapper.readValue(jsonString, ReservationListDto.class)).thenReturn(expectedObject);

    // Act
    ReservationListDto result =
        cacheHelper.getCacheValue(cacheName, key, ReservationListDto.class);

    // Assert
    assertEquals(expectedObject, result);
    verify(rawCacheManager).getCache(cacheName);
    verify(cache).get(key, String.class);
    verify(jsonMapper).readValue(jsonString, ReservationListDto.class);
  }

  @Test
  void getCacheValue_whenDeserializationFails_shouldReturnNull() throws JacksonException {
    // Arrange
    String cacheName = "testCache";
    String key = "testKey";
    String invalidJson = "{invalid json}";

    when(rawCacheManager.getCache(cacheName)).thenReturn(cache);
    when(cache.get(key, String.class)).thenReturn(invalidJson);
    when(jsonMapper.readValue(invalidJson, ReservationListDto.class))
      .thenThrow(new JacksonException("Invalid JSON") {
        });

    // Act
    ReservationListDto result =
        cacheHelper.getCacheValue(cacheName, key, ReservationListDto.class);

    // Assert
    assertNull(result);
    verify(rawCacheManager).getCache(cacheName);
    verify(cache).get(key, String.class);
    verify(jsonMapper).readValue(invalidJson, ReservationListDto.class);
  }
}
