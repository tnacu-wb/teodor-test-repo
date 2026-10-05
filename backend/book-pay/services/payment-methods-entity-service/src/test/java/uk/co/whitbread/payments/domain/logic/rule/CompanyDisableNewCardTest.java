package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.COMPANY_DISABLED_NEW_CARD;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class CompanyDisableNewCardTest {

  private final CompanyDisableNewCard rule = new CompanyDisableNewCard();

  @Test
  void calculate__companyDisableNewCardTest() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> !paymentMethod.getType().equals(SAVED_CARD.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(Collections.singletonList(COMPANY_DISABLED_NEW_CARD.name()),
              paymentMethod.getReasons());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
        });
  }

}
