package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import java.time.LocalDate;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

public class DepartureDateBookingsResultsFilter implements SearchBookingsResultsFilter {

  @Override
  public boolean filter(SearchBooking searchBooking) {
    return searchBooking.getDepartureDate().isAfter(LocalDate.now().minusYears(1))
        || searchBooking.getDepartureDate().isEqual(LocalDate.now().minusYears(1));
  }
}
