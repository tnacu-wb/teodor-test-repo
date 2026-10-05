package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class BusinessCentralCardsOnlyForPoa implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rules]: executing rule BusinessCentralCardsOnlyForPOA");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
            && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions().stream()
            .filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> paymentOption.setEnabled(false)));

    return request;
  }
}