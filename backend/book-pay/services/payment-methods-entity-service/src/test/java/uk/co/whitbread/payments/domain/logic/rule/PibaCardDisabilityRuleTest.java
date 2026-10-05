package uk.co.whitbread.payments.domain.logic.rule;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.*;
import static uk.co.whitbread.payments.domain.model.out.CardType.*;

class PibaCardDisabilityRuleTest {

    private final PibaCardDisabilityRule rule = new PibaCardDisabilityRule();

    @Test
    void calculate_New_PIBA_in_DE_Hotels() {
        // Arrange
        RuleData ruleData = createRequestWithNewPIBACardPaymentMethod(Country.DE, PibaCard.DE.getSubType());

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
                .forEach(paymentMethod -> {
                    assertTrue(paymentMethod.isEnabled());
                });
    }

    @Test
    void calculate_New_PIBA_in_UK_Hotels() {
        // Arrange
        RuleData ruleData = createRequestWithNewPIBACardPaymentMethod(Country.GB, PibaCard.UK.getSubType());

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
                .forEach(paymentMethod -> {
                    assertTrue(paymentMethod.isEnabled());
                });
    }

    @Test
    void calculate_New_PIBA_in_IE_Hotels() {
        // Arrange
        RuleData ruleData = createRequestWithNewPIBACardPaymentMethod(Country.IE, PibaCard.UK.getSubType());

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
                .forEach(paymentMethod -> {
                    assertFalse(paymentMethod.isEnabled());
                    assertTrue(paymentMethod.getReasons().contains(PIBA_UK_ALLOWED_ONLY_IN_UK.name()));
                });
    }

    @Test
    void calculate_savedPIBAUKAllowedInUK() {
        // Arrange
        RuleData ruleData = RuleDataTest.getPaymentMethodSpecificCard(6, Country.GB);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
                        (AT.name().equals(paymentMethod.getCard().getType()) ||
                                PI.name().equals(paymentMethod.getCard().getType())))
                .forEach(paymentMethod -> {
                    assertTrue(paymentMethod.isEnabled());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
                });
    }

    @Test
    void calculate_savedPIBAUKNotAllowedInDE() {
        // Arrange
        RuleData ruleData = RuleDataTest.getPaymentMethodSpecificCard(6, Country.DE);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
                        (AT.name().equals(paymentMethod.getCard().getType()) ||
                                PI.name().equals(paymentMethod.getCard().getType())))
                .forEach(paymentMethod -> {
                    assertFalse(paymentMethod.isEnabled());
                    assertEquals(Collections.singletonList(PIBA_UK_ALLOWED_ONLY_IN_UK.name()),
                            paymentMethod.getReasons());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
                });
    }

    @Test
    void calculate_savedPIBAUKNotAllowedInIE() {
        // Arrange
        RuleData ruleData = RuleDataTest.getPaymentMethodSpecificCard(6, Country.IE);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
                        (AT.name().equals(paymentMethod.getCard().getType()) ||
                                PI.name().equals(paymentMethod.getCard().getType())))
                .forEach(paymentMethod -> {
                    assertFalse(paymentMethod.isEnabled());
                    assertEquals(Collections.singletonList(PIBA_UK_ALLOWED_ONLY_IN_UK.name()),
                            paymentMethod.getReasons());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
                });
    }

    @Test
    void calculate_savedPIBAEUNotAllowedInUK() {
        // Arrange
        RuleData ruleData = RuleDataTest.getPaymentMethodSpecificCard(7, Country.GB);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
                        (BD.name().equals(paymentMethod.getCard().getType()) ))
                .forEach(paymentMethod -> {
                    assertFalse(paymentMethod.isEnabled());
                    assertEquals(Collections.singletonList(PIBA_EU_ALLOWED_ONLY_IN_EU.name()),
                            paymentMethod.getReasons());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
                });
    }


    @Test
    void calculate_savedPIBAEUNotAllowedInIE() {
        // Arrange
        RuleData ruleData = RuleDataTest.getPaymentMethodSpecificCard(7, Country.IE);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name()) &&
                        (BD.name().equals(paymentMethod.getCard().getType()) ))
                .forEach(paymentMethod -> {
                    assertFalse(paymentMethod.isEnabled());
                    assertEquals(Collections.singletonList(PIBA_EU_ALLOWED_ONLY_IN_EU.name()),
                            paymentMethod.getReasons());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
                });
    }
    private RuleData createRequestWithNewPIBACardPaymentMethod(Country country, String subType) {
        return RuleData.builder()
                .country(country)
                .paymentMethods(List.of(PaymentMethodsTestUtils.getNewPibaPaymentMethod("PIBA", subType)))
                .build();
    }

}