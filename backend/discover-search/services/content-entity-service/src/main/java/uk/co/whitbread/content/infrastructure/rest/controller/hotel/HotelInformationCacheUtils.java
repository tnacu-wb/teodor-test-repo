package uk.co.whitbread.content.infrastructure.rest.controller.hotel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.utils.SanitizingUtils;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationExtendedDto;

@Slf4j
@UtilityClass
public class HotelInformationCacheUtils {

  private static final ObjectMapper REDIS_NO_TYPE_OBJECT_MAPPER = new ObjectMapper();
  private static final String HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE = "HotelInformationExtendedNoTypeCache";

  static {
    REDIS_NO_TYPE_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
  }

  private static String getCacheKey(HotelInformationRequest hotelInformationRequest, String hotelId) {
    return hotelId + "_" + hotelInformationRequest.getCountry() + "_"
        + hotelInformationRequest.getLanguage() + "_" + hotelInformationRequest.getChannel() + "_"
        + hotelInformationRequest.getSubchannel();
  }

  private static Optional<Cache> getCache(CacheManager cacheManager) {
    return Optional.ofNullable(cacheManager.getCache(HOTEL_INFORMATION_EXTENDED_NO_TYPE_CACHE));
  }

  public static Optional<HotelInformationExtendedDto> getValueFromCache(CacheManager cacheManager,
      HotelInformationRequest hotelInformationRequest, String hotelId) {

    var cacheKey = getCacheKey(hotelInformationRequest, hotelId);
    return getCache(cacheManager)
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
  }

  public static void storeValueInCache(CacheManager cacheManager, HotelInformationRequest hotelInformationRequest,
      String hotelId, HotelInformationExtendedDto value) {

    var cacheKey = getCacheKey(hotelInformationRequest, hotelId);
    getCache(cacheManager).ifPresent(cache -> {
      if (cache != null) {
        try {
          String jsonValue = REDIS_NO_TYPE_OBJECT_MAPPER.writeValueAsString(value);
          cache.put(cacheKey, jsonValue);
        } catch (Exception e) {
          log.error("Error serializing HotelInformationExtendedDto from cache for key {}: {}",
              SanitizingUtils.sanitize(cacheKey), e.getMessage());
        }
      }
    });
  }
}
