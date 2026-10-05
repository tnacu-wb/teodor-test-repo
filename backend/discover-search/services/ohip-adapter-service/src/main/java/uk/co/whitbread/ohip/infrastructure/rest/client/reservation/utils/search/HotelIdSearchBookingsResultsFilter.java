package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

public class HotelIdSearchBookingsResultsFilter implements SearchBookingsResultsFilter {

  private final String filterField;

  public HotelIdSearchBookingsResultsFilter(String filterField) {
    this.filterField = filterField;
  }

  @Override
  public boolean filter(SearchBooking searchBooking) {
    if (searchBooking.getHotelId() == null) {
      return false;
    }

    return filterField.equals(searchBooking.getHotelId());
  }

}
