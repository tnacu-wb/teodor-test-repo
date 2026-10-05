package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.COMPANY_DISABLED_NEW_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class CompanyDisableNewCard implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule CompanyDisableNewCard");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> !paymentMethod.getType().equals(SAVED_CARD.name()))
        .forEach(paymentMethod -> disableNewCardIfNotApplicable(request, paymentMethod));
    return request;
  }

  private void disableNewCardIfNotApplicable(RuleData request, PaymentMethod paymentMethod) {
    var newCardAllowed = request.isAllowIndividualCards();
    if (!newCardAllowed) {
      paymentMethod.disablePaymentMethod(COMPANY_DISABLED_NEW_CARD.name());
    }
  }
}
