package uk.co.whitbread.infrastructure.rest.client.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.ValueOperations;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsOpeningSoonResult;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsWithFacilityFilterResult;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.model.srp.out.HotelsOpeningSoonModel;
import uk.co.whitbread.domain.model.srp.out.HotelsWithFilterModel;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.infrastructure.config.cache.RedisProperties;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.cache.mapper.CacheResponseMapper;

@ExtendWith(MockitoExtension.class)
public class CacheSearchOutPortImplTest {
  private static final double CACHE_SCORE_NOT_AVAILABLE = 100000;
  private static final String FILTERS_CACHE_NAME = "HotelFacilitiesFilterCache";
  private static final String OPENING_SOON_CACHE_NAME = "HotelsOpeningSoonCache";
  private static final String OPENING_SOON_CACHE_KEY = "OpeningSoon";
  private static final String PREFIX = "PREFIX";
  private static final String HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE = "HotelInformationExtendedNoTypeCache";
  private static final String EDIPAR = "EDIPAR";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";

  @Mock
  private CacheManager cacheManager;

  @Mock
  private CacheResponseMapper responseMapper;

  @Mock
  private RedisTemplate<String, HotelAvailabilityResponse> hotelAvailabilitiesRedisTemplate;

  @Mock
  private RedisTemplate<String, String> hotelAvailabilitiesIndexRedisTemplate;

  @Mock
  private RedisTemplate<String, HotelStatusDto> hotelStatusesRedisTemplate;

  @Mock
  private RedisProperties redisProperties;

  @Mock
  private CacheKeyPrefix cacheKeyPrefix;

  private CacheSearchOutPortImpl cacheSearchOutPort;
  @Mock
  private OhipClient ohipClient;

  @BeforeEach
  public void before() {
    cacheSearchOutPort = new CacheSearchOutPortImpl(cacheManager,
        responseMapper,
        Optional.of(hotelAvailabilitiesRedisTemplate),
        Optional.of(hotelAvailabilitiesIndexRedisTemplate),
        Optional.of(hotelStatusesRedisTemplate),
        redisProperties,
        Optional.of(cacheKeyPrefix),
        ohipClient);
  }

  @Test
  public void testCheckCacheFacilitiesFilterAvailability_true() {
    var cache = mock(Cache.class);
    when(cache.get("KEY")).thenReturn(mock(ValueWrapper.class));
    when(cacheManager.getCache(FILTERS_CACHE_NAME))
        .thenReturn(cache);

    var response = cacheSearchOutPort.checkCacheFacilitiesFilterAvailability("KEY");

    assertTrue(response);
  }

  @Test
  public void testCheckCacheOpeningSoonAvailability_true() {
    var cache = mock(Cache.class);
    when(cache.get(OPENING_SOON_CACHE_KEY)).thenReturn(mock(ValueWrapper.class));
    when(cacheManager.getCache(OPENING_SOON_CACHE_NAME))
        .thenReturn(cache);

    var response = cacheSearchOutPort.checkCacheOpeningSoonAvailability();

    assertTrue(response);
  }

  @Test
  public void testGetHotelIdsByFilter_success() {
    var cache = mock(Cache.class);
    when(cache.get("TEST", HotelsWithFacilityFilterResult.class))
        .thenReturn(mock(HotelsWithFacilityFilterResult.class));
    when(cacheManager.getCache(FILTERS_CACHE_NAME))
        .thenReturn(cache);
    when(responseMapper.toHotelListModel(any()))
        .thenReturn(mock(HotelsWithFilterModel.class));

    var response = cacheSearchOutPort.getHotelIdsByFilter("TEST");

    assertNotNull(response);
  }

  @Test
  public void testGetHotelIdsOpeningSoon_success() {
    var cache = mock(Cache.class);
    when(cache.get(OPENING_SOON_CACHE_KEY, HotelsOpeningSoonResult.class))
        .thenReturn(mock(HotelsOpeningSoonResult.class));
    when(cacheManager.getCache(OPENING_SOON_CACHE_NAME))
        .thenReturn(cache);
    when(responseMapper.toHotelsOpeningSoonModel(any()))
        .thenReturn(mock(HotelsOpeningSoonModel.class));

    var response = cacheSearchOutPort.getHotelIdsOpeningSoon();

    assertNotNull(response);
  }

