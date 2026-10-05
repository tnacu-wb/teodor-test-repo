package uk.co.whitbread.spending.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.spending.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class AccountSpendingRequest extends DomainValidator<AccountSpendingRequest> {

  @NotEmpty
  String pibaAccountId;

  @NotEmpty
  String fromMonthYear;

  @NotEmpty
  String toMonthYear;

  String tetheredUserGuid;

  public AccountSpendingRequest(String pibaAccountId, String fromMonthYear, String toMonthYear,
      String tetheredUserGuid) {
    this.pibaAccountId = pibaAccountId;
    this.fromMonthYear = fromMonthYear;
    this.toMonthYear = toMonthYear;
    this.tetheredUserGuid = tetheredUserGuid;
    this.validateSelf();
  }

}
