package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder
public class BillingCcui implements SelfValidation<BillingCcui> {

  private AddressCcui address;

  public BillingCcui(AddressCcui address) {
    this.address = address;
    this.validateSelf();
  }
}
