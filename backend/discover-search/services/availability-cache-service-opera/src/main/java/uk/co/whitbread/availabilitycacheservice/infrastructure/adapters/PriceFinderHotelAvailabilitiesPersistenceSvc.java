package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.PriceFinderHotelAvailabilitiesRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

@Slf4j
@AllArgsConstructor
public class PriceFinderHotelAvailabilitiesPersistenceSvc implements PriceFinderAvailabilitiesPersistencePort {

  private final PriceFinderHotelAvailabilitiesRepository priceFinderHotelAvailabilitiesRepository;

  @Override
  public List<PriceFinderResultSet> getLowestPricesByHotels(PriceFinderSearchCriteria priceFinderSearchCriteria) {

    final LocalDate arrivalDate = LocalDate.parse(priceFinderSearchCriteria.getArrival());
    final LocalDate departureDate = LocalDate.parse(priceFinderSearchCriteria.getDeparture());

    return priceFinderHotelAvailabilitiesRepository.findAvailabilitiesForPriceFinder(
        priceFinderSearchCriteria.getHotelCodes(), arrivalDate, departureDate);
  }

}
