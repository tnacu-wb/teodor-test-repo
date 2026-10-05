package uk.co.whitbread.reservation.infrastructure.rest.client.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

@ExtendWith(MockitoExtension.class)
class CacheOutPortTest {

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  @InjectMocks
  private CacheOutPortImpl cacheOutPort;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String HOTEL_ID = "HOTEL1";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private static final String CACHE_NAME = "HotelInformationExtendedNoTypeCache";

  @Test
  void getHotelInformationFromCache_successfulDeserialization() throws Exception {
    // Arrange
    var dto = new HotelInformationExtendedDto();
    var json = MAPPER.writeValueAsString(dto);
    var expectedKey = HOTEL_ID + "_" + COUNTRY + "_" + LANGUAGE + "_null_null";

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(json);

    // Act
    var result = cacheOutPort.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNotNull(result);
  }

  @Test
  void getHotelInformationFromCache_cacheMiss_returnsNull() {
    // Arrange
    var expectedKey = HOTEL_ID + "_" + COUNTRY + "_" + LANGUAGE + "_null_null";

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(null);

    // Act
    var result = cacheOutPort.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_cacheIsNull_returnsNull() {
    // Arrange
    when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

    // Act
    var result = cacheOutPort.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_invalidJson_returnsNull() {
    // Arrange
    var expectedKey = HOTEL_ID + "_" + COUNTRY + "_" + LANGUAGE + "_null_null";

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn("not a json");

    // Act
    var result = cacheOutPort.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_emptyCountryAndLanguage_usesDefaults() throws Exception {
    // Arrange
    var dto = new HotelInformationExtendedDto();
    var json = MAPPER.writeValueAsString(dto);
    var expectedKey = HOTEL_ID + "_gb_en_null_null";

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(json);

    // Act
    var result = cacheOutPort.getHotelInformationFromCache(HOTEL_ID, "", "");

    // Assert
    assertNotNull(result);
  }

  @Test
  void getHotelInformationFromCache_nullCountryAndLanguage_usesDefaults() throws Exception {
    // Arrange
    var dto = new HotelInformationExtendedDto();
    var json = MAPPER.writeValueAsString(dto);
    var expectedKey = HOTEL_ID + "_gb_en_null_null";

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(json);

    // Act
    var result = cacheOutPort.getHotelInformationFromCache(HOTEL_ID, null, null);

    // Assert
    assertNotNull(result);
  }
}
