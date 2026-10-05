package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;

import java.util.Collection;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

public class PrepaymentAllowed implements Rule {
  @Override
  public RuleData calculate(RuleData request) {
    boolean isPrepaymentAllowed = request.getReservation().getHotelPaymentPolicies().contains(PAY_NOW);
    if (!isPrepaymentAllowed) {
      var paymentMethods = request.getPaymentMethods();
      paymentMethods.stream()
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
          .forEach(paymentOption -> paymentOption.setEnabled(false));
    }
    return request;
  }
}
