package uk.co.whitbread.payments.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.in.UserType;
import uk.co.whitbread.payments.domain.model.out.AcceptedCreditCard;
import uk.co.whitbread.payments.domain.model.out.Address;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.model.out.HotelPaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentOptionTest;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.domain.model.out.PaymentProvider;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;
@ExtendWith(MockitoExtension.class)
class PaymentCcuiMethodsServiceTest {

  private static final String ANY_NAME = "ANY_NAME";
  private static final String ANY_TYPE = "ANY_TYPE";
  private static final String ACCOUNT_COMPANY = "ACCOUNT_COMPANY";
  private static final String PAYMENT_PROVIDER_3CP = "3CP";
  private static final String HOTEL_CODE = "DUNCRO";
  private static final String HOTEL_COUNTRY_DE = "Germany";
  private static final String HOTEL_COUNTRY_UK = "gb";
  private static final String HOTEL_COUNTRY_IE = "Ireland";
  public static final String NON_GUARANTEED_BOOKING = "Non-guaranteed booking";
  public static final String RESERVE_WITHOUT_CARD = "RESERVE_WITHOUT_CARD";


  private PaymentCcuiMethodsPortImpl paymentCcuiMethodsService;
  @Mock
  private HotelInfoPort hotelInfoPort;
  @Mock
  private DefaultPaymentMethodsPort defaultPaymentMethodsPort;
  @Mock
  private ReservationsPort reservationPort;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @InjectMocks
  private PaymentMethodsCommon paymentMethodsCommon;

  @BeforeEach
  public void init() {
    paymentCcuiMethodsService = new PaymentCcuiMethodsPortImpl(hotelInfoPort,
        defaultPaymentMethodsPort, reservationPort, paymentMethodsCommon, Boolean.FALSE, unleashWrapper,
        new DataTransConfig());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentMethods_gb_ShouldReturnOk(boolean disablePaymentsFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);
    final var requestPayment = createPaymentRequest();
    if (disablePaymentsFf) {
      doReturn(createListFailSafePaymentMethods()).when(defaultPaymentMethodsPort).getFailSafePaymentCcuiMethods();
    } else {
      doReturn(createListDefaultPaymentMethods()).when(defaultPaymentMethodsPort).getDefaultPaymentCcuiMethods();
    }
    doReturn(createHotelInfo(HOTEL_COUNTRY_UK, createAcceptedCreditCard(), createPaymentProvider())).when(
        hotelInfoPort).findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage());
    doReturn(createReservation()).when(reservationPort).findReservations(requestPayment.getBasketReference(), false);

    //Act
    var responsePayment = this.paymentCcuiMethodsService.getPaymentMethods(requestPayment);

