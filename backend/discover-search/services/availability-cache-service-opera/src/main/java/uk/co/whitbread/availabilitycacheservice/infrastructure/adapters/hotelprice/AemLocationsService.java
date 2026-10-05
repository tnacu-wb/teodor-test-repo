package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.AemLocationPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.aem.AemFeignClientProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.AemFeignClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.AemItem;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.AemLocationResponse;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.Location;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.Root;

@Slf4j
@RequiredArgsConstructor
public class AemLocationsService implements AemLocationPort {

  private static final String ERROR_MESSAGE = "AEM locations not found.";
  private final AemFeignClient aemFeignClient;
  private final AemFeignClientProperties aemFeignClientProperties;

  public Collection<Location> getAemLocations() {
    log.info("AemLocationService getAemLocations processing started..!!");
    final AemLocationResponse locations = aemFeignClient.getLocations(
        aemFeignClientProperties.getCountry(),
        aemFeignClientProperties.getLanguage(),
        aemFeignClientProperties.getResource());

    log.trace("aemResponse locations - {}", locations);
    final Collection<Location> aemLocations = Optional.ofNullable(locations)
        .map(AemLocationResponse::getItems)
        .map(AemItem::getRoot)
        .map(Root::getItems)
        .map(Map::values)
        .orElseThrow(() -> {
          log.error(ERROR_MESSAGE);
          return new HotelAvailabilitiesException(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        });
    log.info("{} locations found in AEM.", aemLocations.size());
    log.trace("AemLocationService getAemLocations completed Processing..!!");
    return aemLocations;
  }
}
