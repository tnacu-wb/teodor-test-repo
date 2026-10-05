package uk.co.whitbread.payments.domain.logic.rule.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.CARD_EXPIRED_BEFORE_DEPARTURE;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.CARD_NOT_ACCEPTED_AT_HOTEL;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.EXPIRED;
import static uk.co.whitbread.payments.domain.model.out.CardType.AM;
import static uk.co.whitbread.payments.domain.model.out.CardType.VI;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;


class LeisureSavedCardRuleExecutorTest {

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardsEnabled() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && (paymentMethod.getCard().getType().equals(VI.name()) ||
            paymentMethod.getCard().getType().equals(AM.name())))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().isEmpty());
        });
  }

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardsDisabledAndNotAcceptedAtHotel() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && !(paymentMethod.getCard().getType().equals(VI.name()) ||
            paymentMethod.getCard().getType().equals(AM.name())))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().contains(CARD_NOT_ACCEPTED_AT_HOTEL.name()));
        });
  }

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardEnabled() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && !(paymentMethod.getCard().getToken().equals("2222111133334444") ||
            paymentMethod.getCard().getToken().equals("3333111122224444")))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().isEmpty());
        });
  }

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardsDisabledAndExpiredBeforeDeparture() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequestWithDepartureData());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && (paymentMethod.getCard().getToken().equals("2222111133334444") ||
            paymentMethod.getCard().getToken().equals("3333111122224444")))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().contains(EXPIRED.name()));
          assertTrue(paymentMethod.getReasons().contains(CARD_EXPIRED_BEFORE_DEPARTURE.name()));
        });
  }

  // FOR PIBA EURO

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardsEnabledForPibaEuro() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && (paymentMethod.getCard().getType().equals(VI.name()) ||
            paymentMethod.getCard().getType().equals(AM.name())))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().isEmpty());
        });
  }

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardsDisabledAndNotAcceptedAtHotelForPibaEuro() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && !(paymentMethod.getCard().getType().equals(VI.name()) ||
            paymentMethod.getCard().getType().equals(AM.name())))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().contains(CARD_NOT_ACCEPTED_AT_HOTEL.name()));
        });
  }

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardEnabledForPibaEuro() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && !(paymentMethod.getCard().getToken().equals("2222111133334444") ||
            paymentMethod.getCard().getToken().equals("3333111122224444")))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().isEmpty());
        });
  }

  @Test
  void givenARequest_returnsPaymentMethodsWithSavedCardsDisabledAndExpiredBeforeDepartureForPibaEuro() {
    // Arrange
    LeisureSavedCardRuleExecutor leisureSavedCardRuleExecutor =
        new LeisureSavedCardRuleExecutor(RuleDataTest.createRequestWithDepartureData());

    // Act
    var result = leisureSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && (paymentMethod.getCard().getToken().equals("2222111133334444") ||
            paymentMethod.getCard().getToken().equals("3333111122224444")))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().contains(EXPIRED.name()));
          assertTrue(paymentMethod.getReasons().contains(CARD_EXPIRED_BEFORE_DEPARTURE.name()));
        });
  }


}

