package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_PERSONAL_STORED_CARD;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class CompanyDisabledPersonalStoredCardTest {

  private final CompanyDisabledPersonalStoredCard rule = new CompanyDisabledPersonalStoredCard();

  @Test
  void calculate__companyDisableNewCardTest() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(BUSINESS_PERSONAL_STORED_CARD.name(),
              paymentMethod.getCard().getCardType());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
        });
  }

}
