package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.getNewCardPaymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.getNewPibaPaymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.getPaypalPaymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.paymentMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.logic.PaymentMethodsPortImpl;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.in.UserType;
import uk.co.whitbread.payments.domain.model.out.AcceptedCardType;
import uk.co.whitbread.payments.domain.model.out.AcceptedCreditCard;
import uk.co.whitbread.payments.domain.model.out.Address;
import uk.co.whitbread.payments.domain.model.out.BasketReservation;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.model.out.HotelPaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsConfiguration;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.domain.model.out.PaymentProvider;
import uk.co.whitbread.payments.domain.ports.secondary.BasketPort;
import uk.co.whitbread.payments.domain.ports.secondary.CustomerAccountPort;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;
import uk.co.whitbread.payments.domain.ports.secondary.TokenPort;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;

@ExtendWith(MockitoExtension.class)
class DisablePibaForGermanyTest {

    private static final String PAYMENT_PROVIDER_3CP = "3CP";
    private static final String HOTEL_CODE = "DRECIT";

    public static final String PIBA_UK_NAME = "PIBA UK";
    public static final String PIBAGB_SUBTYPE = "PIBAGB";

    private PaymentMethodsPortImpl paymentMethodService;
    @Mock
    private HotelInfoPort hotelInfoPort;
    @Mock
    private DefaultPaymentMethodsPort defaultPaymentMethodsPort;
    @Mock
    private ReservationsPort reservationPort;
    @Mock
    private CustomerAccountPort customerAccountPort;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private TokenPort tokenPort;
    @Mock
    private BasketPort basketPort;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;


    @InjectMocks
    private PaymentMethodsCommon paymentMethodsCommon;

  @BeforeEach
  public void init() {
    paymentMethodService = new PaymentMethodsPortImpl(hotelInfoPort, customerAccountPort,
        defaultPaymentMethodsPort, reservationPort, paymentMethodsCommon, authenticatedUserService,
        tokenPort, basketPort, Boolean.FALSE, unleashWrapper, new DataTransConfig());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void rule_PibaDisableForGermanHotels(boolean disablePaymentsFf) {
    final var requestPayment = createPaymentRequest();
    List<PaymentMethod> paymentMethodList = List.of(
        getNewPibaPaymentMethod(PIBA_UK_NAME, PIBAGB_SUBTYPE),
        getNewCardPaymentMethod(), getPaypalPaymentMethod());
    List<PaymentMethod> failSafePaymentMethodList = List.of(paymentMethod("NEW_CARD"));

    if (disablePaymentsFf) {
      when(defaultPaymentMethodsPort.getFailSafePaymentMethods()).thenReturn(failSafePaymentMethodList);
    } else {
      when(defaultPaymentMethodsPort.getDefaultPaymentMethods()).thenReturn(
          paymentMethodList);
    }
    when(reservationPort.findBasketReservations(requestPayment.getBasketReference())).thenReturn(
        createReservation());

    when(hotelInfoPort.findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage())).thenReturn(
        createHotelInfo("Deutschland", createAcceptedCreditCard(), createPaymentProvider()));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);

