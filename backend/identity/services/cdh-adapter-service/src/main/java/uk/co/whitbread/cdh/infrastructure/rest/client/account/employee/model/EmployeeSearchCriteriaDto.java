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
public class EmployeeSearchCriteriaDto {

  @JsonProperty("GlobalCompanyId")
  private String globalCompanyId;

  @JsonProperty("BartEmployeeID")
  private String bartEmployeeId;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("ActivationKey")
  private String activationKey;

  @JsonProperty("PageToken")
  private String pageToken;

  @JsonProperty("PageSize")
  private String pageSize;

  @JsonProperty("EmailAddress")
  private String emailAddress;

}

