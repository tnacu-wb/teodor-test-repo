package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

public interface SearchBookingsResultsFilter {

  boolean filter(SearchBooking searchBooking);

}
