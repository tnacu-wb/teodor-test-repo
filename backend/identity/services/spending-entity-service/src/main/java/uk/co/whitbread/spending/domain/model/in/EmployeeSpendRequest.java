package uk.co.whitbread.spending.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.spending.domain.model.validation.DomainValidator;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class EmployeeSpendRequest extends DomainValidator<EmployeeSpendRequest> {

  private String companyAccountId;
  private String employeeAccountId;

  @NotEmpty
  private String fromMonthYear;

  @NotEmpty
  private String toMonthYear;

  private String accessContext;
  private String accessedBy;

  public EmployeeSpendRequest(String companyAccountId, String employeeAccountId, String fromMonthYear,
      String toMonthYear, String accessContext, String accessedBy) {
    this.companyAccountId = companyAccountId;
    this.employeeAccountId = employeeAccountId;
    this.fromMonthYear = fromMonthYear;
    this.toMonthYear = toMonthYear;
    this.accessContext = accessContext;
    this.accessedBy = accessedBy;
    this.validateSelf();
  }
}
