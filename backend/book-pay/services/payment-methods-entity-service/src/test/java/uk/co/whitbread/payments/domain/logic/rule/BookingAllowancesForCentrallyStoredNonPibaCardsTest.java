package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class BookingAllowancesForCentrallyStoredNonPibaCardsTest {

  private final BookingAllowancesForCentrallyStoredNonPibaCards centrallyNonPibaRule =
      new BookingAllowancesForCentrallyStoredNonPibaCards();

  @Test
  void calculate__BookingAllowancesForCentrallyStoredCardsNonPiba() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethod();

    // Act
    var result = this.centrallyNonPibaRule.calculate(rule);

    //Assert
    var paymentMethods = result.getPaymentMethods();
    assertNull(paymentMethods.get(1).getBookingAllowances());
    assertNotNull(paymentMethods.get(4).getBookingAllowances());
    assertNotNull(paymentMethods.get(4).getBookingAllowances().getMaxDinnerBudgets());
  }

  @Test
  void calculate__BookingAllowancesForCentrallyStoredCardsNonPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.getPaymentMethodForPibaEURO();

    // Act
    var result = this.centrallyNonPibaRule.calculate(rule);

    //Assert
    var paymentMethods = result.getPaymentMethods();
    assertNull(paymentMethods.get(1).getBookingAllowances());
    assertNotNull(paymentMethods.get(4).getBookingAllowances());
    assertNotNull(paymentMethods.get(4).getBookingAllowances().getMaxDinnerBudgets());
  }


}
