package uk.co.whitbread.cdh.domain.model.account.out;

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
public class CompanySearch {

  @JsonProperty("TotalResults")
  private Integer totalResults;

  @JsonProperty("SearchResults")
  private Integer searchResults;

  @JsonProperty("Results")
  private List<Company> results;

  @JsonProperty("ContinuationToken")
  private String continuationToken;
  
}
