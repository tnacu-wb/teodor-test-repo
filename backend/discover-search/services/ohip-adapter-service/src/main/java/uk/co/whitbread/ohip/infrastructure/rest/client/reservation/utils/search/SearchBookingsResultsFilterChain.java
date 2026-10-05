package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

public class SearchBookingsResultsFilterChain {

  private final List<SearchBookingsResultsFilter> filters;

  public SearchBookingsResultsFilterChain(List<SearchBookingsResultsFilter> filters) {
    this.filters = filters;
  }

  public boolean filter(SearchBooking searchBooking) {
    return filters.stream().allMatch(filter -> filter.filter(searchBooking));
  }

}