    //Act
    var responsePayment = this.paymentMethodService.getPaymentMethods(requestPayment);
    List<PaymentMethod> paymentMethods = responsePayment.getPaymentMethods();
    if (!disablePaymentsFf) {
      assertFalse(paymentMethods.stream().filter(paymentMethod -> StringUtils.equals(paymentMethod.getType(),
          "NEW_PIBA")).findFirst().map(PaymentMethod::isEnabled).get());
    }
    assertTrue(paymentMethods.stream().filter(paymentMethod -> StringUtils.equals(paymentMethod.getType(),
        "NEW_CARD")).findFirst().map(PaymentMethod::isEnabled).get());
  }

    @Test
    void test_setLogoSrcUndefinedIfBlank() {
      List<PaymentMethod> paymentMethodList = new ArrayList<>();

      PaymentMethod paymentMethod = new PaymentMethod();
      paymentMethod.setName("NEW_CARD");

      List<AcceptedCardType> acceptedCardTypes = new ArrayList<>();
      AcceptedCardType acceptedCardType = new AcceptedCardType();
      acceptedCardType.setName("AMEX");
      acceptedCardTypes.add(acceptedCardType);

      paymentMethod.setAcceptedCardTypes(acceptedCardTypes);
      paymentMethodList.add(paymentMethod);

      PaymentMethods paymentMethods = PaymentMethods.builder()
            .paymentMethods(paymentMethodList).build();

      this.paymentMethodService.setLogoSrcUndefinedIfBlank(paymentMethods);

      paymentMethods.getPaymentMethods().forEach(pm -> {
        pm.getAcceptedCardTypes().forEach(act -> {
          assertEquals("undefined", act.getLogoSrc());
        });
      });

    }

    @Test
    void test_setLogoSrcUndefinedIfBlank_NullPaymentMethods() {
      this.paymentMethodService.setLogoSrcUndefinedIfBlank(null);
      assertTrue(true);
    }

    @Test
    void test_setLogoSrcUndefinedIfBlank_EmptyPaymentMethods() {
      PaymentMethods paymentMethods = mock(PaymentMethods.class);
      when(paymentMethods.getPaymentMethods()).thenReturn(null);
      this.paymentMethodService.setLogoSrcUndefinedIfBlank(paymentMethods);
      assertTrue(true);
    }

    @Test
    void test_setLogoSrcUndefinedIfBlank_PaymentMethodWithNullValue() {
      PaymentMethods paymentMethods = mock(PaymentMethods.class);
      when(paymentMethods.getPaymentMethods()).thenReturn(Collections.singletonList(null));
      this.paymentMethodService.setLogoSrcUndefinedIfBlank(paymentMethods);
      assertTrue(true);
    }

    @Test
    void test_setLogoSrcUndefinedIfBlank_PaymentMethodWithNullAcceptedCardList() {
      PaymentMethods paymentMethods = mock(PaymentMethods.class);
      List<PaymentMethod> paymentMethodList = new ArrayList<>();
      paymentMethodList.add(new PaymentMethod());
      when(paymentMethods.getPaymentMethods()).thenReturn(paymentMethodList);
      this.paymentMethodService.setLogoSrcUndefinedIfBlank(paymentMethods);
      assertTrue(true);
    }

    @Test
    void test_setLogoSrcUndefinedIfBlank_PaymentMethodWithNullAcceptedCard() {
      PaymentMethods paymentMethods = mock(PaymentMethods.class);
      List<PaymentMethod> paymentMethodList = new ArrayList<>();
      PaymentMethod paymentMethod = new PaymentMethod();
      paymentMethod.setAcceptedCardTypes(Collections.singletonList(null));
      paymentMethodList.add(paymentMethod);
      when(paymentMethods.getPaymentMethods()).thenReturn(paymentMethodList);
      this.paymentMethodService.setLogoSrcUndefinedIfBlank(paymentMethods);
      assertTrue(true);
    }

    @Test
    void test_setLogoSrcUndefinedIfBlank_Negatives() {
      PaymentMethod paymentMethod = new PaymentMethod();
      List<PaymentMethod> pmList = new ArrayList<>();
      PaymentMethod pm = new PaymentMethod();
      pm.setName("NEW_CARD");
      pmList.add(paymentMethod);
      PaymentMethods pms = PaymentMethods.builder()
          .paymentMethods(pmList).build();

      this.paymentMethodService.setLogoSrcUndefinedIfBlank(pms);

      assertTrue(true);
    }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void rule_PibaDisableForGermanHotelsWithPI(boolean disablePaymentsFf) {
    final var requestPayment = createPaymentRequestLeisureUser();
    List<PaymentMethod> paymentMethodList = List.of(
        getNewPibaPaymentMethod(PIBA_UK_NAME, PIBAGB_SUBTYPE),
        getNewCardPaymentMethod(), getPaypalPaymentMethod());
    List<PaymentMethod> failSafePaymentMethodList = List.of(paymentMethod("NEW_CARD"));

    if (disablePaymentsFf) {
      when(defaultPaymentMethodsPort.getFailSafePaymentMethods()).thenReturn(failSafePaymentMethodList);
    } else {
      when(defaultPaymentMethodsPort.getDefaultPaymentMethods()).thenReturn(
          paymentMethodList);
    }
    when(reservationPort.findBasketReservations(requestPayment.getBasketReference())).thenReturn(
        createReservation());
    when(hotelInfoPort.findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage())).thenReturn(
        createHotelInfo("Deutschland", createAcceptedCreditCard(), createPaymentProvider()));

    //when(tokenPort.getPaypalToken(Mockito.any())).thenReturn(new PaypalToken("test", "test"));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);

    //Act
    var responsePayment = this.paymentMethodService.getPaymentMethods(requestPayment);
    List<PaymentMethod> paymentMethods = responsePayment.getPaymentMethods();
    if (!disablePaymentsFf) {
      assertFalse(paymentMethods.stream().filter(paymentMethod -> StringUtils.equals(paymentMethod.getType(),
          "NEW_PIBA")).findFirst().map(PaymentMethod::isEnabled).get());
    }
    assertTrue(paymentMethods.stream().filter(paymentMethod -> StringUtils.equals(paymentMethod.getType(),
        "NEW_CARD")).findFirst().map(PaymentMethod::isEnabled).get());
  }

  @Test
  void getPaymentMethods_throwException(){
    var errorMessage = "Error while obtaining user account from token.";
    final var requestPayment = createPaymentRequest();

    when(reservationPort.findBasketReservations(requestPayment.getBasketReference())).thenReturn(
          createReservation());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(Optional.empty());

    //Act
    var exception = assertThrows(PaymentMethodsException.class, () ->
          paymentMethodService.getPaymentMethods(requestPayment));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));

  }

    private AcceptedCreditCard createAcceptedCreditCard() {
        final var acceptedCard = new AcceptedCreditCard();
        acceptedCard.setName("Mastercard Credit");
        acceptedCard.setCode("AC");
        acceptedCard.setListOrder("1");
        acceptedCard.setPaymentOnly(Boolean.TRUE);
        acceptedCard.setSchemeLogo("/content/dam/global/booking/Mastercard.jpg");
        return acceptedCard;
    }

    private PaymentProvider createPaymentProvider() {
        final var paymentProvider = new PaymentProvider();
        paymentProvider.setProviderId(PAYMENT_PROVIDER_3CP);
        final var hotelPaymentMethod = new HotelPaymentMethod();
        hotelPaymentMethod.setCode("AC");
        hotelPaymentMethod.setPaymentOnly(Boolean.TRUE);
        hotelPaymentMethod.setName("Mastercard Credit");
        hotelPaymentMethod.setSchemeLogo("/content/dam/global/booking/Mastercard.jpg");
        hotelPaymentMethod.setListOrder("1");
        final var hotelPaymentMethod2 = new HotelPaymentMethod();
        hotelPaymentMethod2.setCode("VC");
        hotelPaymentMethod2.setPaymentOnly(Boolean.TRUE);
        hotelPaymentMethod2.setName("VISA");
        hotelPaymentMethod2.setSchemeLogo("/content/dam/global/booking/Visa.jpg");
        hotelPaymentMethod2.setListOrder("2");
        paymentProvider.setPaymentMethods(List.of(hotelPaymentMethod, hotelPaymentMethod2));
        return paymentProvider;
    }

    private HotelInfo createHotelInfo(String country, AcceptedCreditCard acceptedCard,
                                      PaymentProvider paymentProvider) {
        final var hotelInfo = new HotelInfo();
        hotelInfo.setAcceptedCreditCards(List.of(acceptedCard));
        final var address = new Address();
        address.setCountry(country);
        hotelInfo.setAddress(address);
        hotelInfo.setPaymentProviders(paymentProvider == null ? null : List.of(paymentProvider));
        hotelInfo.setBrand("PI");
        PaymentMethodsConfiguration pc = new PaymentMethodsConfiguration();
        pc.setCode("PP");
        pc.setName("Paypal");
        pc.setSupportedChannels("PI, BB");
        pc.setSupportedBookingTypes("POA, PN");
        pc.setOperaPaymentMethod("DPP");
        pc.setListOrder("3");
        List<PaymentMethodsConfiguration> pl =new ArrayList<PaymentMethodsConfiguration>();
        pl.add(pc);
        pc = new PaymentMethodsConfiguration();
        pc.setCode("AP");
        pc.setName("Apple Pay");
        pc.setSupportedChannels("PI, BB");
        pc.setSupportedBookingTypes("POA, PN");
        pc.setOperaPaymentMethod("AP");
        pc.setListOrder("4");
        pl.add(pc);
        hotelInfo.setPaymentMethodsConfiguration(pl);
        return hotelInfo;
    }


    private BasketReservation createReservation() {
        return BasketReservation.builder()
                .hotelId(HOTEL_CODE)
                .hotelPaymentPolicies(Set.of(PaymentPolicy.PAY_NOW))
                .folioView(1)
                .paymentMethod("BU")
                .build();
    }

    private PaymentMethodsRequest createPaymentRequest() {
        return PaymentMethodsRequest.builder()
                .country("de")
                .language("de")
                .basketReference("DRE3242124")
                .userType(UserType.AGENT)
                .clientChannel("PI")
                .build();
    }

    private PaymentMethodsRequest createPaymentRequestLeisureUser() {
        return PaymentMethodsRequest.builder()
            .country("de")
            .language("de")
            .basketReference("DRE3242124")
            .bookingChannel("PI")
            .userType(UserType.LEISURE)
            .clientChannel("PI")
            .build();
    }
}
