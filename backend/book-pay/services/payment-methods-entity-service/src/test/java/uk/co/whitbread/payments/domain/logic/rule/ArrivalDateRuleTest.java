package uk.co.whitbread.payments.domain.logic.rule;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrivalDateRuleTest {

    private final ArrivalDateRule rule = new ArrivalDateRule();

    @Test
    void givenPayNowDEHotel_returnTrue_ForFeatureFlagAsTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_NOW,
              LocalDate.now().plusDays(1), Country.DE,
              true, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.DE, result.getCountry());
        result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .forEach(paymentOption -> {
                  if (paymentOption.getType().equals(PaymentPolicy.PAY_NOW.name())) {
                      assertTrue(paymentOption.isEnabled());
                  }
              });
    }

    @Test
    void givenPayOnArrivalDEHotel_returnTrue_ForFeatureFlagAsTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
            PaymentMethodsTestUtils.getNewCardPaymentMethod(
                false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_ON_ARRIVAL,
            LocalDate.now().plusDays(1), Country.DE,
            true, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.DE, result.getCountry());
        result.getPaymentMethods()
            .stream()
            .map(PaymentMethod::getPaymentOptions)
            .flatMap(Collection::stream)
            .forEach(paymentOption -> {
                if (paymentOption.getType().equals(PaymentPolicy.PAY_ON_ARRIVAL.name())) {
                    assertTrue(paymentOption.isEnabled());
                }
            });
    }

    @Test
    void givenPayNowDEHotel_returnFalse_ForFeatureFlagAsFalse() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_NOW,
              LocalDate.now().plusDays(1), Country.DE,
              false, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.DE, result.getCountry());
        result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .forEach(paymentOption -> {
                  assertFalse(paymentOption.isEnabled());
              });
    }

    @Test
    void givenPayOnArrival_returnTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_ON_ARRIVAL,
              LocalDate.now().plusDays(1), Country.DE,
              false, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.DE, result.getCountry());
        PaymentOption paymentOpt = result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .filter(paymentOption -> PaymentPolicy.PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .findAny().orElseThrow();
        assertTrue(paymentOpt.isEnabled());
    }

    @Test
    void givenPayOnArrival_returnFalse() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    true, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(
              PaymentPolicy.PAY_ON_ARRIVAL,
              LocalDate.now().plusDays(4), Country.DE,
              false, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        PaymentOption paymentOpt = result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .filter(paymentOption -> PaymentPolicy.PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
              .findAny().orElseThrow();
        assertFalse(paymentOpt.isEnabled());
    }

    @Test
    void givenPayNowPolicyUKHotel_returnTrue_ForFeatureFlagAsTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_NOW,
              LocalDate.now().plusDays(1), Country.GB,
              false, true, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.GB, result.getCountry());
        result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .forEach(paymentOption -> {
                  if (paymentOption.getType().equals(PaymentPolicy.PAY_NOW.name())) {
                      assertTrue(paymentOption.isEnabled());
                  }
              });
    }

    @Test
    void givenPayOnArrivalPolicyUKHotel_returnTrue_ForFeatureFlagAsTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
            PaymentMethodsTestUtils.getNewCardPaymentMethod(
                false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_ON_ARRIVAL,
            LocalDate.now().plusDays(1), Country.GB,
            false, true, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.GB, result.getCountry());
        result.getPaymentMethods()
            .stream()
            .map(PaymentMethod::getPaymentOptions)
            .flatMap(Collection::stream)
            .forEach(paymentOption -> {
                if (paymentOption.getType().equals(PaymentPolicy.PAY_ON_ARRIVAL.name())) {
                    assertTrue(paymentOption.isEnabled());
                }
            });
    }

    @Test
    void givenPayOnArrivalPolicyUKHotel_returnTrue_ForFeatureFlagAsTrue_PIBA_UK_Card() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
            PaymentMethodsTestUtils.getPayOnArrivalPaymentMethod(
                false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_ON_ARRIVAL,
            LocalDate.now().plusDays(1), Country.GB,
            false, true, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.GB, result.getCountry());
        result.getPaymentMethods()
            .stream()
            .map(PaymentMethod::getPaymentOptions)
            .flatMap(Collection::stream)
            .forEach(paymentOption -> {
                if (paymentOption.getType().equals(PaymentPolicy.PAY_ON_ARRIVAL.name())) {
                    assertTrue(paymentOption.isEnabled());
                }
            });
    }


    @Test
    void givenPayNowUKHotel_returnFalse_FeatureFlagAsFalseButWithin72h() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_NOW,
              LocalDate.now().plusDays(1), Country.GB,
              false, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.GB, result.getCountry());
        result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .forEach(paymentOption -> {
                  assertFalse(paymentOption.isEnabled());
              });
    }

    @Test
    void givenPayNowUKHotel_returnTrue_FeatureFlagAsFalseButAfter72h() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
              PaymentMethodsTestUtils.getNewCardPaymentMethod(
                    true, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_NOW,
              LocalDate.now().plusDays(4), Country.GB,
              false, false, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.GB, result.getCountry());
        PaymentOption paymentOpt = result.getPaymentMethods()
              .stream()
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .filter(paymentOption -> PaymentPolicy.PAY_NOW.name().equals(paymentOption.getType()))
              .findAny().orElseThrow();
        assertTrue(paymentOpt.isEnabled());
    }

    @Test
    void givenPayNowPolicyIEHotel_returnTrue_ForFeatureFlagAsTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
            PaymentMethodsTestUtils.getNewCardPaymentMethod(
                false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_NOW,
            LocalDate.now().plusDays(1), Country.IE,
            false, true, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.IE, result.getCountry());
        result.getPaymentMethods()
            .stream()
            .map(PaymentMethod::getPaymentOptions)
            .flatMap(Collection::stream)
            .forEach(paymentOption -> {
                if (paymentOption.getType().equals(PaymentPolicy.PAY_NOW.name())) {
                    assertTrue(paymentOption.isEnabled());
                }
            });
    }

    @Test
    void givenPayOnArrivalPolicyIEHotel_returnTrue_ForFeatureFlagAsTrue() {
        // Arrange
        List<PaymentMethod> paymentMethods = List.of(
            PaymentMethodsTestUtils.getNewCardPaymentMethod(
                false, false));
        RuleData ruleDate = RuleDataTest.createRequestWithForPayments(PaymentPolicy.PAY_ON_ARRIVAL,
            LocalDate.now().plusDays(1), Country.IE,
            false, true, paymentMethods);

        // Act
        var result = this.rule.calculate(ruleDate);

        // Assert
        assertEquals(Country.IE, result.getCountry());
        result.getPaymentMethods()
            .stream()
            .map(PaymentMethod::getPaymentOptions)
            .flatMap(Collection::stream)
            .forEach(paymentOption -> {
                if (paymentOption.getType().equals(PaymentPolicy.PAY_ON_ARRIVAL.name())) {
                    assertTrue(paymentOption.isEnabled());
                }
            });
    }
}