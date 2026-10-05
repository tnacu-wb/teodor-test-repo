package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchBookingsInput {

  private SearchBookingsType searchBookingsType;

  private Map<String, List<String>> searchFields;

  private SearchBookingsResultsFilterChain filterChain;
}
