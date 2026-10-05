package uk.co.whitbread.infrastructure.rest.client.cache;

import static java.util.stream.Collectors.toMap;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.getStartingIndex;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsOpeningSoonResult;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsWithFacilityFilterResult;
import uk.co.whitbread.domain.logic.AvailabilitySortOption;
import uk.co.whitbread.domain.logic.HotelAvailabilitySorter;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.model.srp.out.HotelsOpeningSoonModel;
import uk.co.whitbread.domain.model.srp.out.HotelsWithFilterModel;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.infrastructure.config.cache.RedisProperties;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.cache.mapper.CacheResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.cache.model.OperaHotelAvailabilitiesKey;
import uk.co.whitbread.infrastructure.rest.client.utils.SanitizingUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheSearchOutPortImpl implements CacheSearchOutPort {

  public static final String HASH_KEY_TYPE = "HASH";
  public static final String ZSET_KEY_TYPE = "ZSET";
  private static final String FILTERS_CACHE_NAME = "HotelFacilitiesFilterCache";
  private static final String OPENING_SOON_CACHE_NAME = "HotelsOpeningSoonCache";
  private static final String HOTEL_INFORMATION_CACHE_NAME = "HotelInformationExtendedNoTypeCache";
  private static final String OPENING_SOON_CACHE_KEY = "OpeningSoon";
  private static final double CACHE_SCORE_NOT_AVAILABLE = 100000;
  private static final double CACHE_SCORE_OPENING_SOON = 5000;
  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String DEFAULT_CHANNEL = "PI";
  private static final String DEFAULT_SUB_CHANNEL = "WEB";
  @Autowired
  @Qualifier("cacheManagerForContentEntityService")
  private final CacheManager cacheManager;
  private final CacheResponseMapper responseMapper;
  private final Optional<RedisTemplate<String, HotelAvailabilityResponse>>
      hotelAvailabilitiesRedisTemplate;
  private final Optional<RedisTemplate<String, String>>
      hotelAvailabilitiesIndexRedisTemplate;
  private final Optional<RedisTemplate<String, HotelStatusDto>>
      hotelStatusesRedisTemplate;
  private final RedisProperties redisProperties;
  private final Optional<CacheKeyPrefix> keyPrefix;
  private final OhipClient ohipClient;
  private static final ObjectMapper REDIS_NO_TYPE_OBJECT_MAPPER = new ObjectMapper();

  static {
    REDIS_NO_TYPE_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    REDIS_NO_TYPE_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public boolean checkCacheFacilitiesFilterAvailability(String key) {
    var isHotelFacilitiesFilterCacheAvailable = isCacheKeyAvailable(FILTERS_CACHE_NAME, key);
    log.info("Facilities Filter cache is available={}. Using key={}. ", isHotelFacilitiesFilterCacheAvailable, key);
    return isHotelFacilitiesFilterCacheAvailable;
  }

  @Override
  public boolean checkCacheOpeningSoonAvailability() {
    var isOpeningSoonCacheAvailable = isCacheKeyAvailable(OPENING_SOON_CACHE_NAME, OPENING_SOON_CACHE_KEY);
    log.info("Opening Soon cache is available={}. Using Key={}", isOpeningSoonCacheAvailable, OPENING_SOON_CACHE_KEY);
    return isOpeningSoonCacheAvailable;
  }

  private boolean isCacheKeyAvailable(String cacheName, String key) {
    var cache = cacheManager.getCache(cacheName);
    var isCacheAvailable = false;
    if (cache != null) {
      isCacheAvailable = cache.get(key) != null;
    }
    return isCacheAvailable;
  }

  @Override
  public HotelsWithFilterModel getHotelIdsByFilter(String filter) {
    var cache = cacheManager.getCache(FILTERS_CACHE_NAME);
    var cacheValue = cache != null ?  cache.get(filter, HotelsWithFacilityFilterResult.class) : null;
    if (cacheValue != null) {
      return responseMapper.toHotelListModel(cacheValue);
    }
    return new HotelsWithFilterModel(List.of());
  }

  @Override
  public HotelsOpeningSoonModel getHotelIdsOpeningSoon() {
    var cache = cacheManager.getCache(OPENING_SOON_CACHE_NAME);
    var cacheValue = cache != null ?  cache.get(OPENING_SOON_CACHE_KEY, HotelsOpeningSoonResult.class) : null;
    if (cacheValue != null) {
      return responseMapper.toHotelsOpeningSoonModel(cacheValue);
    }
    return new HotelsOpeningSoonModel(List.of());
  }

  @Override
  public HotelInformationExtendedDto getHotelInformationFromCache(String country, String language,
      String hotelId) {

    String cacheKey = hotelId
        + "_"  + (StringUtils.isNotEmpty(country) ? country : COUNTRY_GB)
        + "_"  + (StringUtils.isNotEmpty(language) ? language : LANGUAGE_EN)
        + "_"  + DEFAULT_CHANNEL
        + "_"  + DEFAULT_SUB_CHANNEL;

    var value = Optional.ofNullable(cacheManager.getCache(HOTEL_INFORMATION_CACHE_NAME))
        .map(cache -> cache.get(cacheKey, String.class))
        .filter(Objects::nonNull)
        .map(json -> {
          try {
            return REDIS_NO_TYPE_OBJECT_MAPPER.readValue(json, HotelInformationExtendedDto.class);
          } catch (Exception e) {
            log.error("Error deserializing HotelInformationExtendedDto from cache for key {}: {}",
                SanitizingUtils.sanitize(cacheKey), e.getMessage());
            return null;
          }
        });

    return value.isPresent() ? value.get() : null;
  }

  @Override
  @Async
  public void saveOperaHotels(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                              List<HotelAvailabilityResponse> hotelAvailabilityList) {
    var indexRedisTemplate = getIndexRedisTemplate();
    var hotelAvailabilitiesRedisTemplate =
        getAvailabilitiesRedisTemplate();
    var cacheKeyPrefix = getCacheKeyPrefix();

    if (Objects.isNull(indexRedisTemplate) || Objects.isNull(
        hotelAvailabilitiesRedisTemplate) || Objects.isNull(cacheKeyPrefix)) {
      return;
    }

    // ZSET for each sort option available
    for (AvailabilitySortOption sortOption : AvailabilitySortOption.values()) {
      if ((hotelAvailabilitiesRequest.getSort() != null
          && !AvailabilitySortOption.RECOMMENDATION.name().equals(hotelAvailabilitiesRequest.getSort())
          || hotelAvailabilitiesRequest.getSort() == null)
          && sortOption == AvailabilitySortOption.RECOMMENDATION) {
        continue;
      }
      var keyZSet = OperaHotelAvailabilitiesKey
          .fromAvailabilitiesRequest(getSortKey(sortOption.name()), hotelAvailabilitiesRequest);
      var keyZ = cacheKeyPrefix.compute(keyZSet.toString());

      var valuesWithScore = hotelAvailabilityList
          .stream()
          .map(response ->
              TypedTuple.of(response.getHotelId(),
                  calculateScore(sortOption, response, hotelAvailabilityList.indexOf(response))))
          .collect(Collectors.toSet());

      // Set is automatically sorted ascending by score, see calculateScore description for info
      indexRedisTemplate.opsForZSet().add(keyZ, valuesWithScore);

      indexRedisTemplate.expire(keyZ, Duration.ofMillis(redisProperties.getAvailabilityCacheTTLms()));
    }

    var keyHash = OperaHotelAvailabilitiesKey
        .fromAvailabilitiesRequest(HASH_KEY_TYPE, hotelAvailabilitiesRequest);
    var key = cacheKeyPrefix.compute(keyHash.toString());

    var hashValues = hotelAvailabilityList
        .stream()
        .collect(toMap(HotelAvailabilityResponse::getHotelId, Function.identity()));

    hotelAvailabilitiesRedisTemplate.opsForHash()
        .putAll(key, hashValues);

    hotelAvailabilitiesRedisTemplate
        .expire(key, Duration.ofMillis(redisProperties.getAvailabilityCacheTTLms()));

    log.info("Saved {} hotels under key {}", hotelAvailabilityList.size(), key);
  }

  @Override
  public List<HotelStatusDto> getOnsaleFlagFromCache(List<String> hotelIds) {
    if (hotelIds == null || hotelIds.isEmpty()) {
      return List.of();
    }

    List<String> requestedIds = hotelIds.stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    RedisTemplate<String, HotelStatusDto> hotelStatusesRedisTemplate =
        getHotelStatusesRedisTemplate();
    CacheKeyPrefix cacheKeyPrefix = getCacheKeyPrefix();

    if (Objects.isNull(hotelStatusesRedisTemplate) || Objects.isNull(cacheKeyPrefix)) {
      return Collections.emptyList();
    }

    Map<String, HotelStatusDto> idToStatus = new LinkedHashMap<>();
    List<String> missingIds = new ArrayList<>();

    // try fetching from cache
    for (String id : requestedIds) {
      String key = cacheKeyPrefix.compute(id);

      HotelStatusDto cached = hotelStatusesRedisTemplate.opsForValue().get(key);
      if (cached != null) {
        idToStatus.put(id, cached);
      } else {
        missingIds.add(id);
      }
    }

    // try fetching from ohip
    if (!missingIds.isEmpty()) {
      List<HotelStatusDto> fetched = ohipClient.getOnSaleFlagFromOpera(missingIds);
      for (HotelStatusDto dto : fetched) {
        if (dto != null && dto.getHotelId() != null) {
          var key = cacheKeyPrefix.compute(dto.getHotelId());

          hotelStatusesRedisTemplate.opsForValue().set(key, dto);
          hotelStatusesRedisTemplate.expire(key,
              Duration.ofHours(redisProperties.getOnSaleFlagCacheTTLHours()));
          idToStatus.put(dto.getHotelId(), dto);
        }
      }
      log.info("Fetched and cached {} hotel status entries (TTL {} hours)",
          fetched.size(), redisProperties.getOnSaleFlagCacheTTLHours());
    }

    return requestedIds.stream()
        .map(idToStatus::get)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public List<HotelAvailabilityResponse> getOperaAvailabilityFromCache(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    var indexRedisTemplate = getIndexRedisTemplate();
    var hotelAvailabilitiesRedisTemplate =
        getAvailabilitiesRedisTemplate();
    var cacheKeyPrefix = getCacheKeyPrefix();

    if (Objects.isNull(indexRedisTemplate) || Objects.isNull(
        hotelAvailabilitiesRedisTemplate) || Objects.isNull(cacheKeyPrefix)) {
      return Collections.emptyList();
    }

    var keyZSet = OperaHotelAvailabilitiesKey
        .fromAvailabilitiesRequest(getSortKey(hotelAvailabilitiesRequest.getSort()), hotelAvailabilitiesRequest);
    var keyZ = cacheKeyPrefix.compute(keyZSet.toString());
    var keyHash = OperaHotelAvailabilitiesKey
        .fromAvailabilitiesRequest(HASH_KEY_TYPE, hotelAvailabilitiesRequest);
    var key = cacheKeyPrefix.compute(keyHash.toString());
    var start = getStartingIndex(hotelAvailabilitiesRequest);
    var end = hotelAvailabilitiesRequest.getPageSize()
        + ((hotelAvailabilitiesRequest.getPage() - 1) * hotelAvailabilitiesRequest.getLazyLoadPageSize())
        - 1;

    var hotelIds = indexRedisTemplate.opsForZSet().range(keyZ, start, end);

    if (hotelIds == null || hotelIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<HotelAvailabilityResponse> results =
        hotelAvailabilitiesRedisTemplate
            .opsForHash().multiGet(key, List.copyOf(hotelIds))
            .stream()
            .filter(r -> r instanceof HotelAvailabilityResponse)
            .map(r -> (HotelAvailabilityResponse) r)
            .toList();

    log.info("Loaded {} hotels from cache key {}", results.size(), key);

    return results;
  }

  private String getSortKey(String sort) {
    return ZSET_KEY_TYPE + (sort == null ? AvailabilitySortOption.AVAILABLE_FIRST.name() : sort);
  }

  /**
   * Method that calculates sorted set score based on the sort option.
   * Entries are sorted ascending by score in cache so a predefined value will be added
   * to the score to preserve the sort order required.
   *
   * @param sortOption The sort option.
   * @param hotelAvailabilityResponse The response to be cached.
   * @return A cache score to be used when sorting sets.
   */
  private double calculateScore(AvailabilitySortOption sortOption,
      HotelAvailabilityResponse hotelAvailabilityResponse, int index) {
    double score = 0;
    switch (sortOption) {
      case AVAILABLE_FIRST, DISTANCE -> {
        score = hotelAvailabilityResponse.getDistance();
        if (isNotAvailable(hotelAvailabilityResponse)) {
          score += CACHE_SCORE_NOT_AVAILABLE;

          if (isOpeningSoon(hotelAvailabilityResponse)) {
            score -= CACHE_SCORE_OPENING_SOON;
          }
        } else {
          if (isOpeningSoon(hotelAvailabilityResponse)) {
            score += CACHE_SCORE_OPENING_SOON;
          }
        }
      }
      case PRICE -> {
        if (isNotAvailable(hotelAvailabilityResponse)) {
          score += CACHE_SCORE_NOT_AVAILABLE;
        } else {
          score = hotelAvailabilityResponse.getLowestRoomRate().getNetTotal().doubleValue();
        }
      }
      case RECOMMENDATION ->
        score = getScoreForRecommendationOption(hotelAvailabilityResponse, score, index);
      default ->
        score = CACHE_SCORE_NOT_AVAILABLE;

    }

    return score;
  }

  private static double getScoreForRecommendationOption(
      HotelAvailabilityResponse hotelAvailabilityResponse, double score, int index) {
    if (isNotAvailable(hotelAvailabilityResponse)) {
      score += CACHE_SCORE_NOT_AVAILABLE + index;
    } else {
      score = CACHE_SCORE_NOT_AVAILABLE + HotelAvailabilitySorter.UNAVAILABLE_HOTEL_SCORE
          - hotelAvailabilityResponse.getScore();
    }
    return score;
  }

  private static boolean isOpeningSoon(HotelAvailabilityResponse hotelAvailabilityResponse) {
    return hotelAvailabilityResponse.getHotelOpeningSoon() != null
        && hotelAvailabilityResponse.getHotelOpeningSoon();
  }

  private static boolean isNotAvailable(HotelAvailabilityResponse hotelAvailabilityResponse) {
    return hotelAvailabilityResponse.getAvailable() == null
        || !hotelAvailabilityResponse.getAvailable();
  }

  private RedisTemplate<String, HotelAvailabilityResponse> getAvailabilitiesRedisTemplate() {
    return hotelAvailabilitiesRedisTemplate.orElse(null);
  }

  private RedisTemplate<String, String> getIndexRedisTemplate() {
    return hotelAvailabilitiesIndexRedisTemplate.orElse(null);
  }

  private RedisTemplate<String, HotelStatusDto> getHotelStatusesRedisTemplate() {
    return hotelStatusesRedisTemplate.orElse(null);
  }

  private CacheKeyPrefix getCacheKeyPrefix() {
    return keyPrefix.orElse(null);
  }
}
