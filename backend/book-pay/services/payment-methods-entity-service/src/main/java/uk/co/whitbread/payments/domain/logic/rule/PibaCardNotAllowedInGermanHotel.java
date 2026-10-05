package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.PIBA_ALLOWED_ONLY_IN_UK;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.BD;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.Country.GB;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class PibaCardNotAllowedInGermanHotel implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule PibaCardNotAllowedInGermanHotel");
    disableForNewPibaCard(request);
    disableForSavedPibaCard(request);
    return request;
  }

  private void disableForSavedPibaCard(RuleData request) {
    log.debug("disabling for saved PIBA cards");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
        .filter(paymentMethod -> AT.name().equals(paymentMethod.getCard().getType())
            || PI.name().equals(paymentMethod.getCard().getType())
            || BD.name().equals(paymentMethod.getCard().getType()))
        .forEach(paymentMethod -> disablePibaCardForNotUkCountries(request, paymentMethod));
  }

  private void disableForNewPibaCard(RuleData request) {
    log.debug("disabling for new PIBA cards");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> disablePibaCardForNotUkCountries(request, paymentMethod));
  }

  private void disablePibaCardForNotUkCountries(RuleData request, PaymentMethod paymentMethod) {
    if (!GB.equals(request.getCountry())) {
      paymentMethod.disablePaymentMethod(PIBA_ALLOWED_ONLY_IN_UK.name());
    }
  }
}
