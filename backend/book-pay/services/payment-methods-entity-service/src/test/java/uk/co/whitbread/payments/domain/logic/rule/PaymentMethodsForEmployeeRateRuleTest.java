package uk.co.whitbread.payments.domain.logic.rule;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.isBusinessCard;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;

public class PaymentMethodsForEmployeeRateRuleTest {

    private final PaymentMethodsForEmployeeRateRule rule = new PaymentMethodsForEmployeeRateRule();

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
                        .stream().filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
                        .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
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
                        .stream().filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
                        .forEach(paymentOption -> assertTrue(paymentOption.isEnabled())));
    }
}