  @ParameterizedTest
  @ValueSource(strings = {"RECOMMENDATION", "PRICE"})
  public void testSaveOperaHotels_success(String sortBy) {
    var request = HotelAvailabilitiesRequest.builder()
        .arrivalDate("2026-06-06")
        .departureDate("2026-06-11")
        .locationFormat(LocationFormatEnum.PLACEID)
        .location("ABCNDKEKFG")
        .channel("BB")
        .subChannel("WEB")
        .language(LANGUAGE)
        .country(COUNTRY)
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .radius(50)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .roomTypes(List.of("DB"))
        .page(1)
        .lazyLoadPageSize(10)
        .pageSize(5)
        .sort(sortBy)
        .build();

    var hotelAvailabilityList = new ArrayList<HotelAvailabilityResponse>();

    hotelAvailabilityList.add(HotelAvailabilityResponse.builder()
            .hotelId("HOTEL_BIGGER_PRICE_LOWER_DISTANCE")
            .available(true)
            .distance(100.0)
            .lowestRoomRate(Cost.builder()
                .netTotal(BigDecimal.valueOf(1000))
                .build())
            .score(0.123)
            .build());

    hotelAvailabilityList.add(HotelAvailabilityResponse.builder()
        .hotelId("HOTEL_LOWER_PRICE_BIGGER_DISTANCE")
        .available(true)
        .distance(200.0)
        .lowestRoomRate(Cost.builder()
            .netTotal(BigDecimal.valueOf(150))
            .build())
        .score(0.456)
        .build());

    hotelAvailabilityList.add(HotelAvailabilityResponse.builder()
        .hotelId("HOTEL_NOT_AVAILABLE_LOWEST_DISTANCE")
        .available(false)
        .distance(50.0)
        .score(0.789)
        .build());

    var mockOpsForZSet = (ZSetOperations<String, String>) mock(ZSetOperations.class);
    var mockOpsForHash = (HashOperations<String, Object, Object>) mock(HashOperations.class);
    when(mockOpsForZSet.add(anyString(), anySet()))
        .thenReturn(null);
    when(hotelAvailabilitiesIndexRedisTemplate.expire(any(), any()))
        .thenReturn(true);
    when(hotelAvailabilitiesIndexRedisTemplate.opsForZSet())
        .thenReturn(mockOpsForZSet);
    doNothing().when(mockOpsForHash).putAll(anyString(), anyMap());
    when(hotelAvailabilitiesRedisTemplate.expire(any(), any()))
        .thenReturn(true);
    when(hotelAvailabilitiesRedisTemplate.opsForHash())
        .thenReturn(mockOpsForHash);
    when(cacheKeyPrefix.compute(any())).thenReturn(PREFIX);

    cacheSearchOutPort
        .saveOperaHotels(request, hotelAvailabilityList);

    // For each sort option (2 for DEFAULT + DISTANCE, 1 for PRICE)
    Set<TypedTuple<String>> availableFirstDistanceScores = Set.of(
        TypedTuple.of("HOTEL_BIGGER_PRICE_LOWER_DISTANCE", 100.0),
        TypedTuple.of("HOTEL_LOWER_PRICE_BIGGER_DISTANCE", 200.0),
        TypedTuple.of("HOTEL_NOT_AVAILABLE_LOWEST_DISTANCE", CACHE_SCORE_NOT_AVAILABLE + 50.0)
    );

    Set<TypedTuple<String>> priceScores = Set.of(
        TypedTuple.of("HOTEL_BIGGER_PRICE_LOWER_DISTANCE", 1000.0),
        TypedTuple.of("HOTEL_LOWER_PRICE_BIGGER_DISTANCE", 150.0),
        TypedTuple.of("HOTEL_NOT_AVAILABLE_LOWEST_DISTANCE", CACHE_SCORE_NOT_AVAILABLE)
    );

    Set<TypedTuple<String>> availableFirst = Set.of(
        TypedTuple.of("HOTEL_BIGGER_PRICE_LOWER_DISTANCE", 100.0),
        TypedTuple.of("HOTEL_LOWER_PRICE_BIGGER_DISTANCE", 200.0),
        TypedTuple.of("HOTEL_NOT_AVAILABLE_LOWEST_DISTANCE", CACHE_SCORE_NOT_AVAILABLE + 50.0)
    );

    Set<TypedTuple<String>> recommended = Set.of(
        TypedTuple.of("HOTEL_LOWER_PRICE_BIGGER_DISTANCE", 99994.544),
        TypedTuple.of("HOTEL_BIGGER_PRICE_LOWER_DISTANCE", 99994.877),
        TypedTuple.of("HOTEL_NOT_AVAILABLE_LOWEST_DISTANCE", 100002.0)
    );

    verify(mockOpsForZSet, times(2)).add(PREFIX, availableFirstDistanceScores);
    verify(mockOpsForZSet).add(PREFIX, priceScores);
    verify(mockOpsForZSet, times(2)).add(PREFIX, availableFirst);
    if (sortBy.equals("RECOMMENDATION")) {
      verify(mockOpsForZSet).add(PREFIX, recommended);
    }
  }

