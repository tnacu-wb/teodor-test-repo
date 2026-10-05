package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.BD;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_PERSONAL_STORED_CARD;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class BookingAllowancesForPibaCardsTest {

  private final BookingAllowancesForPibaCards rule = new BookingAllowancesForPibaCards();

  @Test
  void calculate__enableBookingAllowancesForNewPibaCards() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForPersonalStoredPibaCards() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForCentrallyStoredPibaCards_cnpRequired() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForCentrallyStoredPibaCards_cnpNotRequired() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .filter(paymentMethod -> !paymentMethod.getCard().isCnpRequired())
        .forEach(paymentOption -> {
          assertNull(paymentOption.getBookingAllowances());
        });
  }

    @Test
    void calculate__enableBookingAllowancesForCentrallyStoredPibaCards_PibaEuroCard() {
        // Arrange
        RuleData rule = RuleDataTest.getPaymentMethodSpecificCard(7, Country.DE);
        // Act
        var resultFinal = this.rule.calculate(rule);

        // Assert
        resultFinal.getPaymentMethods()
                .stream()
                .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
                        && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
                        && BD.name().equals(paymentMethod.getCard().getType()))
                .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
                .forEach(paymentOption -> {
                    assertNotNull(paymentOption.getBookingAllowances());
                    assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
                });
    }


    //FOR PIBA EURO

  @Test
  void calculate__enableBookingAllowancesForNewPibaCardsForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodForPibaEURO();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForPersonalStoredPibaCardsForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodForPibaEURO();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForCentrallyStoredPibaCards_cnpRequiredForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodForPibaEURO();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForCentrallyStoredPibaCards_cnpNotRequiredForPibaEuor() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodForPibaEURO();

    // Act
    var result = this.rule.calculate(rule);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .filter(paymentMethod -> !paymentMethod.getCard().isCnpRequired())
        .forEach(paymentOption -> {
          assertNull(paymentOption.getBookingAllowances());
        });
  }

  @Test
  void calculate__enableBookingAllowancesForCentrallyStoredPibaCards_PibaEuroCardForPibaEuor() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodSpecificCard(7, Country.DE);
    rule.setPibaEuroEnabled(Boolean.TRUE);
    // Act
    var resultFinal = this.rule.calculate(rule);

    // Assert
    resultFinal.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_CENTRALLY_STORED_CARD.name().equals(paymentMethod.getCard().getCardType())
            && BD.name().equals(paymentMethod.getCard().getType()))
        .filter(paymentMethod -> paymentMethod.getCard().isCnpRequired())
        .forEach(paymentOption -> {
          assertNotNull(paymentOption.getBookingAllowances());
          assertNotNull(paymentOption.getBookingAllowances().getMaxDinnerBudgets());
        });
  }
}
