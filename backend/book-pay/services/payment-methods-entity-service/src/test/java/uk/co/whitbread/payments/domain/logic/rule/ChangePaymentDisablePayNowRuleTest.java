package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils;
import uk.co.whitbread.payments.domain.model.out.RuleData;

class ChangePaymentDisablePayNowRuleTest {

  private static final String PAY_NOW = "PAY_NOW";
  private final ChangePaymentDisablePayNowRule rule = new ChangePaymentDisablePayNowRule();

  @Test
  void rule_DisablePayNowOption() {
    // Arrange
    RuleData ruleData = RuleData.builder()
        .country(Country.DE)
        .changePaymentBIC(true)
        .paymentMethods(
            List.of(PaymentMethodsTestUtils.getGooglePaymentMethod(),
                PaymentMethodsTestUtils.getNewCardPaymentMethod(),
                PaymentMethodsTestUtils.getApplePaymentMethod()
            )
        )
        .build();

    // Act
    var request = rule.calculate(ruleData);

    // Assert
    request.getPaymentMethods()
        .stream()
        .forEach(paymentMethod -> {
          paymentMethod.getPaymentOptions().stream()
              .filter(paymentOption -> PAY_NOW.equalsIgnoreCase(paymentOption.getType()))
              .forEach(paymentOption ->
                  assertFalse(paymentOption.isEnabled())
              );
        });
  }

  @Test
  void rule_EnablePayNowOption() {
    // Arrange
    RuleData ruleData = RuleData.builder()
        .country(Country.DE)
        .changePaymentBIC(false)
        .paymentMethods(
            List.of(PaymentMethodsTestUtils.getGooglePaymentMethod(),
                PaymentMethodsTestUtils.getNewCardPaymentMethod(),
                PaymentMethodsTestUtils.getApplePaymentMethod()
            )
        )
        .build();

    // Act
    var request = rule.calculate(ruleData);

    // Assert
    request.getPaymentMethods()
        .stream()
        .forEach(paymentMethod -> {
          paymentMethod.getPaymentOptions().stream()
              .filter(paymentOption -> PAY_NOW.equalsIgnoreCase(paymentOption.getType()))
              .forEach(paymentOption ->
                  assertTrue(paymentOption.isEnabled())
              );
        });
  }


}