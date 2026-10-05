package uk.co.whitbread.payments.domain.logic.rule;

import static java.util.Objects.nonNull;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class BookingAllowancesForCentrallyStoredNonPibaCards implements Rule {


  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rules]: executing rule BookingAllowancesForCentrallyStoredNonPibaCards");
    if (nonNull(request.getBookingAllowances())) {
      if (request.isPibaEuroEnabled()) {
        request.getPaymentMethods()
            .stream()
            .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
                && BUSINESS_CENTRALLY_STORED_CARD.name()
                .equals(paymentMethod.getCard().getCardType())
                && !isBusinessCard(paymentMethod.getCard().getType()))
            .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
            .forEach(
                paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()
                ));
      } else {
        request.getPaymentMethods()
            .stream()
            .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
                && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
                && !AT.name().equals(paymentMethod.getCard().getType())
                && !PI.name().equals(paymentMethod.getCard().getType()))
            .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
            .forEach(
                paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()
                ));
      }
    }
    return request;
  }

}
