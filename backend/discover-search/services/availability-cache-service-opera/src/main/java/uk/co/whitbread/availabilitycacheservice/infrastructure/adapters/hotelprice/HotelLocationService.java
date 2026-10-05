package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.AemLocationPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.Location;

@Slf4j
@RequiredArgsConstructor
public class HotelLocationService implements HotelLocationPort {

  private static final int HOTEL_CODE_LENGTH = 6;
  private final HotelLocationPersistencePort hotelLocationPersistencePort;
  private final SnowdropHotelLookupPort snowdropHotelLookupPort;
  private final AemLocationPort aemLocationPort;

  @Override
  @Async("updateHotelLocation")
  public void updateHotelLocations() {
    try {
      log.trace("UpdateHotelLocation started at -{}",
          LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      final List<HotelLocationEntity> hotelLocationEntities = buildHotelLocationEntities();
      final List<HotelLocationEntity> filteredHotelLocationEntities = hotelLocationEntities
          .stream()
          .filter(hotelLocationEntity -> hotelLocationEntity.getHotelCode().length() == HOTEL_CODE_LENGTH)
          .collect(toList());
      hotelLocationPersistencePort.update(filteredHotelLocationEntities);
      log.trace("UpdateHotelLocation completed at -{}",
          LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    } catch (Exception ex) {
      log.error("Failed to execute UpdateHotelLocation", ex);
      throw ex;
    }
  }

  private List<HotelLocationEntity> buildHotelLocationEntities() {
    try {
      log.trace("Building HotelLocationEntity with placeId and hotelCode");
      return aemLocationPort.getAemLocations()
          .stream()
          .filter(location -> location.getPlaceId() != null)
          .map(location -> snowdropHotelLookupPort.getHotelsFromLocation(location.getPlaceId(),
                  location.getRadiusInMiles())
              .stream()
              .map(HotelDetails::getCode)
              .map(hotelCode -> getHotelLocationEntity(location, hotelCode)))
          .flatMap(Stream::distinct)
          .toList();
    } catch (Exception ex) {
      log.error("Error creating HotelLocationEntity with placeId and hotelCode", ex);
      throw ex;
    }
  }

  private HotelLocationEntity getHotelLocationEntity(final Location location, final String hotelCode) {
    return HotelLocationEntity.builder()
        .hotelCode(hotelCode)
        .placeId(location.getPlaceId())
        .build();
  }

}
