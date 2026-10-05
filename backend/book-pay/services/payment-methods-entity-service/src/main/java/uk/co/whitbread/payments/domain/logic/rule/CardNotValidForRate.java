package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.CARD_NOT_VALID_FOR_RATE;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class CardNotValidForRate implements Rule {


  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule CardNotValidForRate");
    boolean isPrepaymentRequired = request.getReservation()
        .getHotelPaymentPolicies().equals(Set.of(PAY_NOW));
    if (isPrepaymentRequired) {
      enableNonPibaCards(request);
      disableCentralStoredNonBacCards(request);
    }
    return request;
  }

  private void enableNonPibaCards(RuleData request) {
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> !NEW_PIBA.name().equals(paymentMethod.getType()))
          .filter(paymentMethod -> !(SAVED_CARD.name().equals(paymentMethod.getType())
              && (isBusinessCard(paymentMethod.getCard().getType()))))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false)));
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> !NEW_PIBA.name().equals(paymentMethod.getType()))
          .filter(paymentMethod -> !(SAVED_CARD.name().equals(paymentMethod.getType())
              && (AT.name().equals(paymentMethod.getCard().getType())
              || PI.name().equals(paymentMethod.getCard().getType()))))
          .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
              .stream()
              .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false)));
    }
  }

  private void disableCentralStoredNonBacCards(RuleData request) {
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
          .filter(paymentMethod -> (
              BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
                  && !(isBusinessCard(paymentMethod.getCard().getType()))))
          .forEach(
              paymentMethod -> paymentMethod.disablePaymentMethod(CARD_NOT_VALID_FOR_RATE.name()));
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
          .filter(paymentMethod -> (
              BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
                  && !(AT.name().equals(paymentMethod.getCard().getType())
                  || PI.name().equals(paymentMethod.getCard().getType()))))
          .forEach(
              paymentMethod -> paymentMethod.disablePaymentMethod(CARD_NOT_VALID_FOR_RATE.name()));
    }
  }
}
