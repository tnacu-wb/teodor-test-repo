package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class GetCompanyEmployeesV2Request {

  @JsonProperty("SearchCriteria")
  private String searchCriteria;

  @JsonProperty("AccessLevel")
  private String accessLevel;

  @JsonProperty("AwaitingApproval")
  private Boolean awaitingApproval;

  @JsonProperty("PageToken")
  private String pageToken;

  @JsonProperty("PageSize")
  private Integer pageSize;
}
