package uk.co.whitbread.payments.domain.logic.rule;

import static java.lang.Integer.parseInt;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.EXPIRED;

import java.time.LocalDate;
import java.time.YearMonth;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class ExpiredCard implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule ExpiredCard");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
        .forEach(this::disableCardIfExpired);
    return request;
  }

  private void disableCardIfExpired(PaymentMethod paymentMethod) {
    var card = paymentMethod.getCard();
    LocalDate today = LocalDate.now();
    var expiryMonth = card.getExpiryMonth();
    var expiryYear = "20" + card.getExpiryYear();
    YearMonth cardExpiry = YearMonth.of(parseInt(expiryYear), parseInt(expiryMonth));
    if (today.isAfter(cardExpiry.atEndOfMonth())) {
      paymentMethod.disablePaymentMethod(EXPIRED.name());
    }
  }
}