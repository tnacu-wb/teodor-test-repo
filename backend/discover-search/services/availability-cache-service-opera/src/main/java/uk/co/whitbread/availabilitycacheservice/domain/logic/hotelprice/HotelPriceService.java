package uk.co.whitbread.availabilitycacheservice.domain.logic.hotelprice;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.HotelPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;

@Slf4j
@AllArgsConstructor
public class HotelPriceService implements HotelPricePort {

  private final HotelPricePersistencePort hotelPricePersistencePort;
  private final ContentClientLookUpService contentClientLookUpService;

  public List<Hotel> getBestPricedHotels(final HotelPriceRequest hotelPriceRequest) {
    log.debug("Best priced hotels request: {}", sanitize(hotelPriceRequest));
    try {
      return hotelPricePersistencePort.getBestPriceForHotels(hotelPriceRequest);
    } catch (Exception ex) {
      log.error("Error getting best prices for {}", sanitize(hotelPriceRequest), ex);
      throw ex;
    }
  }

  @Override
  public List<Hotel> getBestPricedHotelsForGivenHotelCode(final String hotelCode,
      final HotelPriceCalendarRequest hotelPriceCalendarRequest) {
    log.debug("Best price for hotel request: hotelCode = {}, hotelPriceCalendarRequest: roomType={}, arrival={}, "
        + "departure={}, hotelCityTaxInfo={}",
        sanitize(hotelCode), sanitize(hotelPriceCalendarRequest.getRoomType()),
        sanitize(hotelPriceCalendarRequest.getArrival()), sanitize(hotelPriceCalendarRequest.getDeparture()),
        sanitize(hotelPriceCalendarRequest.getHotelCityTaxInfo()));
    try {

      hotelPriceCalendarRequest.setHotelCityTaxInfo(
          contentClientLookUpService.getHotelsCityTaxInfo("gb", "en", List.of(hotelCode)));
      return hotelPricePersistencePort.getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);
    } catch (Exception ex) {
      log.error("Error getting best prices for hotelCode = {}, hotelPriceCalendarRequest = {}",
          sanitize(hotelCode), sanitize(hotelPriceCalendarRequest), ex);
      throw ex;
    }
  }

}