  @Test
  public void testGetOperaAvailabilityFromCache_success() {
    var request = HotelAvailabilitiesRequest.builder()
        .arrivalDate("2026-06-06")
        .departureDate("2026-06-11")
        .locationFormat(LocationFormatEnum.PLACEID)
        .location("ABCNDKEKFG")
        .channel("BB")
        .subChannel("WEB")
        .language(LANGUAGE)
        .country(COUNTRY)
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .radius(50)
        .radiusUnit(RadiusUnitEnum.KILOMETERS)
        .roomTypes(List.of("DB"))
        .page(1)
        .lazyLoadPageSize(10)
        .pageSize(5)
        .build();

    var mockOpsForZSet = (ZSetOperations<String, String>) mock(ZSetOperations.class);
    var mockOpsForHash = mock(HashOperations.class);
    var cacheResponse = new LinkedHashSet<String>();
    cacheResponse.add("HOTEL1");
    cacheResponse.add("HOTEL2");
    when(mockOpsForZSet.range(anyString(), eq((long) 0), eq((long) 4)))
        .thenReturn(cacheResponse);
    when(hotelAvailabilitiesIndexRedisTemplate.opsForZSet())
        .thenReturn(mockOpsForZSet);
    when(mockOpsForHash.multiGet(any(), eq(List.of("HOTEL1", "HOTEL2"))))
        .thenReturn(List.of(
            HotelAvailabilityResponse.builder().hotelId("HOTEL1").build(),
            HotelAvailabilityResponse.builder().hotelId("HOTEL2").build()));
    when(hotelAvailabilitiesRedisTemplate.opsForHash())
        .thenReturn(mockOpsForHash);
    when(cacheKeyPrefix.compute(anyString())).thenReturn(PREFIX);

    var response = cacheSearchOutPort
        .getOperaAvailabilityFromCache(request);

    assertNotNull(response);
    assertEquals(2, response.size());
    assertEquals("HOTEL1", response.get(0).getHotelId());
    assertEquals("HOTEL2", response.get(1).getHotelId());

    verify(mockOpsForZSet).range(PREFIX, 0L, 4L);
    verify(mockOpsForHash).multiGet(PREFIX, List.of("HOTEL1", "HOTEL2"));
  }

  @Test
  void getOnsaleFlagFromCache_ReturnsEmpty_WhenInputNullOrEmpty() {
    assertNotNull(cacheSearchOutPort.getOnsaleFlagFromCache(null));
    assertEquals(0, cacheSearchOutPort.getOnsaleFlagFromCache(null).size());
    assertEquals(0, cacheSearchOutPort.getOnsaleFlagFromCache(List.of()).size());
  }

