package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.*;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.LEISURE_STORED_CARD;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class CnpOptionAllowedTest {

  private final CnpOptionAllowed rule = new CnpOptionAllowed();

  @Test
  void givenNewPibaCard_returnsCnpOptionAllowed() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isCnpOptionAvailable());
        });
  }

  @Test
  void givenNewOpenLoopCard_returnsCnpOptionNotAllowed() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isCnpOptionAvailable());
        });
  }

    @Test
    void givenSavedCard_returnsCnpOptionAllowed() {
        // Arrange
        RuleData rule = RuleDataTest.getPaymentMethodSpecificCard(7, Country.DE);
        rule.getPaymentMethods().forEach(i->i.getCard().setCardType(LEISURE_STORED_CARD.name()));

        // Act
        var result = this.rule.calculate(rule);

        // Assert
        result.getPaymentMethods()
                .forEach(paymentMethod -> {
                    assertTrue(paymentMethod.isCnpOptionAvailable());
                });
    }

    //FOR PIBA EURO

  @Test
  void givenNewPibaCard_returnsCnpOptionAllowedForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isCnpOptionAvailable());
        });
  }

  @Test
  void givenNewOpenLoopCard_returnsCnpOptionNotAllowedForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequestWithNewCardPaymentMethodRequired();
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isCnpOptionAvailable());
        });
  }

  @Test
  void givenSavedCard_returnsCnpOptionAllowedForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodSpecificCard(7, Country.DE);
    rule.getPaymentMethods().forEach(i->i.getCard().setCardType(LEISURE_STORED_CARD.name()));
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isCnpOptionAvailable());
        });
  }

}