package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

public class GuestLastNameSearchBookingsResultsFilter implements SearchBookingsResultsFilter {

  private final String filterField;

  public GuestLastNameSearchBookingsResultsFilter(String filterField) {
    this.filterField = filterField;
  }

  @Override
  public boolean filter(SearchBooking searchBooking) {
    if (searchBooking.getReservations() == null) {
      return false;
    }

    return searchBooking.getReservations().stream().anyMatch(
        reservation -> reservation.getStayingGuest() != null && filterField.equals(
            reservation.getStayingGuest().getLastName()));
  }

}
