package uk.co.whitbread.payments.domain.logic.rule;

import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.VS;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.paymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.CardStatus;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class CardNotValidForRateTest {

  private final CardNotValidForRate rule = new CardNotValidForRate();

  @Test
  void givenPrepaymentAllowedFlag_returnsNewPIBA_POAEnabled() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    // check if new PIBA is disabled.
    result.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getPaymentOptions().get(1).isEnabled());
        });
  }
  @Test
  void givenPrepaymentAllowedFlag_returnsNewPIBA_POAEnabledForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    // check if new PIBA is disabled.
    result.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getPaymentOptions().get(1).isEnabled());
        });
  }
  @Test
  void calculate_runsDisablesStoredBusinessCentralCard() {
    // Arrange
    RuleData rule = RuleData.builder()
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PaymentPolicy.PAY_NOW))
            .build())
        .paymentMethods(List.of(
            paymentMethod(SAVED_CARD, VS, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL)
        ))
        .build();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    var mapOnCardOptionType = result
        .getPaymentMethods()
        .stream()
        .collect(groupingBy(PaymentMethod::getType));
    var savedCardPaymentMethod = mapOnCardOptionType.get(SAVED_CARD.name()).get(0);
    assertFalse(savedCardPaymentMethod.isEnabled());
    assertEquals(List.of(CardStatus.CARD_NOT_VALID_FOR_RATE.name()), savedCardPaymentMethod.getReasons());
    assertFalse(mapOnCardOptionType.get(NEW_CARD.name()).get(0).isEnabled());
  }
  @Test
  void calculate_runsDisablesStoredBusinessCentralCardForPibaEuro() {
    // Arrange
    RuleData rule = RuleData.builder()
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PaymentPolicy.PAY_NOW))
            .build())
        .paymentMethods(List.of(
            paymentMethod(SAVED_CARD, VS, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL)
        ))
        .isPibaEuroEnabled(Boolean.TRUE)
        .build();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    var mapOnCardOptionType = result
        .getPaymentMethods()
        .stream()
        .collect(groupingBy(PaymentMethod::getType));
    var savedCardPaymentMethod = mapOnCardOptionType.get(SAVED_CARD.name()).get(0);
    assertFalse(savedCardPaymentMethod.isEnabled());
    assertEquals(List.of(CardStatus.CARD_NOT_VALID_FOR_RATE.name()), savedCardPaymentMethod.getReasons());
    assertFalse(mapOnCardOptionType.get(NEW_CARD.name()).get(0).isEnabled());
  }
}