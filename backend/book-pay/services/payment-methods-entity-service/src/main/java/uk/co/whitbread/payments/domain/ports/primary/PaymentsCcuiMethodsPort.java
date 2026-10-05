package uk.co.whitbread.payments.domain.ports.primary;

import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;

public interface PaymentsCcuiMethodsPort {
  PaymentMethods getPaymentMethods(final PaymentMethodsRequest request);
}
