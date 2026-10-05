package uk.co.whitbread.payments.domain.logic.rule.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.*;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class LeisureNewCardRuleExecutorTest {

  @Test
  void givenARequest_PaymentMethodsReturnedAcceptedCardList() {
    // Arrange
    LeisureNewCardRuleExecutor leisureNewCardRuleExecutor =
        new LeisureNewCardRuleExecutor(
            RuleDataTest.createRequestWithNewCardPaymentMethodAndCountry());

    // Act
    var result = leisureNewCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentMethod = result.get().getPaymentMethods().get(0);
    assertTrue(paymentMethod.isEnabled());
    assertEquals(RuleDataTest.getThreecAcceptedCardTypes(), paymentMethod.getAcceptedCardTypes());
  }

  @Test
  void givenARequestListIsReturnedWithNewPIBADisabledForDE() {
    // Arrange
    LeisureNewCardRuleExecutor leisureNewCardRuleExecutor =
        new LeisureNewCardRuleExecutor(
            RuleDataTest.createRequestWithNewPibaPaymentMethod());

    // Act
    var result = leisureNewCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentMethod = result.get().getPaymentMethods().get(0);
    assertFalse(paymentMethod.isEnabled());
    assertEquals(Collections.singletonList(PIBA_ALLOWED_ONLY_IN_UK.name()),
        paymentMethod.getReasons());
  }

  @Test
  void givenARequestAnListIsReturnedWithPIBADisabledForPayNow() {
    // Arrange
    LeisureNewCardRuleExecutor leisureNewCardRuleExecutor =
        new LeisureNewCardRuleExecutor(
            RuleDataTest.createRequestWithNewPibaPaymentMethod());

    // Act
    var result = leisureNewCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentOption = result.get().getPaymentMethods().get(0).getPaymentOptions().get(0);
    assertFalse(paymentOption.isEnabled());
  }

  // FOR PIBA EURO

  @Test
  void givenARequest_PaymentMethodsReturnedAcceptedCardListForPibaEuro() {
    // Arrange
    LeisureNewCardRuleExecutor leisureNewCardRuleExecutor =
        new LeisureNewCardRuleExecutor(
            RuleDataTest.createRequestWithNewCardPaymentMethodAndCountryForPibaEuro());

    // Act
    var result = leisureNewCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentMethod = result.get().getPaymentMethods().get(0);
    assertTrue(paymentMethod.isEnabled());
    assertEquals(RuleDataTest.getThreecAcceptedCardTypes(), paymentMethod.getAcceptedCardTypes());
  }

  @Test
  void givenARequestListIsReturnedWithNewPIBADisabledForDEForPibaEuro() {
    // Arrange
    LeisureNewCardRuleExecutor leisureNewCardRuleExecutor =
        new LeisureNewCardRuleExecutor(
            RuleDataTest.createRequestWithNewPibaPaymentMethodForPibaEuro());

    // Act
    var result = leisureNewCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentMethod = result.get().getPaymentMethods().get(0);
    assertFalse(paymentMethod.isEnabled());
    assertEquals(Collections.singletonList(PIBA_UK_ALLOWED_ONLY_IN_UK.name()),
        paymentMethod.getReasons());
  }

  @Test
  void givenARequestAnListIsReturnedWithPIBADisabledForPayNowForPibaEuro() {
    // Arrange
    LeisureNewCardRuleExecutor leisureNewCardRuleExecutor =
        new LeisureNewCardRuleExecutor(
            RuleDataTest.createRequestWithNewPibaPaymentMethodForPibaEuro());

    // Act
    var result = leisureNewCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentOption = result.get().getPaymentMethods().get(0).getPaymentOptions().get(0);
    assertFalse(paymentOption.isEnabled());
  }

}