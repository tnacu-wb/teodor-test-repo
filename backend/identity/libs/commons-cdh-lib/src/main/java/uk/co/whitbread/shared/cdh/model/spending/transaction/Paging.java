package uk.co.whitbread.shared.cdh.model.spending.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Paging {

  @JsonProperty("TotalResults")
  private Integer totalResults;

  @JsonProperty("CurrentPage")
  private Integer currentPage;

  @JsonProperty("PageSize")
  private Integer pageSize;
}
