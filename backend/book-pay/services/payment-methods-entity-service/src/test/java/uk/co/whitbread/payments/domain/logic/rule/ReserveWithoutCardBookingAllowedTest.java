package uk.co.whitbread.payments.domain.logic.rule;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.paymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.RESERVE_WITHOUT_CARD;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;

class ReserveWithoutCardBookingAllowedTest {

  @Test
  void calculate__reserveWithoutCreditCardAllowed() {
    // Arrange
    var underTest = new ReserveWithoutCardBookingAllowed();
    var rule = RuleData.builder()
        .paymentMethods(List.of(
            paymentMethod(NEW_CARD.name()),
            paymentMethod(NEW_PIBA.name())))
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(List.of(RESERVE_WITHOUT_CARD))
            .build())
        .build();

    // Act
    var result = underTest.calculate(rule);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getPaymentMethods()).isNotNull();
    assertThat(result.getPaymentMethods()).isNotNull();
    var enabledOptions = result.getPaymentMethods()
        .stream()
        .filter(PaymentMethod::isEnabled)
        .collect(Collectors.groupingBy(PaymentMethod::getType,
            flatMapping(method -> method.getPaymentOptions()
                .stream()
                .filter(PaymentOption::isEnabled)
                .map(PaymentOption::getType), toSet())));

    assertThat(enabledOptions).isEqualTo(Map.of(
        NEW_CARD.name(), Set.of(RESERVE_WITHOUT_CARD.name()),
        NEW_PIBA.name(), Set.of(RESERVE_WITHOUT_CARD.name())
    ));

  }
}
