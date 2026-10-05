package uk.co.whitbread.cdh.domain.model.spending;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSpendRequest {

  @NotBlank
  private String companyAccountId;
  @NotBlank
  private String employeeAccountId;
  @NotBlank
  private String fromMonthYear;
  @NotBlank
  private String toMonthYear;
  @NotBlank
  private String accessContext;
  @NotBlank
  private String accessedBy;
}
