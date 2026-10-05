package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.PERSONAL_STORED_CARD_DISABLED;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_PERSONAL_STORED_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;


@Slf4j
public class CompanyDisabledPersonalStoredCard implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rules]: executing rule CompanyDisabledPersonalStoredCard");
    if (!request.isAllowIndividualCards()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
              && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
          .forEach(paymentMethodFiltered -> paymentMethodFiltered
              .disablePaymentMethod(PERSONAL_STORED_CARD_DISABLED.name()));
    }
    return request;
  }
}
