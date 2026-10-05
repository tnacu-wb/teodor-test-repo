package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

public class PaymentMethodsForHubHotelRuleTest {

  private final PaymentMethodsForHubHotelRule rule = new PaymentMethodsForHubHotelRule();

  @Test
  void calculate__disablePayNowForNewPiba() {
    //Arrange
    RuleData ruleData = RuleDataTest.createRequestWithNewCardForHubHotels();

    //Act
    var result = this.rule.calculate(ruleData);

    //Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> NEW_PIBA.name().equals(paymentMethod.getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void calculate__disablePayNowForStoredPiba() {
    //Arrange
    RuleData ruleData = RuleDataTest.createRequestWithStoredCardForHubHotels();

    //Act
    var result = this.rule.calculate(ruleData);

    //Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
        .filter(paymentMethod -> isBusinessCard(paymentMethod.getCard().getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void calculate__disablePoaForNormalNewCards() {
    //Arrange
    RuleData ruleData = RuleDataTest.createRequestWithNewCardForHubHotels();

    //Act
    var result = this.rule.calculate(ruleData);

    //Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> NEW_CARD.name().equals(paymentMethod.getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void calculate__disablePoaForNormalStoredCards() {
    //Arrange
    RuleData ruleData = RuleDataTest.createRequestWithStoredCardForHubHotels();

    //Act
    var result = this.rule.calculate(ruleData);

    //Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
        .filter(paymentMethod -> !isBusinessCard(paymentMethod.getCard().getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void calculate__enablePoaForNormalNewCards_whenFeatureFlagEnabled() {
    //Arrange
    RuleData ruleData = RuleDataTest.createRequestWithNewCardForHubHotels();
    ruleData.setPoaForHubEnabled(true);

    //Act
    var result = this.rule.calculate(ruleData);

    //Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> NEW_CARD.name().equals(paymentMethod.getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
  }

  @Test
  void calculate__enablePoaForNormalStoredCards_whenFeatureFlagEnabled() {
    //Arrange
    RuleData ruleData = RuleDataTest.createRequestWithStoredCardForHubHotels();
    ruleData.setPoaForHubEnabled(true);

    //Act
    var result = this.rule.calculate(ruleData);

    //Assert
    result.getPaymentMethods().stream()
        .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()))
        .filter(paymentMethod -> !isBusinessCard(paymentMethod.getCard().getType()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream().filter(paymentOption -> PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
  }


}
