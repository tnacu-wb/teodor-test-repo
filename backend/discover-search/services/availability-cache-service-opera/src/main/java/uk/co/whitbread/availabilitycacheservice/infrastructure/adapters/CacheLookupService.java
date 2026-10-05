package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.CacheLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

@Slf4j
@Service
public class CacheLookupService implements CacheLookupPort {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String DEFAULT_CHANNEL = "PI";
  private static final String DEFAULT_SUB_CHANNEL = "WEB";

  private static final String HOTEL_INFORMATION_CACHE_NAME = "HotelInformationExtendedNoTypeCache";

  private CacheManager cacheManagerForContentEntityService;
  private static final ObjectMapper REDIS_NO_TYPE_OBJECT_MAPPER = new ObjectMapper();

  static {
    REDIS_NO_TYPE_OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    REDIS_NO_TYPE_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public CacheLookupService(
      @Qualifier("cacheManagerForContentEntityService") CacheManager cacheManagerForContentEntityService) {
    this.cacheManagerForContentEntityService = cacheManagerForContentEntityService;
  }

  @Override
  public HotelInformationExtendedDto getHotelInformationFromCache(String hotelId, String country,
      String language) {

    var cacheKey = hotelId
        + "_"  + (StringUtils.isNotEmpty(country) ? country : COUNTRY_GB)
        + "_"  + (StringUtils.isNotEmpty(language) ? language : LANGUAGE_EN)
        + "_"  + DEFAULT_CHANNEL
        + "_"  + DEFAULT_SUB_CHANNEL;

    var value = Optional.ofNullable(cacheManagerForContentEntityService.getCache(HOTEL_INFORMATION_CACHE_NAME))
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
}
