package uk.co.whitbread.payments.domain.ports.primary;

import uk.co.whitbread.payments.domain.model.out.PaymentActionsResponse;

public interface PaymentActionsPort {
  PaymentActionsResponse determinePaymentActions(String basketReference);
}

