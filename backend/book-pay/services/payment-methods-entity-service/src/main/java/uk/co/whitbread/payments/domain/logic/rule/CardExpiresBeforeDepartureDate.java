package uk.co.whitbread.payments.domain.logic.rule;

import static java.lang.Integer.parseInt;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.CARD_EXPIRED_BEFORE_DEPARTURE;

import java.time.YearMonth;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.CardOption;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class CardExpiresBeforeDepartureDate implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule CardExpiresBeforeDepartureDate");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(CardOption.SAVED_CARD.name()))
        .forEach(paymentMethod -> disableCardIfExpired(request, paymentMethod));
    return request;
  }

  private void disableCardIfExpired(RuleData request, PaymentMethod paymentMethod) {
    var card = paymentMethod.getCard();
    var expiryMonth = card.getExpiryMonth();
    var expiryYear = "20" + card.getExpiryYear();
    YearMonth cardExpiry = YearMonth.of(parseInt(expiryYear), parseInt(expiryMonth));
    boolean cardExpiresBeforeDeparture = null != request.getReservation().getDepartureDate()
        && request.getReservation().getDepartureDate().isAfter(cardExpiry.atEndOfMonth());
    if (cardExpiresBeforeDeparture) {
      paymentMethod.disablePaymentMethod(CARD_EXPIRED_BEFORE_DEPARTURE.name());
    }
  }
}
