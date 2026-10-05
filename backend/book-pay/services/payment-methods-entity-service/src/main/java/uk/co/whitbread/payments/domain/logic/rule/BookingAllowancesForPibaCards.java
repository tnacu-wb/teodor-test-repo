package uk.co.whitbread.payments.domain.logic.rule;

import static java.util.Objects.nonNull;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_PERSONAL_STORED_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class BookingAllowancesForPibaCards implements Rule {


  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rules]: executing rule BookingAllowancesForPibaCards");
    if (nonNull(request.getBookingAllowances())) {
      enableBookingAllowancesNewPibaCard(request);
      enableBookingAllowancesPersonalStoredPibaCard(request);
      enableBookingAllowancesCentrallyStoredPibaCard(request);
    }
    return request;
  }

  private void enableBookingAllowancesNewPibaCard(RuleData request) {
    log.debug("enabling booking allowances for new PIBA cards");
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(
            paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()));
  }

  private void enableBookingAllowancesPersonalStoredPibaCard(RuleData request) {
    log.debug("enabling booking allowances for personal stored PIBA cards");
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
              && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
              && (isBusinessCard(paymentMethod.getCard().getType())))
          .forEach(
              paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()));
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
              && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
              && (AT.name().equals(paymentMethod.getCard().getType())
              || PI.name().equals(paymentMethod.getCard().getType())))
          .forEach(
              paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()));
    }
  }

  private void enableBookingAllowancesCentrallyStoredPibaCard(RuleData request) {
    log.debug("enabling booking allowances for centrally stored PIBA cards");
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
              && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
              && (isBusinessCard(paymentMethod.getCard().getType())))
          .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
          .forEach(
              paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()));
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType())
              && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
              && (AT.name().equals(paymentMethod.getCard().getType())
              || PI.name().equals(paymentMethod.getCard().getType())))
          .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
          .forEach(
              paymentMethod -> paymentMethod.setBookingAllowances(request.getBookingAllowances()));
    }
  }
}
