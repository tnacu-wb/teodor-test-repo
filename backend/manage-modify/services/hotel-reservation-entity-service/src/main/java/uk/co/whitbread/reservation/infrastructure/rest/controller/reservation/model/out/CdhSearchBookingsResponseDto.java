package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhSearchBookingsResponseDto {

  private List<CdhResultsDto> results;
  private int cdhSearchResults;
  private int searchResults;
  private int pageResults;
  private String continuationToken;
  private boolean hasMore;
  private boolean responseLimitExceeded;
  private String operaConfNumber;
}
