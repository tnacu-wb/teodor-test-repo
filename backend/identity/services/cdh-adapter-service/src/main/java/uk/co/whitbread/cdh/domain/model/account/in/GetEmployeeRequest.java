package uk.co.whitbread.cdh.domain.model.account.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeeRequest {

  @NotBlank
  private String companyAccountId;
  @NotBlank
  private String employeeAccountId;
  @NotBlank
  private String accessContext;
  @NotBlank
  private String accessedBy;
}
