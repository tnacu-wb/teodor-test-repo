package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelToHotelMapper.mapLocationBestPricedHotelsToHotels;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.LocationPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.DatabaseProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.LocationBestPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelLocationJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;

@Slf4j
@AllArgsConstructor
public class LocationPricePersistenceAdapter implements LocationPricePersistencePort {

  public static final String INCORRECT_DATE_FORMAT =
      "Format of given date is not correct, please try with yyyy-mm-dd";
  private final JdbcTemplate jdbcTemplate;
  private final DatabaseProperties databaseProperties;
  private HotelLocationJpaRepository hotelLocationJpaRepository;

  @Override
  public void updateBestHotelPricesForLocations(final LocalDate availDate, final String hotelCode) {
    try {
      log.trace("Going to update best price for locations for the hotelCode - {} and availDate - {}", hotelCode,
          availDate);
      jdbcTemplate.update("call avail_cache.UPDATE_LOCATION_PRICE (?, ?)", availDate, hotelCode);
    } catch (Exception ex) {
      log.error("Error while trying to execute function update_locations", ex);
    }
  }

  @Override
  public List<Hotel> findBestPricePerLocation(final LocationPriceRequest request) {

    final LocationPriceRequest locationPriceRequest = ofNullable(request).orElse(new LocationPriceRequest());

    final LocalDate arrival = ofNullable(locationPriceRequest.getStartDate()).map(LocalDate::parse)
        .orElseThrow(getResponseStatusExceptionSupplier());
    final LocalDate departure = ofNullable(locationPriceRequest.getEndDate()).map(LocalDate::parse)
        .orElseThrow(getResponseStatusExceptionSupplier());

    final BigDecimal altPrice = locationPriceRequest.getAltPriceThreshold();
    final BigDecimal priceThreshold = locationPriceRequest.getPriceThreshold();
    //compare altPrice and priceThreshold and find out  the minimum of the two
    final BigDecimal minPrice = altPrice.min(priceThreshold);

    try {
      log.debug("Requesting best price for arrival={}, departure={} and minPrice:{}",
          arrival, departure, minPrice);

      final List<LocationBestPrice> bestPricedHotels = hotelLocationJpaRepository
          .findBestPricedHotelForLocations(arrival, departure, minPrice);

      log.debug("bestPricedHotels: {}", bestPricedHotels);

      return mapLocationBestPricedHotelsToHotels(bestPricedHotels);
    } catch (Exception ex) {
      log.error("Error while trying to fetch best prices for arrival={}, departure={} and altThreshold={}",
          arrival, departure, altPrice, ex);
      throw ex;
    }

  }

  private Supplier<ResponseStatusException> getResponseStatusExceptionSupplier() {
    return () -> new ResponseStatusException(
        HttpStatus.BAD_REQUEST, INCORRECT_DATE_FORMAT);
  }
}
