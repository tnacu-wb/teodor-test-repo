package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.RuleData;


@Slf4j
public class PibaEuDisabledForUKCountry implements Rule {

  public static final String PIBA_EU_NAME = "PIBA EU";

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule PibaUKForUKHotels");
    disablePibaEuForUkCountry(request);
    disablePoaForPibaEuWhenUkCountry(request);
    return request;
  }

  private void disablePoaForPibaEuWhenUkCountry(RuleData request) {
    log.debug("Disabling POA for Piba EU");
    if (Country.GB.name().equals(request.getCountry().name())) {
      request.getPaymentMethods().stream()
          .filter(paymentMethod -> PIBA_EU_NAME.equalsIgnoreCase(paymentMethod.getName()))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false)));
    }
  }

  private void disablePibaEuForUkCountry(RuleData request) {
    log.debug("Disabling Piba EU for UK hotels");
    if (Country.GB.name().equals(request.getCountry().name())) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> PIBA_EU_NAME.equalsIgnoreCase(paymentMethod.getName()))
          .forEach(paymentMethod -> paymentMethod.setEnabled(false));
    }
  }
}
