package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.PAYPAL;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.CARD_NOT_ACCEPTED_AT_HOTEL;
import static uk.co.whitbread.payments.domain.model.out.CardType.AP;
import static uk.co.whitbread.payments.domain.model.out.CardType.BD;
import static uk.co.whitbread.payments.domain.model.out.CardType.GP;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.CardType.PP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.AcceptedCardType;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsConfiguration;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class AcceptedCardByHotel implements Rule {

  private static final List<String> newPaymentList = Arrays.asList(PP.name(), AP.name(), GP.name());

  private static boolean isValidPibaCard(AcceptedCardType acceptedCardType) {
    return PI.name().equals(acceptedCardType.getType())
        || BD.name().equals(acceptedCardType.getType());
  }

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule AcceptedCardByHotel");
    mapAcceptedCardsToNewCardPaymentMethod(request);
    mapAcceptedCardsToNewPibaPaymentMethod(request);
    mapAcceptedCardsToPaypalPaymentMethod(request);
    mapAcceptedCardsToApplePaymentMethod(request);
    mapAcceptedCardsToGooglePaymentMethod(request);
    disableSavedCardsPaymentMethodIfNotAcceptedAtHotel(request);
    return request;
  }

  private void disableSavedCardsPaymentMethodIfNotAcceptedAtHotel(RuleData request) {
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
        .forEach(paymentMethod -> disableCardIfNotAccepted(request, paymentMethod));
  }

  private void mapAcceptedCardsToNewCardPaymentMethod(RuleData request) {
    var cardSource = request.resolveCardSource(NEW_CARD.name());
    var provider = request.resolvePaymentProvider(NEW_CARD.name());
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> NEW_CARD.name().equals(paymentMethod.getType()))
          .forEach(paymentMethod -> {
            paymentMethod.setAcceptedCardTypes(
                cardSource.stream().filter(acceptedCardType -> !(isValidPibaCard(acceptedCardType)))
                    .filter(acceptedCardType -> !newPaymentList.contains(acceptedCardType.getType()))
                    .toList()
            );
            paymentMethod.setPaymentProvider(provider);
          });
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> NEW_CARD.name().equals(paymentMethod.getType()))
          .forEach(paymentMethod -> {
            paymentMethod.setAcceptedCardTypes(
                cardSource.stream().filter(acceptedCardType -> !PI.name().equals(acceptedCardType.getType()))
                    .filter(acceptedCardType -> !newPaymentList.contains(acceptedCardType.getType()))
                    .toList()
            );
            paymentMethod.setPaymentProvider(provider);
          });
    }
  }

  private void mapAcceptedCardsToNewPibaPaymentMethod(RuleData request) {
    var cardSource = request.resolveCardSource(NEW_PIBA.name());
    var provider = request.resolvePaymentProvider(NEW_PIBA.name());
    if (request.isPibaEuroEnabled()) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> NEW_PIBA.name().equals(paymentMethod.getType()))
          .forEach(paymentMethod -> {
            paymentMethod.setAcceptedCardTypes(
                cardSource.stream().filter(AcceptedCardByHotel::isValidPibaCard).toList()
            );
            paymentMethod.setPaymentProvider(provider);
          });
    } else {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> NEW_PIBA.name().equals(paymentMethod.getType()))
          .forEach(paymentMethod -> {
            paymentMethod.setAcceptedCardTypes(
                cardSource.stream().filter(acceptedCardType -> PI.name().equals(acceptedCardType.getType()))
                    .toList()
            );
            paymentMethod.setPaymentProvider(provider);
          });
    }
  }

  private void mapAcceptedCardsToPaypalPaymentMethod(RuleData request) {
    if (request.getPaymentMethodsConfiguration() != null) {
      request.getPaymentMethods()
          .stream()
          .filter(paymentMethod -> PAYPAL.name().equalsIgnoreCase(paymentMethod.getType()))
          .forEach(paymentMethod -> paymentMethod.setAcceptedCardTypes(
              resolveAcceptedCardType(
                  request.getPaymentMethodsConfiguration()
                      .stream().filter(acceptedCardType -> PP.name().equals(acceptedCardType.getCode()))
                      .findFirst().orElse(null))
          ));
    }
  }

  private void mapAcceptedCardsToApplePaymentMethod(RuleData request) {
    var cardSource = request.resolveCardSource(AP.name());
    var provider = request.resolvePaymentProvider(AP.name());
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> AP.name().equalsIgnoreCase(paymentMethod.getType()))
        .forEach(paymentMethod -> {
          paymentMethod.setAcceptedCardTypes(
              cardSource.stream()
                  .filter(acceptedCardType -> !PI.name().equals(acceptedCardType.getType()))
                  .filter(acceptedCardType -> !BD.name().equals(acceptedCardType.getType()))
                  .filter(acceptedCardType -> !newPaymentList.contains(acceptedCardType.getType()))
                  .toList()
          );
          paymentMethod.setPaymentProvider(provider);
        });
  }

  private void mapAcceptedCardsToGooglePaymentMethod(RuleData request) {
    var cardSource = request.resolveCardSource(GP.name());
    var provider = request.resolvePaymentProvider(GP.name());
    request.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> GP.name().equalsIgnoreCase(paymentMethod.getType()))
        .forEach(paymentMethod -> {
          paymentMethod.setAcceptedCardTypes(
              cardSource.stream()
                  .filter(acceptedCardType -> !PI.name().equals(acceptedCardType.getType()))
                  .filter(acceptedCardType -> !BD.name().equals(acceptedCardType.getType()))
                  .filter(acceptedCardType -> !newPaymentList.contains(acceptedCardType.getType()))
                  .toList()
          );
          paymentMethod.setPaymentProvider(provider);
        });
  }

  private void disableCardIfNotAccepted(RuleData request, PaymentMethod paymentMethod) {
    var savedCard = paymentMethod.getCard();
    var cardType = savedCard.getType();
    if (!checkCardTypePresent(request, cardType)) {
      paymentMethod.disablePaymentMethod(CARD_NOT_ACCEPTED_AT_HOTEL.name());
    }
    // setting logo url for stored cards.
    savedCard.setLogoSrc(getStoredCardLogo(request, cardType));
  }

  // TODO: DATATRANS — SAVED_CARD uses 3CP/Opera card codes (e.g. "VI"); Datatrans uses different
  //  codes (e.g. "VIS"). If SAVED_CARD is ever made Datatrans-eligible, this check will silently
  //  fail to match. A code-translation layer must be introduced before removing SAVED_CARD from
  //  datatrans.not-supported-card-options.
  private boolean checkCardTypePresent(RuleData request, String cardType) {
    return Stream.of(
            request.getAcceptedCardType(),
            request.getThreecAcceptedCardType()
        ).flatMap(Collection::stream)
        .anyMatch(acceptedCardType -> cardType != null && !"".equals(cardType)
            && cardType.equals(acceptedCardType.getType()));
  }

  // TODO: DATATRANS — same limitation as checkCardTypePresent: stored card logo resolution uses
  //  3CP/Opera card codes and will not match Datatrans codes.
  private String getStoredCardLogo(RuleData request, String cardType) {
    var card = Stream.of(
            request.getAcceptedCardType(),
            request.getThreecAcceptedCardType()
        ).flatMap(Collection::stream)
        .filter(acceptedCardType -> cardType != null && !"".equals(cardType)
            && cardType.equals(acceptedCardType.getType()))
        .findFirst();
    return card.isPresent() ? card.get().getLogoSrc() : "";
  }

  private List<AcceptedCardType> resolveAcceptedCardType(PaymentMethodsConfiguration pmc) {
    List<AcceptedCardType> result = null;
    AcceptedCardType act = new AcceptedCardType();
    if (pmc != null) {
      result = new ArrayList<>();
      act.setType(pmc.getCode());
      act.setName(pmc.getName());
      act.setLogoSrc(pmc.getLogo());
      result.add(act);
    }
    return result;
  }
}
