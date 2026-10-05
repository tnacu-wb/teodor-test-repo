package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.RESERVE_WITHOUT_CARD;

import uk.co.whitbread.payments.domain.model.out.RuleData;

public class NonGuaranteedBookingAllowed implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    boolean isReserveWithoutCardBookingAllowed = request.getReservation().getHotelPaymentPolicies()
        .contains(RESERVE_WITHOUT_CARD);
    if (!isReserveWithoutCardBookingAllowed) {
      var paymentMethods = request.getPaymentMethods();
      paymentMethods.stream()
          .filter(paymentOption -> paymentOption.getType().equals(RESERVE_WITHOUT_CARD.name()))
          .forEach(paymentOption -> {
            paymentOption.setEnabled(false);
            paymentOption.getPaymentOptions()
                .forEach(paymentOption1 -> paymentOption1.setEnabled(false));
          });
    }
    return request;
  }
}
