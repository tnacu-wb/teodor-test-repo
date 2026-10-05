package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.CARD_EXPIRED_BEFORE_DEPARTURE;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class CardExpiresBeforeDepartureDateTest {

  private final CardExpiresBeforeDepartureDate rule = new CardExpiresBeforeDepartureDate();

  @Test
  void calculate__cardsExpiredBeforeDeparture() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithDepartureDataExpired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
            (paymentMethod.getCard().getToken().equals("2222111133334444") ||
                paymentMethod.getCard().getToken().equals("3333111122224444")))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(Collections.singletonList(CARD_EXPIRED_BEFORE_DEPARTURE.name()),
              paymentMethod.getReasons());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
        });
  }

  @Test
  void calculate__cardsNotExpiredBeforeDeparture() {
    // Arrange
    RuleData ruleData = RuleDataTest.createRequestWithDepartureDataExpired();

    // Act
    var result = this.rule.calculate(ruleData);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
            !(paymentMethod.getCard().getToken().equals("2222111133334444") ||
                paymentMethod.getCard().getToken().equals("3333111122224444")))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().isEmpty());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
        });
  }

}