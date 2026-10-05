package uk.co.whitbread.payments.domain.model.in;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PaymentMethodsFlow {
  CHECKINONLINE("CheckInOnline");

  private final String paymentMethod;

  PaymentMethodsFlow(String value) {
    this.paymentMethod = value;
  }

  public String value() {
    return paymentMethod;
  }

  public static PaymentMethodsFlow valueOfPaymentMethod(String value) {

    if (value == null || value.isBlank()) {
      return null;
    }
    for (PaymentMethodsFlow flow : values()) {
      if (flow.paymentMethod.equals(value)) {
        return flow;
      }
    }
    log.warn("No enum constant with value {} in PaymentMethodsFlow", value);
    return null;
  }
}
