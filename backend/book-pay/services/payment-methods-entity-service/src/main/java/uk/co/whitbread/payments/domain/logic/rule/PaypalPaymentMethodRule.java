package uk.co.whitbread.payments.domain.logic.rule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class PaypalPaymentMethodRule implements Rule {
  private static final String PAYPAL_NAME = "PAYPAL";


  @Override
  public RuleData calculate(RuleData request) {
    if (!request.getPaypalRequest().isPaypalEnabled()
        || request.getPaypalRequest().getClientToken().isEmpty()) {
      //Remove Paypal From Payment Methods
      var paymentMethods = request.getPaymentMethods();
      paymentMethods.removeIf(paymentMethod -> StringUtils.equalsIgnoreCase(PAYPAL_NAME, paymentMethod.getName()));
    } else {
      var paypalPaymentMethodsConfiguration = request.getPaymentMethodsConfiguration().stream()
          .filter(dto -> StringUtils.equalsIgnoreCase(PAYPAL_NAME, dto.getName())).findFirst();
      for (PaymentMethod i : request.getPaymentMethods()) {
        if (i.getType().equalsIgnoreCase(PAYPAL_NAME) && paypalPaymentMethodsConfiguration.isPresent()) {
          i.setClientToken(request.getPaypalRequest().getClientToken());
          i.setClientId(request.getPaypalRequest().getClientId());
          i.getPaymentOptions().forEach(dto ->
              dto.setEnabled(paypalPaymentMethodsConfiguration.get().getSupportedBookingTypes()
                  .contains(dto.getType())));
        }
      }
    }
    return request;
  }
}