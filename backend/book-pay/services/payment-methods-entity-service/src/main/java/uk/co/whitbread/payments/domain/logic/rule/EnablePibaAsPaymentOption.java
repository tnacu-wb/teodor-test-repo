package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.RuleData;


@Slf4j
public class EnablePibaAsPaymentOption implements Rule {

  public static final String PIBA_UK_NAME = "PIBA UK";
  public static final String RATE_PLAN_CODE_FLEXRATE = "FLEXRATE";

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule PibaPoaEnableAsPaymentOption");
    disablePoaAsPaymentOptionForNewCard(request);
    enablePibaUk(request);
    enablePoaForPibaUk(request);
    disablePnForPibaUk(request);
    return request;
  }

  private void disablePoaAsPaymentOptionForNewCard(RuleData request) {
    log.debug("Disabling POA as payment option for NEW_CARD when rate plan code is different then FLEXRATE.");
    if (Country.GB.name().equals(request.getCountry().name())
        && !RATE_PLAN_CODE_FLEXRATE.equalsIgnoreCase(request.getReservation().getRatePlanCode())) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false)));
    }
  }

  private void enablePoaForPibaUk(RuleData request) {
    log.debug("Enabling POA for Piba Uk.");
    if (Country.GB.name().equals(request.getCountry().name())) {
      request.getPaymentMethods().stream()
          .filter(paymentMethod -> PIBA_UK_NAME.equalsIgnoreCase(paymentMethod.getName()))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(true)));
    }
  }

  private void enablePibaUk(RuleData request) {
    log.debug("Enabling PIBA UK as payment method for UK countries.");
    if (Country.GB.name().equals(request.getCountry().name())) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> PIBA_UK_NAME.equalsIgnoreCase(paymentMethod.getName()))
          .forEach(paymentMethod -> paymentMethod.setEnabled(true));
    }
  }

  private void disablePnForPibaUk(RuleData request) {
    log.debug("Disabling PN for PIBA Uk.");
    if (Country.GB.name().equals(request.getCountry().name())) {
      request.getPaymentMethods().stream()
          .filter(paymentMethod -> PIBA_UK_NAME.equalsIgnoreCase(paymentMethod.getName()))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false)));
    }
  }
}