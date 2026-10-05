package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSearch {

  @JsonProperty("TotalResults")
  private Integer totalResults;

  @JsonProperty("SearchResults")
  private Integer searchResults;

  @JsonProperty("Results")
  private List<Results> results;

  @JsonProperty("ContinuationToken")
  private String continuationToken;

  @JsonProperty("Totals")
  private Totals totals;

  @JsonProperty("TotalSize")
  private Integer totalSize;


}
