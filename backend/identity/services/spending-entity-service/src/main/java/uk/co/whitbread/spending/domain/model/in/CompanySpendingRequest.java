package uk.co.whitbread.spending.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.spending.domain.model.validation.DomainValidator;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CompanySpendingRequest extends DomainValidator<CompanySpendingRequest> {

  private String companyAccountId;

  @NotEmpty
  private String fromMonthYear;

  @NotEmpty
  private String toMonthYear;

  public CompanySpendingRequest(String companyAccountId, String fromMonthYear, String toMonthYear) {
    this.companyAccountId = companyAccountId;
    this.fromMonthYear = fromMonthYear;
    this.toMonthYear = toMonthYear;
    this.validateSelf();
  }
}
