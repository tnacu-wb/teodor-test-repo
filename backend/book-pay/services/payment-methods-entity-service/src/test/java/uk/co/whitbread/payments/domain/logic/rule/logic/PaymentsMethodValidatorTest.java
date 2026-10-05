package uk.co.whitbread.payments.domain.logic.rule.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsValidationException;
import uk.co.whitbread.payments.domain.logic.PaymentMethodsPortImpl;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.in.SelectedPaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.ports.secondary.*;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentsMethodValidatorTest {

  @Mock
  private ReservationsPort reservationPort;
  @Mock
  private HotelInfoPort hotelInfoPort;
  @Mock
  private DefaultPaymentMethodsPort defaultPaymentMethodsPort;
  @Mock
  private CustomerAccountPort customerAccountPort;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private TokenPort tokenPort;
  @Mock
  private BasketPort basketPort;
  @Mock
  private PaymentMethodsCommon paymentMethodsCommon;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;


  private PaymentMethodsPortImpl paymentMethodsPort;

  @BeforeEach
  public void init() {
    paymentMethodsPort = new PaymentMethodsPortImpl(hotelInfoPort, customerAccountPort,
        defaultPaymentMethodsPort, reservationPort, paymentMethodsCommon, authenticatedUserService,
        tokenPort, basketPort, Boolean.FALSE, unleashWrapper, new DataTransConfig());
  }


  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validatePaymentMethod_ThrowsException(boolean disablePaymentsFf) {
    var request = createSelectedMethods();
    String errorMessage = String.format("Error while validating payment methods, "
                + "we don't allow this payment option=%s for basketReference %s",
          request.getSelectedPaymentOption(), request.getBasketReference());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);
    when(reservationPort.findReservations(request.getBasketReference(), true)).thenReturn(createReservation());

    var exception = assertThrows(PaymentMethodsValidationException.class,
          () -> paymentMethodsPort.validatePaymentMethods(request));


    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));

  }

  private Reservation createReservation() {
    return Reservation.builder()
          .ratePlanCode("NOT_EMPLOYEE")
          .hotelPaymentPolicies(List.of(PaymentPolicy.PAY_ON_ARRIVAL))
          .build();
  }

  private SelectedPaymentMethods createSelectedMethods() {
    return SelectedPaymentMethods.builder()
          .basketReference("basketReference")
          .selectedPaymentOption(PaymentPolicy.PAY_NOW)
          .type("NOT_PIBA")
          .build();
  }
}
