package uk.co.whitbread.payments.domain.ports.primary;

import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.in.SelectedPaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;

public interface PaymentMethodsPort {
  PaymentMethods getPaymentMethods(final PaymentMethodsRequest request);

  void validatePaymentMethods(SelectedPaymentMethods method);
}
