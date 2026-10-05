package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.RESERVE_WITHOUT_CARD;

import uk.co.whitbread.payments.domain.model.out.RuleData;

public class ReserveWithoutCardBookingAllowed implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    boolean isReserveWithoutCardBookingAllowed = request.getReservation().getHotelPaymentPolicies()
        .contains(RESERVE_WITHOUT_CARD);
    if (!isReserveWithoutCardBookingAllowed) {
      var paymentMethods = request.getPaymentMethods();
      paymentMethods.forEach(method -> {
        var paymentOptions = method.getPaymentOptions()
            .stream()
            .filter(option -> !option.getType().equals(RESERVE_WITHOUT_CARD.name()))
            .toList();
        method.setPaymentOptions(paymentOptions);
      });
    }
    return request;
  }
}
