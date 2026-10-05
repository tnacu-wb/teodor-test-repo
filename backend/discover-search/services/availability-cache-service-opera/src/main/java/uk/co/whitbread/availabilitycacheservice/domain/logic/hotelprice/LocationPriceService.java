package uk.co.whitbread.availabilitycacheservice.domain.logic.hotelprice;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.LocationPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.LocationPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;

@Slf4j
@AllArgsConstructor
public class LocationPriceService implements LocationPricePort {

  private final LocationPricePersistencePort locationPricePersistencePort;

  @Override
  public List<Hotel> getBestPricedHotels(final LocationPriceRequest locationPriceRequest) {
    log.debug("Best price requested for hotels with startDate={}, endDate={}, priceThreshold={}, altPriceThreshold={}",
        sanitize(locationPriceRequest.getStartDate()), sanitize(locationPriceRequest.getEndDate()),
        sanitize(locationPriceRequest.getPriceThreshold()), sanitize(locationPriceRequest.getAltPriceThreshold()));
    try {
      return locationPricePersistencePort.findBestPricePerLocation(locationPriceRequest);
    } catch (Exception ex) {
      log.error("Error getting best prices for {}", sanitize(locationPriceRequest), ex);
      throw ex;
    }
  }

}
