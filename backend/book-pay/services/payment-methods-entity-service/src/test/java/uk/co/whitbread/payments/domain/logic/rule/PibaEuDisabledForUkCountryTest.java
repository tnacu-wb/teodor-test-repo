package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.getNewPibaPaymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;

class PibaEuDisabledForUkCountryTest {

  private final PibaEuDisabledForUKCountry rule = new PibaEuDisabledForUKCountry();

  @Test
  void calculate__PIBAEuDisabledForUKCountry(){
    List<PaymentMethod> paymentMethodList = List.of(
        getNewPibaPaymentMethod("PIBA UK", "PIBAGB"),
        getNewPibaPaymentMethod("PIBA EU", "PIBADE"));

    RuleData rule = createRequestForNewPibaPaymentMethod(Country.GB, paymentMethodList);

    // Act
    var result = this.rule.calculate(rule);

    //Assert
    var paymentMethods = result.getPaymentMethods();
    assertTrue(paymentMethods.get(0).isEnabled());
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(1).isEnabled());

    assertFalse(paymentMethods.get(1).isEnabled());
    assertFalse(paymentMethods.get(1).getPaymentOptions().get(1).isEnabled());
  }

  @Test
  void calculate__PIBAEuForNonUkCountry(){
    List<PaymentMethod> paymentMethodList = List.of(
        getNewPibaPaymentMethod("PIBA EU", "PIBADE"));

    RuleData rule = createRequestForNewPibaPaymentMethod(Country.DE, paymentMethodList);

    // Act
    var result = this.rule.calculate(rule);

    //Assert
    var paymentMethods = result.getPaymentMethods();
    assertTrue(paymentMethods.get(0).isEnabled());
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(0).isEnabled());
    assertTrue(paymentMethods.get(0).getPaymentOptions().get(1).isEnabled());
  }

  private static RuleData createRequestForNewPibaPaymentMethod(Country country, List<PaymentMethod> paymentMethods) {
    return RuleData.builder()
        .country(country)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of(PAY_NOW))
            .build())
        .paymentMethods(paymentMethods)
        .build();
  }
}
