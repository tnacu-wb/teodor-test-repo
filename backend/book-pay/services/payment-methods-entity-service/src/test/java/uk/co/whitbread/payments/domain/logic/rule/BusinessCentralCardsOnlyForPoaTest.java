package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;
import static uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentType.PAY_NOW;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class BusinessCentralCardsOnlyForPoaTest {

  private final BusinessCentralCardsOnlyForPoa rule = new BusinessCentralCardsOnlyForPoa();

  @Test
  void calculate__businessCentralCardsOnlyForPoa() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> SAVED_CARD.name().equals(paymentMethod.getType()) &&
            BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertEquals(BUSINESS_CENTRALLY_STORED_CARD.name(),
              paymentMethod.getCard().getCardType());
          paymentMethod.getPaymentOptions().stream()
              .filter(paymentOption -> PAY_NOW.name().equals(paymentOption.getType()))
              .forEach(paymentOption -> paymentOption.setEnabled(false));
        });
  }

}
