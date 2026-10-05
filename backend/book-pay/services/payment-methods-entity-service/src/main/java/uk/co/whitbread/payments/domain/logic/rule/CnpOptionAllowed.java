package uk.co.whitbread.payments.domain.logic.rule;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.LEISURE_STORED_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;

/**
 * Sets a flag to determine whether this payment method allows a card holder not present option.
 * False by default and the rule is only applying to the scenarios where the flag needs to be true.
 */
@Slf4j
public class CnpOptionAllowed implements Rule {


  @Override
  public RuleData calculate(RuleData request) {
    handleNewPibaCard(request);
    handleSavedLeisurePibaCard(request);
    return request;
  }

  private void handleNewPibaCard(RuleData request) {
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> NEW_PIBA.name().equals(paymentMethod.getType()))
        .forEach(paymentMethod -> paymentMethod.setCnpOptionAvailable(true));
  }

  private void handleSavedLeisurePibaCard(RuleData request) {
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> ofNullable(paymentMethod.getCard()).isPresent()
              && LEISURE_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
          .filter(paymentMethod -> isBusinessCard(paymentMethod.getCard().getType()))
          .forEach(paymentMethod -> paymentMethod.setCnpOptionAvailable(true));
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> ofNullable(paymentMethod.getCard()).isPresent()
              && LEISURE_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
          .filter(paymentMethod -> AT.name().equals(paymentMethod.getCard().getType())
              || PI.name().equals(paymentMethod.getCard().getType()))
          .forEach(paymentMethod -> paymentMethod.setCnpOptionAvailable(true));
    }
  }
}
