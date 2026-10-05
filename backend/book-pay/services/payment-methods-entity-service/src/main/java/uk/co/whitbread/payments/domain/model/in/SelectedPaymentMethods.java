package uk.co.whitbread.payments.domain.model.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;

@Data
@Builder
public class SelectedPaymentMethods {

  private String basketReference;

  private String type;

  private PaymentPolicy selectedPaymentOption;

  private Boolean isCiol;
}
