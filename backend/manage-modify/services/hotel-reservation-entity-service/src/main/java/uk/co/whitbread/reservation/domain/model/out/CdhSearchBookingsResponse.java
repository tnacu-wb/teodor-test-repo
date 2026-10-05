package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CdhResultsDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhSearchBookingsResponse {
  @Singular("results")
  private List<CdhResults> results;
  private int cdhSearchResults;
  private int searchResults;
  private int pageResults;
  private String continuationToken;
  private boolean hasMore;
  private boolean responseLimitExceeded;
  private String operaConfNumber;
}
