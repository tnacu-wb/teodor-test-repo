package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class PaymentMethodsForHubHotelRule implements Rule {

  private static final String HUB_HOTEL_BRAND = "HUB";

  @Override
  public RuleData calculate(RuleData request) {

    log.debug("[Rules]: executing rule PaymentMethodsForHubHotelRule");
    disablePayNowForNewPiba(request);
    disablePayNowForStoredPiba(request);
    disablePoaForNormalNewCards(request);
    disablePoaForNormalStoredCards(request);
    return request;
  }

  private void disablePayNowForNewPiba(RuleData request) {
    if (HUB_HOTEL_BRAND.equals(request.getHotelBrand())) {

      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> NEW_PIBA.name().equals(paymentMethod.getType()))
          .forEach(this::disablePayNow);
    }
  }

  private void disablePayNowForStoredPiba(RuleData request) {
    if (HUB_HOTEL_BRAND.equals(request.getHotelBrand())) {

      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
          .filter(paymentMethod -> isBusinessCard(paymentMethod.getCard().getType()))
          .forEach(this::disablePayNow);
    }
  }

  private void disablePayNow(PaymentMethod paymentMethod) {
    paymentMethod.getPaymentOptions()
        .stream()
        .filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
        .forEach(paymentOption -> paymentOption.setEnabled(false));
  }

  private void disablePoaForNormalNewCards(RuleData request) {
    if (HUB_HOTEL_BRAND.equals(request.getHotelBrand()) && !request.isPoaForHubEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> NEW_CARD.name().equals(paymentMethod.getType()))
          .forEach(this::disablePoa);
    }
  }

  private void disablePoaForNormalStoredCards(RuleData request) {
    if (HUB_HOTEL_BRAND.equals(request.getHotelBrand()) && !request.isPoaForHubEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
          .filter(paymentMethod -> !isBusinessCard(paymentMethod.getCard().getType()))
          .forEach(this::disablePoa);
    }
  }

  private void disablePoa(PaymentMethod paymentMethod) {
    paymentMethod.getPaymentOptions()
        .stream()
        .filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
        .forEach(paymentOption -> paymentOption.setEnabled(false));
  }
}