    //Assert
    assertThat(responsePayment, notNullValue());
    assertThat(responsePayment.getPaymentMethods(), notNullValue());
    assertThat(responsePayment.getPaymentMethods().size(), is(1));
    if (disablePaymentsFf) {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(RESERVE_WITHOUT_CARD));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(NON_GUARANTEED_BOOKING));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.TRUE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(PaymentOptionTest.PAY_ON_ARRIVAL));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(1));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.TRUE));
    } else {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(ACCOUNT_COMPANY));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(ANY_NAME));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.FALSE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(2));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.FALSE));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentMethods_Ireland_ShouldReturnOk(boolean disablePaymentsFf) {
    //Arrange
    final var requestPayment = createPaymentRequests();
    when(reservationPort.findReservations(requestPayment.getBasketReference(), false)).thenReturn(
        createReservation());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);
    if (disablePaymentsFf) {
      doReturn(createListFailSafePaymentMethods()).when(defaultPaymentMethodsPort).getFailSafePaymentCcuiMethods();
    } else {
      doReturn(createListDefaultPaymentMethods()).when(defaultPaymentMethodsPort).getDefaultPaymentCcuiMethods();
    }
    when(hotelInfoPort.findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage())).thenReturn(
        createHotelInfo(HOTEL_COUNTRY_IE, createAcceptedCreditCard(), createPaymentProvider()));

    Reservation reservations = reservationPort.findReservations(requestPayment.getBasketReference(), false);

    //Act
    var responsePayment = this.paymentCcuiMethodsService.getPaymentMethods(requestPayment);

    //Assert
    assertThat(requestPayment.getCountry(), notNullValue());
    assertThat(requestPayment.getLanguage(), notNullValue());
    assertThat(reservations, notNullValue());
    assertThat(responsePayment, notNullValue());
    assertThat(responsePayment.getPaymentMethods(), notNullValue());
    assertThat(responsePayment.getPaymentMethods().size(), is(1));
    if (disablePaymentsFf) {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(RESERVE_WITHOUT_CARD));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(NON_GUARANTEED_BOOKING));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(PaymentOptionTest.PAY_ON_ARRIVAL));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(1));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.TRUE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.TRUE));
    } else {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(ACCOUNT_COMPANY));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(ANY_NAME));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(2));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.FALSE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.FALSE));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentMethods_UK_ShouldReturnOk(boolean disablePaymentsFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);

    final var requestPayment = createPaymentRequest();
    if (disablePaymentsFf) {
      doReturn(createListFailSafePaymentMethods()).when(defaultPaymentMethodsPort).getFailSafePaymentCcuiMethods();
    } else {
      doReturn(createListDefaultPaymentMethods()).when(defaultPaymentMethodsPort).getDefaultPaymentCcuiMethods();
    }
    doReturn(createHotelInfo(HOTEL_COUNTRY_DE, createAcceptedCreditCard(), createPaymentProvider())).when(
        hotelInfoPort).findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage());
    doReturn(createReservation()).when(reservationPort).findReservations(requestPayment.getBasketReference(), false);

    //Act
    var responsePayment = this.paymentCcuiMethodsService.getPaymentMethods(requestPayment);
    //Assert
    assertThat(responsePayment, notNullValue());
    assertThat(responsePayment.getPaymentMethods(), notNullValue());
    assertThat(responsePayment.getPaymentMethods().size(), is(1));

    if (disablePaymentsFf) {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(RESERVE_WITHOUT_CARD));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(NON_GUARANTEED_BOOKING));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.TRUE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(PaymentOptionTest.PAY_ON_ARRIVAL));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(1));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.TRUE));
    } else {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(ACCOUNT_COMPANY));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(ANY_NAME));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.FALSE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(2));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.FALSE));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentMethods_DE_ShouldReturnOk(boolean disablePaymentsFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);

    final var requestPayment = createPaymentRequests();
    if (disablePaymentsFf) {
      doReturn(createListFailSafePaymentMethods()).when(defaultPaymentMethodsPort).getFailSafePaymentCcuiMethods();
    } else {
      doReturn(createListDefaultPaymentMethods()).when(defaultPaymentMethodsPort).getDefaultPaymentCcuiMethods();
    }
    doReturn(createHotelInfo(HOTEL_COUNTRY_UK, createAcceptedCreditCard(), null)).when(hotelInfoPort)
        .findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
            requestPayment.getLanguage());
    doReturn(createReservation()).when(reservationPort).findReservations(requestPayment.getBasketReference(), false);


    //Act
    var responsePayment = this.paymentCcuiMethodsService.getPaymentMethods(requestPayment);

    //Assert
    assertThat(responsePayment, notNullValue());
    assertThat(responsePayment.getPaymentMethods(), notNullValue());
    assertThat(responsePayment.getPaymentMethods().size(), is(1));

    if (disablePaymentsFf) {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(RESERVE_WITHOUT_CARD));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(NON_GUARANTEED_BOOKING));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.TRUE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(PaymentOptionTest.PAY_ON_ARRIVAL));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(1));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.TRUE));
    } else {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(ACCOUNT_COMPANY));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(ANY_NAME));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.FALSE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(2));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.FALSE));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentMethods_Germany_ShouldReturnOk(boolean disablePaymentsFf) {
    //Arrange
    final var requestPayment = createPaymentRequests();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);
    if (disablePaymentsFf) {
      doReturn(createListFailSafePaymentMethods()).when(defaultPaymentMethodsPort).getFailSafePaymentCcuiMethods();
    } else {
      doReturn(createListDefaultPaymentMethods()).when(defaultPaymentMethodsPort).getDefaultPaymentCcuiMethods();
    }
    when(hotelInfoPort.findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage())).thenReturn(
        createHotelInfo(HOTEL_COUNTRY_DE, createAcceptedCreditCard(), null));
    when(reservationPort.findReservations(requestPayment.getBasketReference(), false)).thenReturn(
        createReservation());
    //Act
    var responsePayment = this.paymentCcuiMethodsService.getPaymentMethods(requestPayment);

    //Assert
    assertThat(responsePayment, notNullValue());
    assertThat(responsePayment.getPaymentMethods(), notNullValue());
    assertThat(responsePayment.getPaymentMethods().size(), is(1));
    if (disablePaymentsFf) {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(RESERVE_WITHOUT_CARD));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(NON_GUARANTEED_BOOKING));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.TRUE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(PaymentOptionTest.PAY_ON_ARRIVAL));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(1));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.TRUE));
    } else {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(ACCOUNT_COMPANY));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(ANY_NAME));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.FALSE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(2));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.FALSE));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentMethods_ANY_TYPE_Ok(boolean disablePaymentsFf) {
    //Arrange
    final var requestPayment = createPaymentRequests();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getDisablePayments()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getDisablePayments()), any())).thenReturn(disablePaymentsFf);
    if (disablePaymentsFf) {
      doReturn(createListFailSafePaymentMethods()).when(defaultPaymentMethodsPort).getFailSafePaymentCcuiMethods();
    } else {
      doReturn(createListDefaultPaymentMethodsAnyType()).when(defaultPaymentMethodsPort).getDefaultPaymentCcuiMethods();
    }
    when(hotelInfoPort.findHotelPaymentDetails(HOTEL_CODE, requestPayment.getCountry(),
        requestPayment.getLanguage())).thenReturn(
        createHotelInfo(HOTEL_COUNTRY_DE, createAcceptedCreditCard(), null));
    when(reservationPort.findReservations(requestPayment.getBasketReference(), false)).thenReturn(
        createReservation());
    //Act
    var responsePayment = this.paymentCcuiMethodsService.getPaymentMethods(requestPayment);
    //Assert
    assertThat(responsePayment, notNullValue());
    assertThat(responsePayment.getPaymentMethods(), notNullValue());
    assertThat(responsePayment.getPaymentMethods().size(), is(1));
    if (disablePaymentsFf) {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(RESERVE_WITHOUT_CARD));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(NON_GUARANTEED_BOOKING));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.TRUE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(PaymentOptionTest.PAY_ON_ARRIVAL));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(1));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.TRUE));
    } else {
      assertThat(responsePayment.getPaymentMethods().get(0).getType(), is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getName(), is(ANY_NAME));
      assertThat(responsePayment.getPaymentMethods().get(0).isEnabled(), is(Boolean.FALSE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getType(),
          is(ANY_TYPE));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).getOrder(),
          is(2));
      assertThat(responsePayment.getPaymentMethods().get(0).getPaymentOptions().get(0).isEnabled(),
          is(Boolean.FALSE));
    }
  }

  private List<PaymentMethod> createListDefaultPaymentMethods() {
    final var paymentMethod1 = new PaymentMethod();
    paymentMethod1.setReasons(new ArrayList<>());
    paymentMethod1.setType(ACCOUNT_COMPANY);
    paymentMethod1.setName(ANY_NAME);
    paymentMethod1.setEnabled(Boolean.TRUE);
    paymentMethod1.setPaymentOptions((List.of(PaymentOptionTest.createPaymentOption())));
    return List.of(paymentMethod1);
  }

  private List<PaymentMethod> createListFailSafePaymentMethods() {
    final var paymentMethod = new PaymentMethod();
    paymentMethod.setReasons(new ArrayList<>());
    paymentMethod.setType(RESERVE_WITHOUT_CARD);
    paymentMethod.setName(NON_GUARANTEED_BOOKING);
    paymentMethod.setEnabled(Boolean.TRUE);
    paymentMethod.setPaymentOptions(
        (List.of(PaymentOptionTest.createPaymentOption(PaymentOptionTest.PAY_ON_ARRIVAL, 1, Boolean.TRUE))));
    return List.of(paymentMethod);
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
    return hotelInfo;
  }
  private Reservation createReservation() {
    return Reservation.builder()
        .hotelId("DUNCRO")
        .hotelPaymentPolicies(Set.of(PaymentPolicy.PAY_NOW))
        .arrivalDate(LocalDate.of(2022,07, 25))
        .build();
  }

  private PaymentMethodsRequest createPaymentRequest() {
    return PaymentMethodsRequest.builder()
        .country("gb")
        .language("en")
        .basketReference("DUNCRO6988257")
        .userType(UserType.AGENT)
        .build();
  }
  private PaymentMethodsRequest createPaymentRequests() {
    return PaymentMethodsRequest.builder()
        .country("de")
        .language("de")
        .basketReference("DUNCRO6988257")
        .userType(UserType.AGENT)
        .build();
  }
  private List<PaymentMethod> createListDefaultPaymentMethodsAnyType() {
    final var paymentMethod1 = new PaymentMethod();
    paymentMethod1.setReasons(new ArrayList<>());
    paymentMethod1.setType(ANY_TYPE);
    paymentMethod1.setName(ANY_NAME);
    paymentMethod1.setEnabled(Boolean.TRUE);
    paymentMethod1.setPaymentOptions((List.of(PaymentOptionTest.createPaymentOption())));
    return List.of(paymentMethod1);

  }
}