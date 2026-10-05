package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.AC;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.paymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class PibaAllowedOnlyForPOATest {

  private final PibaAllowedOnlyForPoa rule = new PibaAllowedOnlyForPoa();

  @Test
  void calculate__PIBADisabledForPayNowForNewCard() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void calculate__PIBAAllowedForPayNowForNewCard() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream()
            .filter(paymentOption -> paymentOption.getType().equals(PAY_ON_ARRIVAL.name()))
            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
  }

  @Test
  void calculate__disableForSavedPibaCard() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
        .filter(paymentMethod -> AT.name().equals(paymentMethod.getCard().getType())
            || PI.name().equals(paymentMethod.getCard().getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream()
            .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
  }

  @Test
  void calculate_runsDisablesStoredBusinessCentralCard() {
    // Arrange
    RuleData rule = RuleData.builder()
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PAY_NOW))
            .build())
        .paymentMethods(List.of(
            paymentMethod(NEW_PIBA, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AC, null, PAY_NOW)
        ))
        .build();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    var paymentMethods = result.getPaymentMethods();
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(1).getPaymentOptions().get(0).isEnabled());
  }

  // FOR PIBA EURO

  @Test
  void calculate__PIBADisabledForPayNowForNewCardForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();
    rule.setPibaEuroEnabled(Boolean.TRUE);
    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void calculate__PIBAAllowedForPayNowForNewCardForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream()
            .filter(paymentOption -> paymentOption.getType().equals(PAY_ON_ARRIVAL.name()))
            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
  }

  @Test
  void calculate__disableForSavedPibaCardForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()))
        .filter(paymentMethod -> AT.name().equals(paymentMethod.getCard().getType())
            || PI.name().equals(paymentMethod.getCard().getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream()
            .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
  }

  @Test
  void calculate_runsDisablesStoredBusinessCentralCardForPibaEuro() {
    // Arrange
    RuleData rule = RuleData.builder()
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PAY_NOW))
            .build())
        .paymentMethods(List.of(
            paymentMethod(NEW_PIBA, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AC, null, PAY_NOW)
        ))
        .isPibaEuroEnabled(Boolean.TRUE)
        .build();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    var paymentMethods = result.getPaymentMethods();
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(1).getPaymentOptions().get(0).isEnabled());
  }
}