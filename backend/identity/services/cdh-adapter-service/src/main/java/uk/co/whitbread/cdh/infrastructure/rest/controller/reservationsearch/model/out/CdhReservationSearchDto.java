package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhReservationSearchDto {

  private Integer totalResults;
  private Integer searchResults;
  private List<ResultsDto> results;
  private String continuationToken;
  private TotalsDto totals;
  private Integer totalSize;
}