  @Test
  void getOnsaleFlagFromCache_ReturnsFromCacheAndFetchesMissing_ThenCachesWithTTL() {
    // Given
    ValueOperations<String, HotelStatusDto> valueOps = mock(ValueOperations.class);
    when(hotelStatusesRedisTemplate.opsForValue()).thenReturn(valueOps);
    when(cacheKeyPrefix.compute("LONKIN")).thenReturn("PREFIX::LONKIN");
    when(cacheKeyPrefix.compute("FRAMTI")).thenReturn("PREFIX::FRAMTI");

    HotelStatusDto cached = mock(HotelStatusDto.class);
    when(cached.getHotelId()).thenReturn("LONKIN");
    when(cached.getOnSale()).thenReturn(true);

    when(valueOps.get("PREFIX::LONKIN")).thenReturn(cached);
    when(valueOps.get("PREFIX::FRAMTI")).thenReturn(null);

    HotelStatusDto fromOpera = mock(HotelStatusDto.class);
    when(fromOpera.getHotelId()).thenReturn("FRAMTI");
    when(fromOpera.getOnSale()).thenReturn(false);

    when(ohipClient.getOnSaleFlagFromOpera(List.of("FRAMTI"))).thenReturn(List.of(fromOpera));
    when(redisProperties.getOnSaleFlagCacheTTLHours()).thenReturn(6);

    // When
    List<HotelStatusDto> firstResult = cacheSearchOutPort.getOnsaleFlagFromCache(List.of("LONKIN", "FRAMTI"));

    // Then
    assertNotNull(firstResult);
    assertEquals(2, firstResult.size());
    assertEquals("LONKIN", firstResult.get(0).getHotelId());
    assertEquals(true, firstResult.get(0).getOnSale());
    assertEquals("FRAMTI", firstResult.get(1).getHotelId());
    assertEquals(false, firstResult.get(1).getOnSale());

    verify(hotelStatusesRedisTemplate, times(3)).opsForValue();
    verify(ohipClient, times(1)).getOnSaleFlagFromOpera(List.of("FRAMTI"));
    verify(hotelStatusesRedisTemplate, times(1))
        .expire(eq("PREFIX::FRAMTI"), any());
  }

  @Test
  void getOnsaleFlagFromCache_ReturnsEmpty_WhenRedisOrPrefixMissing() {
    CacheSearchOutPortImpl subject = new CacheSearchOutPortImpl(
        cacheManager,
        responseMapper,
        Optional.of(hotelAvailabilitiesRedisTemplate),
        Optional.of(hotelAvailabilitiesIndexRedisTemplate),
        Optional.empty(), // no hotelStatusesRedisTemplate
        redisProperties,
        Optional.of(cacheKeyPrefix),
        ohipClient);

    assertEquals(0, subject.getOnsaleFlagFromCache(List.of("A")).size());

    CacheSearchOutPortImpl subject2 = new CacheSearchOutPortImpl(
        cacheManager,
        responseMapper,
        Optional.of(hotelAvailabilitiesRedisTemplate),
        Optional.of(hotelAvailabilitiesIndexRedisTemplate),
        Optional.of(hotelStatusesRedisTemplate),
        redisProperties,
        Optional.empty(), // no key prefix
        ohipClient);

    assertEquals(0, subject2.getOnsaleFlagFromCache(List.of("A")).size());
  }

  @Test
  void getHotelInformationFromCache_ReturnsNull_WhenCacheIsNull() {
    when(cacheManager.getCache(HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE)).thenReturn(null);

    var result = cacheSearchOutPort.getHotelInformationFromCache(COUNTRY, LANGUAGE, "HOTEL1");

    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_ReturnsNull_WhenCacheValueIsNull() {
    Cache cache = mock(Cache.class);
    when(cacheManager.getCache(HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE)).thenReturn(cache);
    when(cache.get(anyString(), eq(String.class))).thenReturn(null);

    var result = cacheSearchOutPort.getHotelInformationFromCache(COUNTRY, LANGUAGE, EDIPAR);

    assertNull(result);
  }

  @Test
  void getHotelInformationFromCache_ReturnsDto_WhenJsonIsValid() {
    Cache cache = mock(Cache.class);
    when(cacheManager.getCache(HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE)).thenReturn(cache);

    HotelInformationExtendedDto dto = new HotelInformationExtendedDto();
    dto.setHotelId(EDIPAR);
    String validJson = "{\"hotelId\":\"EDIPAR\"}";

    when(cache.get(anyString(), eq(String.class))).thenReturn(validJson);

    var result = cacheSearchOutPort.getHotelInformationFromCache(COUNTRY, LANGUAGE, EDIPAR);

    assertNotNull(result);
    assertEquals(EDIPAR, result.getHotelId());
  }

  @Test
  void getHotelInformationFromCache_ReturnsNull_WhenJsonIsInvalid() {
    Cache cache = mock(Cache.class);
    when(cacheManager.getCache(HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE)).thenReturn(cache);

    String invalidJson = "{invalid json}";
    when(cache.get(anyString(), eq(String.class))).thenReturn(invalidJson);

    var result = cacheSearchOutPort.getHotelInformationFromCache(COUNTRY, LANGUAGE, EDIPAR);

    assertNull(result);
  }

}
