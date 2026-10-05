package uk.co.whitbread.content.infrastructure.rest.controller.hotel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationExtendedDto;

import java.util.Optional;

class HotelInformationCacheUtilsTest {

  private static final String HOTEL_ID = "123";
  private CacheManager cacheManager;
  private Cache cache;
  private HotelInformationRequest hotelInformationRequest;
  private HotelInformationExtendedDto dto;

  @BeforeEach
  void setUp() {
    cacheManager = mock(CacheManager.class);
    cache = mock(Cache.class);
    hotelInformationRequest = new HotelInformationRequest("GB", "en", HOTEL_ID, null, "web", "direct", null, null);
    dto = new HotelInformationExtendedDto();
    dto.setHotelId(HOTEL_ID);
    when(cacheManager.getCache(anyString())).thenReturn(cache);
  }

  @Test
  void getValueFromCache_returnsEmptyIfCacheIsNull() {
    when(cacheManager.getCache(anyString())).thenReturn(null);
    Optional<HotelInformationExtendedDto> result = HotelInformationCacheUtils.getValueFromCache(
        cacheManager, hotelInformationRequest, HOTEL_ID);
    assertTrue(result.isEmpty());
  }

  @Test
  void getValueFromCache_returnsEmptyIfCacheValueIsNull() {
    when(cache.get(anyString(), eq(String.class))).thenReturn(null);

    Optional<HotelInformationExtendedDto> result = HotelInformationCacheUtils.getValueFromCache(
        cacheManager, hotelInformationRequest, HOTEL_ID);

    assertTrue(result.isEmpty());
  }

  @Test
  void getValueFromCache_returnsDtoIfJsonIsValid() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(dto);
    when(cache.get(anyString(), eq(String.class))).thenReturn(json);

    Optional<HotelInformationExtendedDto> result = HotelInformationCacheUtils.getValueFromCache(
        cacheManager, hotelInformationRequest, HOTEL_ID);

    assertTrue(result.isPresent());
    assertEquals(HOTEL_ID, result.get().getHotelId());
  }

  @Test
  void getValueFromCache_returnsEmptyIfDeserializationFails() {
    when(cache.get(anyString(), eq(String.class))).thenReturn("{invalid json}");

    Optional<HotelInformationExtendedDto> result = HotelInformationCacheUtils.getValueFromCache(
        cacheManager, hotelInformationRequest, HOTEL_ID);

    assertTrue(result.isEmpty());
  }

  @Test
  void storeValueInCache_putsJsonInCacheIfCachePresent() {
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    doNothing().when(cache).put(anyString(), captor.capture());

    HotelInformationCacheUtils.storeValueInCache(cacheManager, hotelInformationRequest, HOTEL_ID, dto);

    verify(cache).put(anyString(), anyString());
    String json = captor.getValue();
    assertTrue(json.contains("\"hotelId\":\"123\""));
  }
}
