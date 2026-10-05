package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchCriteriaDto {

  private String globalCompanyId;
  private String bartEmployeeId;
  private String bartGuestHistoryNumber;
  private String activationKey;
  private String pageToken;
  private String pageSize;
  private String emailAddress;

  @NotBlank
  private String accessContext;
  @NotBlank
  private String accessedBy;
}
