package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.getNewCardPaymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.getNewPibaPaymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;


import java.util.List;
import java.util.Set;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;

import org.junit.jupiter.api.Test;

class EnablePibaAsPaymentOptionTest {
  public static final String PIBA_UK_NAME = "PIBA UK";
  public static final String PIBAGB_SUBTYPE = "PIBAGB";
  public static final String ADVANCE_RATE_PLAN_CODE = "ADVANCE";
  public static final String FLEXRATE_RATE_PLAN_CODE = "FLEXRATE";
  private final EnablePibaAsPaymentOption rule = new EnablePibaAsPaymentOption();

  @Test
  void calculate__PibaEnablePoaAsPaymentOptionWithAdvanceRatePlanCode() {
    List<PaymentMethod> paymentMethodList = List.of(
        getNewPibaPaymentMethod(PIBA_UK_NAME, PIBAGB_SUBTYPE),
        getNewCardPaymentMethod());

    RuleData rule = createRuleDataRequest(paymentMethodList, ADVANCE_RATE_PLAN_CODE);
    // Act
    var result = this.rule.calculate(rule);

    //Assert
    var paymentMethods = result.getPaymentMethods();
    assertTrue(paymentMethods.get(0).isEnabled());
    assertFalse(paymentMethods.get(0).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(1).isEnabled());
    assertTrue(paymentMethods.get(1).isEnabled());
    assertTrue(paymentMethods.get(1).getPaymentOptions().get(0).isEnabled());
    assertFalse(paymentMethods.get(1).getPaymentOptions().get(1).isEnabled());
  }

  @Test
  void calculate__PibaEnablePoaAsPaymentOptionWithFlexRatePlanCode() {
    List<PaymentMethod> paymentMethodList = List.of(
        getNewPibaPaymentMethod(PIBA_UK_NAME, PIBAGB_SUBTYPE),
        getNewCardPaymentMethod());

    RuleData rule = createRuleDataRequest(paymentMethodList, FLEXRATE_RATE_PLAN_CODE);
    // Act
    var result = this.rule.calculate(rule);

    //Assert
    var paymentMethods = result.getPaymentMethods();
    assertTrue(paymentMethods.get(0).isEnabled());
    assertFalse(paymentMethods.get(0).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(1).isEnabled());
    assertTrue(paymentMethods.get(1).isEnabled());
    assertTrue(paymentMethods.get(1).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(1).getPaymentOptions().get(1).isEnabled());
  }


  private RuleData createRuleDataRequest(List<PaymentMethod> paymentMethods, String ratePlanCode) {
    return RuleData.builder()
        .country(Country.GB)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PAY_NOW))
            .ratePlanCode(ratePlanCode)
            .build())
        .paymentMethods(paymentMethods)
        .build();
  }
}