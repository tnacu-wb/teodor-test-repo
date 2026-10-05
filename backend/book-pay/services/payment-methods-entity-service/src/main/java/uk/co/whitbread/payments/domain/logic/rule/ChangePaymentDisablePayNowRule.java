package uk.co.whitbread.payments.domain.logic.rule;


import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;
import uk.co.whitbread.payments.domain.model.out.RuleData;


/***
 *<p>Disable the pay now option when the payment change from non-guaranteed to Credit/Debit card.</p>
 */
@Slf4j
public class ChangePaymentDisablePayNowRule implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule ChangePaymentRule");
    disablePayNowRule(request);
    return request;
  }

  private void disablePayNowRule(RuleData request) {
    log.debug("disabling Pay Now method");
    request.getPaymentMethods()
        .stream()
        .forEach(
            paymentMethod -> disablePayNowOptionMethod(request, paymentMethod.getPaymentOptions()));
  }

  private void disablePayNowOptionMethod(RuleData request, List<PaymentOption> paymentOptions) {
    if (request.isChangePaymentBIC()) {
      paymentOptions.stream()
          .filter(paymentOption -> PAY_NOW.name().equalsIgnoreCase(paymentOption.getType()))
          .forEach(paymentOption -> paymentOption.setEnabled(false));
    }
  }

}