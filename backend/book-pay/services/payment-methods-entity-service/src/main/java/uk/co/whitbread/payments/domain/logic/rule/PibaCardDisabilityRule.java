package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.PIBA_EU_ALLOWED_ONLY_IN_EU;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.PIBA_UK_ALLOWED_ONLY_IN_UK;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.PibaCard.isPibaEuInInvalidCountry;
import static uk.co.whitbread.payments.domain.model.out.PibaCard.isPibaUkInInvalidCountry;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class PibaCardDisabilityRule implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule PibaCardDisabilityRule");
    disableForNewPibaCard(request);
    disableForSavedPibaCard(request);
    return request;
  }

  private void disableForSavedPibaCard(RuleData request) {
    log.debug("disabling for saved PIBA cards");
    request.getPaymentMethods()
            .stream()
            .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
            .filter(paymentMethod -> isBusinessCard(paymentMethod.getCard().getType()))
            .forEach(paymentMethod -> disablePibaCardRule(request, paymentMethod));
  }

  private void disableForNewPibaCard(RuleData request) {
    log.debug("disabling for new PIBA cards");
    request.getPaymentMethods()
            .stream()
            .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
            .forEach(paymentMethod -> disablePibaCardRule(request, paymentMethod));
  }

  private void disablePibaCardRule(RuleData request, PaymentMethod paymentMethod) {
    if (isPibaUkInInvalidCountry(paymentMethod.getSubType(), request.getCountry())) {
      paymentMethod.disablePaymentMethod(PIBA_UK_ALLOWED_ONLY_IN_UK.name());
    } else if (isPibaEuInInvalidCountry(paymentMethod.getSubType(), request.getCountry())) {
      paymentMethod.disablePaymentMethod(PIBA_EU_ALLOWED_ONLY_IN_EU.name());
    }
  }

}