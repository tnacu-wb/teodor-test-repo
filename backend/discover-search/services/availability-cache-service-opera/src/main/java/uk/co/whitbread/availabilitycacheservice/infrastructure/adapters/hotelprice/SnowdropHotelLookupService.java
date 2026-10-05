package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.SnowdropFeignClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.snowdrop.HotelDetailsMapper;

@Slf4j
@RequiredArgsConstructor
public class SnowdropHotelLookupService implements SnowdropHotelLookupPort {

  private static final String RADIUS_UNIT = "mi";
  private final SnowdropFeignClient snowdropFeignClient;
  private final HotelDetailsMapper hotelDetailsMapper;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager12Hours",
      value = "HotelsByLocation", key = "{#placeId, #radiusInMiles}")
  public List<HotelDetails> getHotelsFromLocation(final String placeId, final int radiusInMiles) {
    try {
      String sanitizedPlaceId = placeId.replace("\n", "").replace("\r", "");
      log.info("Requesting hotel details from snowdrop for placeId={} and radius={}", sanitizedPlaceId, radiusInMiles);
      return hotelDetailsMapper.toModel(
          snowdropFeignClient.getHotelsFromSnowdrop(placeId, radiusInMiles + RADIUS_UNIT));
    } catch (Exception ex) {
      String sanitizedPlaceId = placeId.replace("\n", "").replace("\r", "");
      log.error("Error looking up hotels from snowdrop for placeId={} and radius={}", sanitizedPlaceId, radiusInMiles,
          ex);
      throw ex;
    }
  }

}
