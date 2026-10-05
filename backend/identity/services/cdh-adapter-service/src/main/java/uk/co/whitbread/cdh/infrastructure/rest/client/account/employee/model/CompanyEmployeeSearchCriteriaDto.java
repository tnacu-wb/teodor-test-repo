package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEmployeeSearchCriteriaDto {

  @JsonProperty("GlobalCompanyId")
  private String globalCompanyId;

  @JsonProperty("BartEmployeeID")
  private String bartEmployeeId;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("ActivationKey")
  private String activationKey;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("PageSize")
  private String pageSize;

  @JsonProperty("PageToken")
  private String pageToken;

  @JsonProperty("SearchCriteria")
  private String searchCriteria;

  @JsonProperty("AwaitingApproval")
  private String awaitingApproval;

  @JsonProperty("AccessLevel")
  private String accessLevel;

}
