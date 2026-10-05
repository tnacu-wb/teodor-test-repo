package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class GetCustomerAccountsResponse {

  @JsonProperty("TotalResults")
  private Integer totalResults;

  @JsonProperty("SearchResults")
  private Integer searchResults;

  @JsonProperty("ContinuationToken")
  private String continuationToken;

  @JsonProperty("Results")
  private List<GetCustomerAccountResponse> results;
}
