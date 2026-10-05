package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.EXPIRED;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class ExpiredCardTest {

  private final ExpiredCard rule = new ExpiredCard();

  @Test
  void calculate__cardsExpired() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
            (paymentMethod.getCard().getToken().equals("1234568844777") ||
                paymentMethod.getCard().getToken().equals("1111222233444")))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(Collections.singletonList(EXPIRED.name()),
              paymentMethod.getReasons());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
        });
  }

  @Test
  void calculate__cardsNotExpired() {
    // Arrange
    RuleData ruleData = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(ruleData);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
            !(paymentMethod.getCard().getToken().equals("1234568844777") ||
                paymentMethod.getCard().getToken().equals("1111222233444")))
        .findFirst().ifPresent(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().isEmpty());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
        });
  }

}
