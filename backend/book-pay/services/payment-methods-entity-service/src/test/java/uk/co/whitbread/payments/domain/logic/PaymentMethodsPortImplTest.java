package uk.co.whitbread.payments.domain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsFlow;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.out.Basket;
import uk.co.whitbread.payments.domain.model.out.BasketReservation;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.domain.model.in.SelectedPaymentMethods;
import uk.co.whitbread.payments.domain.ports.secondary.BasketPort;
import uk.co.whitbread.payments.domain.ports.secondary.CustomerAccountPort;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;
import uk.co.whitbread.payments.domain.ports.secondary.TokenPort;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodsPortImplTest {

  private static final String APPS_ANDROID = "APPS_ANDROID";
  private static final String APPS_IOS = "APPS_IOS";
  @Mock
  private HotelInfoPort hotelInfoPort;
  @Mock
  private CustomerAccountPort customerAccountPort;
  @Mock
  private DefaultPaymentMethodsPort defaultPaymentMethodsPort;
  @Mock
  private ReservationsPort reservationPort;
  @Mock
  private PaymentMethodsCommon paymentMethodsCommon;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private TokenPort tokenPort;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private BasketPort basketPort;

  private PaymentMethodsPortImpl paymentMethodsPortImpl;


  static Stream<Arguments> appsChannelProvider() {
    return Stream.of(
        Arguments.of(APPS_ANDROID,

            PaymentMethodsFlow.CHECKINONLINE.value(), true, "COMPLETED", true),
        Arguments.of(APPS_ANDROID,
            PaymentMethodsFlow.CHECKINONLINE.value(), false, "CIOL_FAILED", true),
        Arguments.of(APPS_IOS, PaymentMethodsFlow.CHECKINONLINE.value(),
            true, "CIOL_RC_FAILED", true),
        Arguments.of(APPS_IOS, PaymentMethodsFlow.CHECKINONLINE.value(),
            false, "CIOL_RC_FAILED", true),
        Arguments.of(null, PaymentMethodsFlow.CHECKINONLINE.value(),
            true, "CIOL_RC_FAILED", false),
        Arguments.of("WEB", PaymentMethodsFlow.CHECKINONLINE.value(),
            true, "CIOL_RC_FAILED", false),
        Arguments.of("WEB", PaymentMethodsFlow.CHECKINONLINE.value(),
            false, "CIOL_RC_FAILED", false),
        Arguments.of(APPS_ANDROID, null, true, "CIOL_RC_FAILED", false),
        Arguments.of(APPS_ANDROID, PaymentMethodsFlow.CHECKINONLINE.value(), true, "OTHER", false)
    );
  }

  @BeforeEach
  void init() {
    paymentMethodsPortImpl = new PaymentMethodsPortImpl(
        hotelInfoPort, customerAccountPort, defaultPaymentMethodsPort, reservationPort,
        paymentMethodsCommon, authenticatedUserService, tokenPort, basketPort, false, unleashWrapper,
        new DataTransConfig()
    );
  }

  @ParameterizedTest
  @MethodSource("appsChannelProvider")
  void testGetPaymentMethods_isAppsLogic(
      String clientChannel,
      String flow,
      boolean disablePaymentsFf,
      String basketStatus,
      boolean expectFilter) {
    // Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeature = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeature);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(
        disablePaymentsFf);

    PaymentMethodsRequest request = mock(PaymentMethodsRequest.class);
    BasketReservation reservation = mock(BasketReservation.class);
    when(reservation.getFolioView()).thenReturn(1);
    when(reservation.getPaymentMethod()).thenReturn("BU");
    HotelInfo hotelInfo = mock(HotelInfo.class);

    PaymentMethods defaultPaymentMethods = mock(PaymentMethods.class);
    PaymentMethod newCardMethod = mock(PaymentMethod.class);
    PaymentMethod gpMethod = mock(PaymentMethod.class);
    PaymentMethod apMethod = mock(PaymentMethod.class);
    PaymentMethod paypalMethod = mock(PaymentMethod.class);

    List<PaymentMethod> allMethods = List.of(newCardMethod,gpMethod,apMethod,paypalMethod);
    List<PaymentMethod> onlyCiolPaymentMethodds = List.of(newCardMethod,gpMethod,apMethod,paypalMethod);
    when(reservation.getHotelId()).thenReturn("hotel123");
    when(hotelInfoPort.findHotelPaymentDetails(any(), any(), any())).thenReturn(hotelInfo);
    when(reservationPort.findBasketReservations((anyString()))).thenReturn(reservation);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(false);
    when(defaultPaymentMethods.getPaymentMethods()).thenReturn(allMethods);
    when(paymentMethodsCommon.updatePaymentMethodOrder(any())).thenReturn(defaultPaymentMethods);

    if ((!disablePaymentsFf || expectFilter)) {
      when(newCardMethod.getType()).thenReturn("NEW_CARD");
      when(gpMethod.getType()).thenReturn("GP");
      when(apMethod.getType()).thenReturn("AP");
      when(paypalMethod.getType()).thenReturn("PAYPAL");
    }


    if (!disablePaymentsFf) {
      when(defaultPaymentMethodsPort.getDefaultPaymentMethods()).thenReturn(allMethods);
    } else {
      when(defaultPaymentMethodsPort.getFailSafePaymentMethods()).thenReturn(allMethods);
    }
    when(request.getClientChannel()).thenReturn(clientChannel);
    when(request.getBasketReference()).thenReturn("ref123");
    when(request.getCountry()).thenReturn("GB");
    when(request.getLanguage()).thenReturn("en");
    if (clientChannel != null && !"WEB".equals(clientChannel)) {
      when(request.getFlow()).thenReturn(flow);
    }

    var basket = mock(Basket.class);
    if (clientChannel != null && List.of(APPS_IOS, APPS_ANDROID).contains(clientChannel)
        && "CheckInOnline".equals(flow)) {
      when(basket.getBasketStatus()).thenReturn(basketStatus);
      when(basketPort.getBasket(anyString())).thenReturn(basket);
    }

    // Act
    PaymentMethods result = paymentMethodsPortImpl.getPaymentMethods(request);

    // Assert
    if (expectFilter) {
      assertEquals(onlyCiolPaymentMethodds, result.getPaymentMethods());
    } else {
      assertEquals(allMethods, result.getPaymentMethods());
    }
  }

  @Test
  void shouldSkipValidation_whenIsCiolTrue() {
    // Arrange
    SelectedPaymentMethods request = SelectedPaymentMethods.builder()
      .basketReference("test-ref")
      .type("CARD")
      .selectedPaymentOption(PaymentPolicy.PAY_NOW)
      .isCiol(true)
      .build();

    Reservation reservation = mock(Reservation.class);
    when(reservation.getChannel()).thenReturn("WEB");
    when(reservationPort.findReservations(anyString(), eq(true))).thenReturn(reservation);

    FeatureFlag mockFeatureFlag = mock(FeatureFlag.class);
    FeatureFlag.Feature mockedFeature = mock(FeatureFlag.Feature.class);

    when(unleashWrapper.featureFlag()).thenReturn(mockFeatureFlag);
    when(mockFeatureFlag.getDisablePayments()).thenReturn(mockedFeature);
    when(unleashWrapper.isEnabled(eq(mockedFeature), any())).thenReturn(false);

    // Act + Assert
    assertDoesNotThrow(() -> paymentMethodsPortImpl.validatePaymentMethods(request));
  }
}