package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@NoArgsConstructor
@Data
@Builder
public class BillingCcuiDto implements SelfValidation<BillingCcuiDto> {

  @Valid
  private AddressCcuiDto address;

  public BillingCcuiDto(AddressCcuiDto address) {
    this.address = address;
    this.validateSelf();
  }
}
