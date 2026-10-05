package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.PIBA_ALLOWED_ONLY_IN_UK;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class PibaCardNotAllowedInGermanHotelTest {

  private final PibaCardNotAllowedInGermanHotel rule = new PibaCardNotAllowedInGermanHotel();

  @Test
  void calculate__newPIBANotAllowedInDE() {
    // Arrange
    RuleData ruleData = createRequestWithNewCardPaymentMethod(Country.DE);

    // Act
    var request = rule.calculate(ruleData);

    // Assert
    request.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(Collections.singletonList(PIBA_ALLOWED_ONLY_IN_UK.name()),
              paymentMethod.getReasons());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
        });
  }

  @Test
  void calculate__PIBAAllowedInGB() {
    // Arrange
    RuleData ruleData = createRequestWithNewCardPaymentMethod(Country.GB);

    // Act
    var request = rule.calculate(ruleData);

    // Assert
    request.getPaymentMethods().forEach(paymentMethod -> {
      assertTrue(paymentMethod.isEnabled());
      assertTrue(paymentMethod.getReasons().isEmpty());
      paymentMethod.getPaymentOptions()
          .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
    });
  }

  @Test
  void calculate__savedPIBANotAllowedInDE() {
    // Arrange
    RuleData ruleData = RuleDataTest.createRequestWithSavedCardPaymentMethod(Country.DE);

    // Act
    var request = rule.calculate(ruleData);

    // Assert
    request.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
        (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(Collections.singletonList(PIBA_ALLOWED_ONLY_IN_UK.name()),
              paymentMethod.getReasons());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
        });
  }

  private RuleData createRequestWithNewCardPaymentMethod(Country country) {
    return RuleData.builder()
        .country(country)
        .paymentMethods(List.of(PaymentMethodsTestUtils.getNewCardPaymentMethod()))
        .build();
  }

}