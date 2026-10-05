package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

public class BookerLastNameSearchBookingsResultsFilter implements SearchBookingsResultsFilter {

  private final String filterField;

  public BookerLastNameSearchBookingsResultsFilter(String filterField) {
    this.filterField = filterField;
  }

  @Override
  public boolean filter(SearchBooking searchBooking) {
    if (searchBooking.getBooker() == null) {
      return false;
    }

    return filterField.equals(searchBooking.getBooker().getLastName());
  }
}
