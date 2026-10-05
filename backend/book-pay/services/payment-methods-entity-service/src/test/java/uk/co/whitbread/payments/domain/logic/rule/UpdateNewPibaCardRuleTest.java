package uk.co.whitbread.payments.domain.logic.rule;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils;
import uk.co.whitbread.payments.domain.model.out.PibaCard;
import uk.co.whitbread.payments.domain.model.out.RuleData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;

class UpdateNewPibaCardRuleTest {

    private final UpdateNewPibaCardRule rule = new UpdateNewPibaCardRule();
    private static final String BLANK_STRING = "";
    private static final String PIBA = "PIBA";

    @Test
    void calculate_New_PIBA_in_UK_Hotels() {
        // Arrange
        RuleData ruleData = createRequestWithNewPIBACardPaymentMethod(Country.GB);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
                .forEach(paymentMethod -> {
                    // NEW PIBA will be updated for UK
                    assertEquals(PIBA, paymentMethod.getName());
                    assertEquals(PibaCard.UK.getSubType(), paymentMethod.getSubType());
                });
    }

    @Test
    void calculate_New_PIBA_in_DE_Hotels() {
        // Arrange
        RuleData ruleData = createRequestWithNewPIBACardPaymentMethod(Country.DE);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
                .forEach(paymentMethod -> {
                    // NEW PIBA will be updated for DE
                    assertEquals(PIBA, paymentMethod.getName());
                    assertEquals(PibaCard.DE.getSubType(), paymentMethod.getSubType());
                });
    }

    @Test
    void calculate_New_PIBA_in_IE_Hotels() {
        // Arrange
        RuleData ruleData = createRequestWithNewPIBACardPaymentMethod(Country.IE);

        // Act
        var request = rule.calculate(ruleData);

        // Assert
        request.getPaymentMethods()
                .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
                .forEach(paymentMethod -> {
                    //No Change as PIBA not implemented
                    assertEquals(PIBA, paymentMethod.getName());
                    assertEquals(BLANK_STRING, paymentMethod.getSubType());
                });
    }

    private RuleData createRequestWithNewPIBACardPaymentMethod(Country country) {
        return RuleData.builder()
                .country(country)
                .paymentMethods(List.of(PaymentMethodsTestUtils.getNewPibaPaymentMethod(PIBA, BLANK_STRING)))
                .build();
    }

}