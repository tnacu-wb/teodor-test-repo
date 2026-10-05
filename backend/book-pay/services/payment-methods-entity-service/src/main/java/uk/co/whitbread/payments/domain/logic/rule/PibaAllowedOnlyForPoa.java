package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class PibaAllowedOnlyForPoa implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule PibaAllowedOnlyForPOA");
    disableForNewPibaCard(request);
    disableForSavedPibaCard(request);
    //disableForRate(request);
    return request;
  }

  private void disableForSavedPibaCard(RuleData request) {
    log.debug("disabling for saved PIBA cards");
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
          .filter(paymentMethod -> isBusinessCard(paymentMethod.getCard().getType()))
          .forEach(this::disablePibaCardForPayNow);
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
          .filter(paymentMethod -> AT.name().equals(paymentMethod.getCard().getType())
              || PI.name().equals(paymentMethod.getCard().getType()))
          .forEach(this::disablePibaCardForPayNow);
    }
  }

  private void disableForNewPibaCard(RuleData request) {
    log.debug("disabling for new PIBA cards");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(this::disablePibaCardForPayNow);
  }

  private void disablePibaCardForPayNow(PaymentMethod paymentMethod) {
    paymentMethod.getPaymentOptions()
        .stream()
        .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
        .forEach(paymentOption -> paymentOption.setEnabled(false));
  }

  /*
  private void disableForRate(RuleData request) {
    boolean isPrepaymentRequired =
        request.getReservation().getHotelPaymentPolicies().equals(Set.of(PAY_NOW));
    if (isPrepaymentRequired) {
      request.getPaymentMethods().stream()
          .filter(paymentMethod -> NEW_PIBA.name().equals(paymentMethod.getType()))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false)));
    }
  }
  */
}
