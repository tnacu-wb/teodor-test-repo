package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

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
class CacheLookupServiceTest {

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  @InjectMocks
  private CacheLookupService cacheLookupService;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String CACHE_NAME = "HotelInformationExtendedNoTypeCache";

  private static final String HOTEL_ID = "HOTEL1";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private static final String DEFAULT_CHANNEL = "PI";
  private static final String DEFAULT_SUB_CHANNEL = "WEB";

  @Test
  void getHotelInformationFromCache_successfulDeserialization() throws Exception {
    // Arrange
    var dto = new HotelInformationExtendedDto();
    var json = MAPPER.writeValueAsString(dto);
    var expectedKey = HOTEL_ID + "_" + COUNTRY + "_" + LANGUAGE + "_"  + DEFAULT_CHANNEL + "_"  + DEFAULT_SUB_CHANNEL;

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(json);

    // Act
    var result = cacheLookupService.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNotNull(result);
  }

  @Test
  void getHotelInformationFromCache_cacheMiss_returnsNull() {
    // Arrange
    var expectedKey = HOTEL_ID + "_" + COUNTRY + "_" + LANGUAGE + "_"  + DEFAULT_CHANNEL + "_"  + DEFAULT_SUB_CHANNEL;

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(null);

    // Act
    var result = cacheLookupService.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_cacheIsNull_returnsNull() {
    // Arrange
    when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

    // Act
    var result = cacheLookupService.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_invalidJson_returnsNull() {
    // Arrange
    var expectedKey = HOTEL_ID + "_" + COUNTRY + "_" + LANGUAGE + "_"  + DEFAULT_CHANNEL + "_"  + DEFAULT_SUB_CHANNEL;

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn("not a json");

    // Act
    var result = cacheLookupService.getHotelInformationFromCache(HOTEL_ID, COUNTRY, LANGUAGE);

    // Assert
    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_emptyCountryAndLanguage_usesDefaults() throws Exception {
    // Arrange
    var dto = new HotelInformationExtendedDto();
    var json = MAPPER.writeValueAsString(dto);
    var expectedKey = HOTEL_ID + "_gb_en" + "_"  + DEFAULT_CHANNEL + "_"  + DEFAULT_SUB_CHANNEL;

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(json);

    // Act
    var result = cacheLookupService.getHotelInformationFromCache(HOTEL_ID, "", "");

    // Assert
    assertNotNull(result);
  }

  @Test
  void getHotelInformationFromCache_nullCountryAndLanguage_usesDefaults() throws Exception {
    // Arrange
    var dto = new HotelInformationExtendedDto();
    var json = MAPPER.writeValueAsString(dto);
    var expectedKey = HOTEL_ID + "_gb_en" + "_"  + DEFAULT_CHANNEL + "_"  + DEFAULT_SUB_CHANNEL;

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    when(cache.get(expectedKey, String.class)).thenReturn(json);

    // Act
    var result = cacheLookupService.getHotelInformationFromCache(HOTEL_ID, null, null);

    // Assert
    assertNotNull(result);
  }
}
