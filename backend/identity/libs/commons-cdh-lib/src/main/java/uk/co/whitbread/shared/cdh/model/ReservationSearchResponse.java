package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationSearchResponse {

  @JsonProperty("TotalResults")
  private Integer totalResults;

  @JsonProperty("SearchResults")
  private Integer searchResults;

  @JsonProperty("Results")
  private List<Booking> results;

  @JsonProperty("ContinuationToken")
  private String continuationToken;

  @JsonProperty("Totals")
  private Totals totals;
}
