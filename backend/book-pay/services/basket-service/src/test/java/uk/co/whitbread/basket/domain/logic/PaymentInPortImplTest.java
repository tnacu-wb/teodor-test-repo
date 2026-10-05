package uk.co.whitbread.basket.domain.logic;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.UNNECESSARY;
import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.RESERVE_WITHOUT_CARD;
import static uk.co.whitbread.basket.infrastructure.rest.client.cdh.CdhTestUtils.createCdhSearchCompaniesResponse;
import static uk.co.whitbread.basket.infrastructure.rest.client.cdh.CdhTestUtils.createCdhSearchCompaniesResponseInvalidAddressLine;
import static uk.co.whitbread.basket.infrastructure.rest.client.cdh.CdhTestUtils.createCdhSearchCompaniesResponseInvalidPostalCode;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.CardDetailsDeclinedException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentFraudException;
import uk.co.whitbread.basket.domain.exception.PaymentOptionNotValidException;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.logic.config.ThreecProp;
import uk.co.whitbread.basket.domain.logic.mapper.CompanyAddressMapper;
import uk.co.whitbread.basket.domain.logic.mapper.PaymentResponseWebhookMapper;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketErrorType;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.business.in.BusinessAllowance;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNote;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.content.out.Note;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessAccount;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswer;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.basket.domain.model.payments.in.Guest;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.Address;
import uk.co.whitbread.basket.domain.model.payments.out.Billing;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus;
import uk.co.whitbread.basket.domain.model.reservation.out.PaymentCard;
import uk.co.whitbread.basket.domain.model.reservation.out.RateInfo;
import uk.co.whitbread.basket.domain.model.reservation.out.RateInfoSummary;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationPackagesDetails;
import uk.co.whitbread.basket.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOhipOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CdhSearchCompaniesOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.generated.models.ohip.RateInfoDto;
import uk.co.whitbread.basket.generated.models.ohip.RateInfoSummaryDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByIdDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentDto.SubTypeEnum;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class PaymentInPortImplTest {

  public static final String PAGE_HTML = "abc==";
  public static final String TEMPLATE = "mock";
  public static final String SESSION_ID = "1234567890";
  public static final long BASKET_VALIDITY = 1680000L;
  public static final String PAYMENT_ID = "87064827403D";
  public static final String REFERENCE = "ABC123456";
  public static final String BASKET_REFERENCE = "ABC-8c8c4a47-4b58-4ac0-9bd3-d78869537fe1";
  private static final Instant FIXED_INSTANT = Instant.parse("2024-10-21T15:00:00Z");
  private static final long CLEAN_UP_TIME_LONG = 5000;
  private boolean setMockInstant = false;

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private PaymentOutPort paymentOutPort;

  @Mock
  private BasketOrderOutPort basketOrderOutPort;

  @Mock
  private HotelReservationOutPort reservationOutPort;

  @Mock
  private EmailNotificationService emailNotificationService;

  @Mock
  private RefundOutPort refundOutPort;

  @Mock
  private ContentOutPort contentOutPort;

  @Mock
  private RulesAgentOutPort rulesAgentOutPort;

  @Mock
  private DistributionProperties distributionProperties;

  @InjectMocks
  private PaymentInPortImpl underTest;
  @Mock
  private PaymentResponseWebhookMapper paymentResponseWebhookMapper;
  @Mock
  private BasketOhipOutPort basketOhipOutPort;
  @Spy
  private final ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private ThreecProp threecProperties;
  @Mock
  private CdhSearchCompaniesOutPort cdhSearchCompaniesOutPort;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private CompanyAddressMapper companyAddressMapper;
  @Mock
  private CleanUpTime cleanUpTime;
  private MockedStatic<Instant> mockedStatic;

  @BeforeEach
  void setUp() {
    underTest = new PaymentInPortImpl(BASKET_VALIDITY, basketOrderOutPort,
        reservationOutPort, paymentOutPort, basketOutPort, contentOutPort,
        emailNotificationService, refundOutPort, rulesAgentOutPort, distributionProperties, threecProperties,
        paymentResponseWebhookMapper, concurrentTracer, basketOhipOutPort, unleashWrapper,
        cdhSearchCompaniesOutPort, authenticatedUserService, companyAddressMapper, cleanUpTime);
  }

  @AfterEach
  public void destroy() {
    if (setMockInstant) {
      mockedStatic.close();
      setMockInstant = false;
    }
  }

  @Test
  void testPaymentWebhookSuccess() {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_ON_ARRIVAL.name())
        .channel("PI").build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    // Act
    PaymentResponse response = underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation);

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getBooking(), notNullValue());
    assertThat(response.getBooking().getReference(), is(BASKET_REFERENCE));
    assertThat(response.getBookingReference(), is(REFERENCE));
    assertThat(response.getPaymentId(), is(PAYMENT_ID));

    verify(basketOutPort, times(1)).updateBasketStatus(BASKET_REFERENCE, BasketStatus.PROCESSING,
        Optional.empty());
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort,
        contentOutPort, emailNotificationService, refundOutPort);
  }

  @ParameterizedTest
  @CsvSource({
      "123, 123, PAYMENT_CONTACT_BANK_EXCEPTION_VALUE, true",
      "456, 456, PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE, true",
      "789, 789, PAYMENT_TRY_AGAIN_EXCEPTION_VALUE, true",
      "999, Card was declined and no payment attempt has been made, errors.payment.generic, true",
      "123, Card was declined and no payment attempt has been made, errors.payment.generic, false",
      "456, Card was declined and no payment attempt has been made, errors.payment.generic, false",
      "789, Card was declined and no payment attempt has been made, errors.payment.generic, false",
      "999, Card was declined and no payment attempt has been made, errors.payment.generic, false"
  })
  void testPaymentWebhook_paymentFailure_returnCode(String returnCode, String expectedErrorCode,
      String expectedErrorDescription, Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_ON_ARRIVAL.name())
        .channel("PI").build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    paymentsConfirmation.setReturnCode(returnCode);
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("ACCEPT");
    paymentResponse.getProviderResponse().getThreecResponse().setProviderReason("LOW FUNDS");
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    Map<String, Set<Integer>> returnCodes = new HashMap<>();
    returnCodes.put("contactBankCodes", Set.of(123));
    returnCodes.put("incorrectCardDetailsCodes", Set.of(456));
    returnCodes.put("tryAgainCodes", Set.of(789));

    if(mockedFeatureThreecpReturnCodesMappingFf) {
      when(threecProperties.getReturnCodes()).thenReturn(returnCodes);
    }

    // Act
    assertThrows(PaymentException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    assertEquals(expectedErrorCode, basket.getBasketError().getCode());
    assertEquals(expectedErrorDescription, basket.getBasketError().getDescription());
    assertEquals(BasketErrorType.PAYMENT, basket.getBasketError().getType());
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testPaymentWebhook_paymentFailure_returnCode_invalid(Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_ON_ARRIVAL.name())
        .channel("PI").build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    paymentsConfirmation.setReturnCode("abc");
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("ACCEPT");
    paymentResponse.getProviderResponse().getThreecResponse().setProviderReason("LOW FUNDS");
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);

    Map<String, Set<Integer>> returnCodes = new HashMap<>();
    returnCodes.put("contactBankCodes", Set.of(123));
    returnCodes.put("incorrectCardDetailsCodes", Set.of(456));
    returnCodes.put("tryAgainCodes", Set.of(789));

    // Act
    assertThrows(PaymentException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    assertEquals("Card was declined and no payment attempt has been made", basket.getBasketError().getCode());
    assertEquals("errors.payment.generic", basket.getBasketError().getDescription());
    assertEquals(BasketErrorType.PAYMENT, basket.getBasketError().getType());
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testPaymentWebhook_paymentFailure_Ciol_Pay_returnCode_invalid(Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_ON_ARRIVAL.name())
        .isCheckInOnlinePay(true)
        .channel("PI").build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    paymentsConfirmation.setReturnCode("abc");
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("ACCEPT");
    paymentResponse.getProviderResponse().getThreecResponse().setProviderReason("LOW FUNDS");
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);

    Map<String, Set<Integer>> returnCodes = new HashMap<>();
    returnCodes.put("contactBankCodes", Set.of(123));
    returnCodes.put("incorrectCardDetailsCodes", Set.of(456));
    returnCodes.put("tryAgainCodes", Set.of(789));

    // Act
    assertThrows(PaymentException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    assertEquals("Card was declined and no payment attempt has been made", basket.getBasketError().getCode());
    assertEquals("errors.payment.generic", basket.getBasketError().getDescription());
    assertEquals(BasketErrorType.PAYMENT, basket.getBasketError().getType());
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
    assertEquals(BasketStatus.CIOL_FAILED,basket.getStatus());
  }

  @ParameterizedTest
  @CsvSource({
      "false,false",
      "true,true"
  })
  void testPaymentWebhook_paymentFailure_returnCode_null(Boolean mockedFeatureThreecpReturnCodesMappingFf,
      boolean isCheckInOnline) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_ON_ARRIVAL.name())
        .channel("PI").build();

    if(isCheckInOnline) {
      basket.setIsCheckInOnlinePay(true);
    }

    final var paymentsConfirmation = mockPaymentsConfirmation();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("ACCEPT");
    paymentResponse.getProviderResponse().getThreecResponse().setProviderReason("LOW FUNDS");
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);

    Map<String, Set<Integer>> returnCodes = new HashMap<>();
    returnCodes.put("contactBankCodes", Set.of(123));
    returnCodes.put("incorrectCardDetailsCodes", Set.of(456));
    returnCodes.put("tryAgainCodes", Set.of(789));

    // Act
    assertThrows(PaymentException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    assertEquals("Card was declined and no payment attempt has been made", basket.getBasketError().getCode());
    assertEquals("errors.payment.generic", basket.getBasketError().getDescription());
    assertEquals(BasketErrorType.PAYMENT, basket.getBasketError().getType());
    if (isCheckInOnline) {
      assertEquals(BasketStatus.CIOL_FAILED,basket.getStatus());
    } else {
      assertEquals(BasketStatus.FAILED,basket.getStatus());
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testPaymentWebhook_paymentFailure_lowFunds(Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);

    final var basket = mockTempBasket();
    basket.setStatus(BasketStatus.PAY_PENDING);

    final var paymentsConfirmation = mockPaymentsConfirmation();
    paymentsConfirmation.setPaymentStatus("FAILURE");
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("ACCEPT");
    paymentResponse.getProviderResponse().getThreecResponse().setProviderReason("LOW FUNDS");

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    // Assert
    assertThrows(PaymentException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @Test
  void testPaymentWebhookSuccessAmend() {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .channel("PI")
        .paymentChannel("WEB")
        .originalBasketId("original-basket-id")
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_NOW.name())
        .build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);
    // Act
    PaymentResponse response = underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation);

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getBooking(), notNullValue());
    assertThat(response.getBooking().getReference(), is(BASKET_REFERENCE));
    assertThat(response.getBookingReference(), is(REFERENCE));
    assertThat(response.getPaymentId(), is(PAYMENT_ID));

    verify(basketOutPort, times(1)).updateBasketStatus(BASKET_REFERENCE, BasketStatus.AMENDING,
        Optional.empty());
    verify(basketOrderOutPort, times(1)).processAmend(any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, basketOrderOutPort);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testPaymentWebhook_amend_paymentFailure(Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    final var basket = mockTempBasket();
    basket.setStatus(BasketStatus.PAY_PENDING);

    final var paymentsConfirmation = mockPaymentsConfirmation();
    paymentsConfirmation.setPaymentStatus("FAILURE");
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("TEST");

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);

    // Assert
    assertThrows(PaymentFraudException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testPaymentWebhookFailureAmend(Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .channel("PI")
        .paymentChannel("WEB")
        .originalBasketId("original-basket-id")
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_NOW.name())
        .build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    paymentsConfirmation.setPaymentStatus("FAILURE");
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    paymentResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("TEST");

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);

    // Act
    assertThrows(PaymentFraudException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    verify(basketOutPort, never()).updateBasketStatus(BASKET_REFERENCE, BasketStatus.PROCESSING, Optional.empty());
    verify(basketOrderOutPort, never()).processAmend(any(), any(), any());
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @Test
  void testPaymentWebhookBasketExpiredPayOnArrival() {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .reference(REFERENCE).hotelId(hotelId).basketId(BASKET_REFERENCE)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .createdAt(createdAt)
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PaymentOption.PAY_ON_ARRIVAL.name())
        .build();
    final var paymentsConfirmation = mockPaymentsConfirmation();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);


    // Act
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    verify(basketOutPort, times(1)).updateBasket(basket);
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort,
        contentOutPort, emailNotificationService, refundOutPort);
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @Test
  void testPaymentWebhookBasketExpiredPayNow() {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .hotelId(hotelId)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .createdAt(createdAt)
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PaymentOption.PAY_NOW.name())
        .build();
    final var paymentsConfirmation = mockPaymentsConfirmation();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    // Act
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));

    // Assert
    verify(basketOutPort).updateBasket(basket);
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
    assertEquals(BasketStatus.FAILED, basket.getStatus());
    verify(refundOutPort).processRefund(any(), any(), any());
    verify(emailNotificationService).sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort,
        contentOutPort, emailNotificationService, refundOutPort);
  }

  @Test
  void paymentWebhook_ShouldThrowNoPaymentExceptionWhenOrderNotProcessed() {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";

    final var basket = Basket.builder()
        .reference(REFERENCE)
        .hotelId(hotelId)
        .basketId(BASKET_REFERENCE)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PaymentOption.PAY_NOW.name())
        .channel("PI").build();

    PaymentResponse paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus(ThreecPaymentStatus.SUCCESS.getStatus());
    paymentResponse.getProviderResponse().getThreecResponse().setProviderResult("1111");

    final var paymentsConfirmation = mockPaymentsConfirmation();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    doThrow(new PaymentException(ErrorCode.DIGITAL_BASKET_ORDER_NO_ITEMS_EXCEPTION,
        "Couldn't process order")).when(basketOrderOutPort)
        .processOrder(any(), any(), any(), any());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    //Assert

    assertThrows(PaymentException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));
    verify(refundOutPort).processRefund(any(), any(), any());
    verify(emailNotificationService).sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort,
        contentOutPort, emailNotificationService, refundOutPort);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void paymentWebhook_ShouldThrowNoPaymentTakenException(Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);

    final var basket = Basket.builder()
        .reference(REFERENCE)
        .hotelId(hotelId)
        .basketId(BASKET_REFERENCE)
        .status(BasketStatus.PAY_PENDING)
        .userId(userId)
        .items(List.of(BasketItem.builder().type("STAY").sourceId("1234").build()))
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PaymentOption.PAY_NOW.name())
        .channel("PI").build();
    var paymentsConfirmation = mockPaymentsConfirmation();
    var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentsConfirmation.setPaymentStatus(ThreecPaymentStatus.NO_PAYMENT_ATTEMPT.getStatus());
    paymentResponse.setPaymentStatus(ThreecPaymentStatus.NO_PAYMENT_ATTEMPT.getStatus());
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    //Assert
    assertThrows(CardDetailsDeclinedException.class,
        () -> underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation));
    verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
    verifyNoMoreInteractions(basketOutPort, basketOrderOutPort, paymentOutPort);
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @Test
  void paymentWebhook_ShouldCallRefundWhenBasketFailed() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var userId = "userId";
    final var paymentOption = PaymentOption.PAY_NOW;

    final var basket = Basket.builder()
        .reference(reference).hotelId(hotelId).status(BasketStatus.FAILED).userId(userId)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PaymentOption.PAY_NOW.name())
        .channel("PI").build();

    final var paymentsConfirmation = mockPaymentsConfirmation();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    //Act
    underTest.paymentWebhook(REFERENCE, paymentsConfirmation);

    //Assert
    verify(refundOutPort, times(1)).processRefund(any(), any(), any());
    verify(emailNotificationService, times(1))
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort,
        contentOutPort, emailNotificationService, refundOutPort);
  }


  @Test
  void initiatePayment_CompletedBasket_ShouldThrowException() {
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name());
    //Arrange
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(mockCompletedBasket());

    //Assert
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment_ExpiredBasket_ShouldThrowException() {
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name());

    //Arrange
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(mockExpiredBasket());

    //Assert
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void initiatePayment__payNow(boolean checkInOnline) {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    var reservationByBasketRefResponse = mockReservationsDetails();
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(reservationByBasketRefResponse);
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(checkInOnline);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var request = mockPaymentRequest(PAY_NOW.name());
    request.setCiol(checkInOnline);
    var paymentResponse = underTest.initiatePaymentProcess(BASKET_REFERENCE, request);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    if (checkInOnline) {
      verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
    }
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    ArgumentCaptor<PaymentRequest> paymentRequestCaptor = ArgumentCaptor.forClass(PaymentRequest.class);
    verify(paymentOutPort, times(1)).createPayment(paymentRequestCaptor.capture());
    var paymentRequest = paymentRequestCaptor.getValue();
    if (checkInOnline) {
      assertEquals(reservationByBasketRefResponse.getBalanceOutstanding(),
          paymentRequest.getPayment().getAmount().getMinorUnits().divide(valueOf(100), UNNECESSARY));
    } else {
      assertEquals(reservationByBasketRefResponse.getTotalCost(),
          paymentRequest.getPayment().getAmount().getMinorUnits().divide(valueOf(100), UNNECESSARY));
    }
    verify(basketOhipOutPort).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort, basketOhipOutPort);
  }

  @Test
  void initiatePayment__payNow_disablesReservationCacheForCiol() {
    var basket = mockFreshBasket();
    var reservationByBasketRefResponse = mockReservationsDetails();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(reservationByBasketRefResponse);
    when(paymentOutPort.createPayment(any())).thenReturn(mockPaymentResponse());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInfoResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    var request = mockPaymentRequest(PAY_NOW.name());
    request.setCiol(true);
    request.setUseCache(true);

    underTest.initiatePaymentProcess(BASKET_REFERENCE, request);

    verify(reservationOutPort).getReservationsByBasketReference(anyString(),
        eq(Boolean.FALSE.toString()), eq(true), eq(false));
  }

  @Test
  void initiatePayment__payNow_UK_Hotel() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse_UK());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest(PAY_NOW.name()));

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(0)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verify(basketOhipOutPort,times(0)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort, basketOhipOutPort);
  }

  @Test
  void initiatePayment__payNow_featureFalse() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(false);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest(PAY_NOW.name()));

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(0)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verify(basketOhipOutPort).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort, basketOhipOutPort);
  }

  @Test
  void initiatePayment__payNow_blankBillingAddress() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentRequest =  mockPaymentRequest(PAY_NOW.name());
    paymentRequest.getPayment().getBilling().setAddress(null);
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__payNow_CaptureCardBillingAddress() {
    var basket = mockFreshBasket();
    var account = mockAccount();
    var cdhSearchCompaniesResponse = createCdhSearchCompaniesResponse();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
            anyString(), anyBoolean(), anyBoolean())).thenReturn(
            mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCaptureBillingAddressBb()))
            .thenReturn(true);
    CustomJwtAuthenticationToken jwtAuthenticationToken = mock(CustomJwtAuthenticationToken.class);
    when(jwtAuthenticationToken.getAccount()).thenReturn(account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
    when(cdhSearchCompaniesOutPort.searchCompaniesFromCdh(any(CdhSearchCompaniesRequest.class)))
            .thenReturn(cdhSearchCompaniesResponse);

    //Act
    var paymentRequest =  mockPaymentRequestWithCardBilling(PAY_NOW.name());
    var paymentResponse =
            underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__payNow_newCardPayment() {
    //Arrange
    var basket = mockFreshBasket();
    var account = mockAccount();
    var cdhSearchCompaniesResponse = createCdhSearchCompaniesResponse();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
            anyString(), anyBoolean(), anyBoolean())).thenReturn(
            mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCaptureBillingAddressBb()))
            .thenReturn(true);
    CustomJwtAuthenticationToken jwtAuthenticationToken = mock(CustomJwtAuthenticationToken.class);
    when(jwtAuthenticationToken.getAccount()).thenReturn(account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
    when(cdhSearchCompaniesOutPort.searchCompaniesFromCdh(argThat(req ->
            req.getGlobalCompanyId() == 12345 &&
                    req.getAccessContext().equals("BB") &&
                    (req.getAccessedBy() == null || req.getAccessedBy().equals("test.email@whitbread.com")))))
            .thenReturn(cdhSearchCompaniesResponse);
    //Act
    var paymentRequest =  mockPaymentRequestWithNewCardPayment(PAY_NOW.name());
    var paymentResponse =
            underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOhipOutPort, times(2)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__payNow_centrallyStoredCard() {
    //Arrange
    var basket = mockFreshBasket();
    var account = mockAccount();
    var cdhSearchCompaniesResponse = createCdhSearchCompaniesResponse();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
            anyString(), anyBoolean(), anyBoolean())).thenReturn(
            mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCaptureBillingAddressBb()))
            .thenReturn(true);
    CustomJwtAuthenticationToken jwtAuthenticationToken = mock(CustomJwtAuthenticationToken.class);
    when(jwtAuthenticationToken.getAccount()).thenReturn(account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
    when(cdhSearchCompaniesOutPort.searchCompaniesFromCdh(argThat(req ->
            req.getGlobalCompanyId() == 12345 &&
                    req.getAccessContext().equals("BB") &&
                    (req.getAccessedBy() == null || req.getAccessedBy().equals("test.email@whitbread.com")))))
            .thenReturn(cdhSearchCompaniesResponse);
    //Act
    var paymentRequest =  mockPaymentRequestWithCentrallyStoredPayment(PAY_NOW.name());
    var paymentResponse =
            underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }


  @Test
  void initiatePayment__payNow_newCardPayment_invalidAddress() {
    //Arrange
    var basket = mockFreshBasket();
    var account = mockAccount();
    var cdhSearchCompaniesResponse = createCdhSearchCompaniesResponseInvalidAddressLine();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
            anyString(), anyBoolean(), anyBoolean())).thenReturn(
            mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCaptureBillingAddressBb()))
            .thenReturn(true);
    CustomJwtAuthenticationToken jwtAuthenticationToken = mock(CustomJwtAuthenticationToken.class);
    when(jwtAuthenticationToken.getAccount()).thenReturn(account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
    when(cdhSearchCompaniesOutPort.searchCompaniesFromCdh(argThat(req ->
            req.getGlobalCompanyId() == 12345 &&
                    req.getAccessContext().equals("BB") &&
                    (req.getAccessedBy() == null || req.getAccessedBy().equals("test.email@whitbread.com")))))
            .thenReturn(cdhSearchCompaniesResponse);

    //Act
    var paymentRequest =  mockPaymentRequestWithNewCardPayment(PAY_NOW.name());
    var paymentResponse =
            underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__payNow_newCardPayment_invalidPostalCode() {
    //Arrange
    var basket = mockFreshBasket();
    var account = mockAccount();
    var cdhSearchCompaniesResponse = createCdhSearchCompaniesResponseInvalidPostalCode();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
            anyString(), anyBoolean(), anyBoolean())).thenReturn(
            mockReservationsDetails());
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCaptureBillingAddressBb()))
            .thenReturn(true);
    CustomJwtAuthenticationToken jwtAuthenticationToken = mock(CustomJwtAuthenticationToken.class);
    when(jwtAuthenticationToken.getAccount()).thenReturn(account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
    when(cdhSearchCompaniesOutPort.searchCompaniesFromCdh(argThat(req ->
            req.getGlobalCompanyId() == 12345 &&
                    req.getAccessContext().equals("BB") &&
                    (req.getAccessedBy() == null || req.getAccessedBy().equals("test.email@whitbread.com")))))
            .thenReturn(cdhSearchCompaniesResponse);
    //Act
    var paymentRequest =  mockPaymentRequestWithNewCardPayment(PAY_NOW.name());
    var paymentResponse =
            underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verify(reservationOutPort, times(0)).updateSpecialRequests(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void initiatePaypalPayment__payNow(boolean checkInOnline) {
    var basket = mockFreshBasket();
    basket.setIsCheckInOnlinePay(checkInOnline);

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    when(paymentOutPort.createPayment(any()))
        .thenReturn((mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE)));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(checkInOnline);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var request = mockPaypalPaymentRequest(PAY_NOW.name());
    request.setCiol(checkInOnline);
    var paymentResponse = underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE, request);

    //Assert
    assertThat(paymentResponse, notNullValue());
    if (checkInOnline) {
      verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
    }
    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
    verifyNoMoreInteractions(reservationOutPort, basketOhipOutPort);
  }

  @Test
  void initiatePaypalPayment__payNow_disablesReservationCacheForCiol() {
    var basket = mockFreshBasket();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(mockReservationsDetails());
    when(paymentOutPort.createPayment(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInfoResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    var request = mockPaypalPaymentRequest(PAY_NOW.name());
    request.setCiol(true);
    request.setUseCache(true);

    underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE, request);

    verify(reservationOutPort).getReservationsByBasketReference(anyString(),
        eq(Boolean.FALSE.toString()), eq(true), eq(false));
  }

  @Test
  void initiatePaypalPayment__payNow_featureFalse() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    when(paymentOutPort.createPayment(any()))
        .thenReturn((mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE)));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(false);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentResponse =
        underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE,
            mockPaypalPaymentRequest(PAY_NOW.name()));

    //Assert
    assertThat(paymentResponse, notNullValue());
    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(reservationOutPort, times(0)).updateBusinessItems(any(), any(), any(), any(), any());
    verifyNoMoreInteractions(reservationOutPort, basketOhipOutPort);

  }

  @Test
  void initiatePaypalPayment__payNow_blankBillingAddress() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(mockReservationsDetails());
    when(paymentOutPort.createPayment(any()))
        .thenReturn((mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE)));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentRequest = mockPaypalPaymentRequest(PAY_NOW.name());
    paymentRequest.getPayment().getBilling().setAddress(null);
    paymentRequest.getPayment().getBilling().setBookerIsNotGuest(false);
    var paymentResponse =
        underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    verify(basketOhipOutPort, times(0)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(reservationOutPort, times(0)).updateBusinessItems(any(), any(), any(), any(), any());
    verifyNoMoreInteractions(reservationOutPort, basketOhipOutPort);

  }

  @Test
  void initiatePaypalPayment_processBasketException_payNow() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    when(paymentOutPort.createPayment(any()))
        .thenReturn((mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE)));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);
    doThrow(new PaymentException(ErrorCode.DIGITAL_BASKET_ORDER_NO_ITEMS_EXCEPTION,
        "Couldn't process order")).when(basketOrderOutPort)
        .processOrder(any(), any(), any(), any());

    assertThrows(PaymentException.class,
        () -> underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE,
            mockPaypalPaymentRequest(PAY_NOW.name())));

    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
  }

  @Test
  void initiatePaypalPayment_processPaymentException_payNow() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(mockReservationsDetails());

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);
    doThrow(new PaymentException(ErrorCode.CREATE_PAYMENT_CLIENT_EXCEPTION,
        "Couldn't process order")).when(paymentOutPort)
        .createPayment(any());

    assertThrows(PaymentException.class,
        () -> underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE,
            mockPaypalPaymentRequest(PAY_NOW.name())));
    verify(basketOhipOutPort, times(1)).updateReservationBillingAddress(any(), any(),  anyBoolean(), anyBoolean(), anyBoolean());
    verify(reservationOutPort, times(1)).updateBusinessItems(any(), any(), any(), any(), any());
  }

  @Test
  void initiatePaypalPayment_CompletedBasket_ShouldThrowException() {
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name());
    //Arrange
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(mockCompletedBasket());

    //Assert
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE, mockPaymentRequest));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment_isCiol() {
    //Arrange
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(mockCompletedBasket());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(reservationOutPort.getReservationsByBasketReference(any(), any(), anyBoolean(), anyBoolean()))
        .thenReturn(mockReservationsDetails());
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));

    //Act
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name(), true);
    var result = underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest);

    //Assert
    assertNotNull(result);
  }

  @Test
  void initiatePaypalPayment_ExpiredBasket_ShouldThrowException() {
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name());

    //Arrange
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(mockExpiredBasket());

    //Assert
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE, mockPaymentRequest));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__payNow_hotelId_basketRef_blank() {
    var basket = mockNoRefNoHotelIdBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    //Assert
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest
            .initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest(PAY_NOW.name())));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePaypalPayment__payNow_hotelId_basketRef_blank() {
    var basket = mockNoRefNoHotelIdBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    //Assert
    assertThrows(BasketReferenceNotValidException.class,
        () -> underTest
            .initiatePaypalPaymentProcess(BASKET_REFERENCE, mockPaymentRequest(PAY_NOW.name())));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__amendFlow() {
    var basket = mockTempBasket();
    var paymentRequest = mockPaymentRequest(PAY_NOW.name());
    paymentRequest.setTmpBasketRef("tmp-basket-ref");

    var paymentCard = PaymentCard
        .builder()
        .cardHolderName("cardholdername")
        .cardType("VA")
        .expirationDate("2100-12-12")
        .token("token")
        .cardNumberMasked("xxxxxxxxxxxxxxxx")
        .build();

    var reservations = mockReservationsDetails();
    reservations.getReservationByIdList().forEach(reservation -> {
      reservation.setPaymentCard(paymentCard);
    });

    //Arrange
    when(basketOutPort.getBasketById(any())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(any(),
        any(), anyBoolean(), anyBoolean())).thenReturn(reservations);
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOutPort, times(1)).updateBasket(basket);
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiateProcessAmend__ShouldReturnOK() {
    // Arrange
    when(basketOutPort.getBasketById(anyString()))
        .thenReturn(Basket.builder().basketId("BASKET1").build());
    // Act
    underTest.initiateProcessAmend("BASKET-REF", new PaymentsConfirmation());

    // Assert
    verify(basketOutPort, times(1)).updateBasketStatus("BASKET1", BasketStatus.AMENDING,
        Optional.empty());

  }

  @Test
  void initiatePayment__amendNoCardTokenPresent() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequest(PAY_NOW.name());
    paymentRequest.setTmpBasketRef("tmp-basket-ref");
    var paymentCard = PaymentCard
        .builder()
        .cardHolderName("cardholdername")
        .cardType("VA")
        .expirationDate("2100-12-12")
        .token(null)
        .cardNumberMasked("xxxxxxxxxxxxxxxx")
        .build();

    var reservations = mockReservationsDetails();
    reservations.getReservationByIdList().forEach(reservation -> {
      reservation.setPaymentCard(paymentCard);
    });
    //Arrange
    when(basketOutPort.getBasketById(any())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(any(),
        any(), anyBoolean(), anyBoolean())).thenReturn(reservations);

    //Assert
    assertThrows(PaymentException.class,
        () -> underTest.initiatePaymentProcess(BASKET_REFERENCE,
            paymentRequest));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__amendFlow__paymentExcpetion() {
    var basket = mockTempBasket();
    var paymentRequest = mockPaymentRequest(PAY_NOW.name());
    paymentRequest.setTmpBasketRef("tmp-basket-ref");

    var paymentCard = PaymentCard
        .builder()
        .cardHolderName("cardholdername")
        .cardType("VA")
        .expirationDate("2100-12-12")
        .token("token")
        .cardNumberMasked("xxxxxxxxxxxxxxxx")
        .build();

    var reservations = mockReservationsDetails();
    reservations.getReservationByIdList().forEach(reservation -> {
      reservation.setPaymentCard(paymentCard);
    });

    //Arrange
    when(basketOutPort.getBasketById(any())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(any(),
        any(), anyBoolean(), anyBoolean())).thenReturn(reservations);
    doThrow(new PaymentException(ErrorCode.CREATE_PAYMENT_CLIENT_EXCEPTION,
        "Couldn't process order")).when(paymentOutPort)
        .createPayment(any());

    assertThrows(PaymentException.class,
        () -> underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest));
  }

  @Test
  void initiatePayment__POA_Piba__Success() {
    var basket = mockFreshBasket();
    var reservationsDetails = mockReservationsDetails();
    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean(), anyBoolean())).thenReturn(reservationsDetails);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(paymentOutPort.createPayment(any())).thenReturn(mockPaymentResponse());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentRequest = mockPibaPaymentRequest(PAY_ON_ARRIVAL.name());
    paymentRequest.getPayment().setPibaCardPresent(Boolean.FALSE);
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.PAYMENT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails().getPaymentRedirect(), is(PAGE_HTML));
    assertThat(paymentResponse.getPaymentRequiredDetails().getTemplate(), is(TEMPLATE));
    assertThat(paymentResponse.getPaymentRequiredDetails().getSessionId(), is(SESSION_ID));

    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(reservationOutPort).updateBusinessItems(createBusinessItemsPI("BFADBF"),
        List.of(reservationsDetails.getReservationByIdList().get(0).getReservationId()),
        "TestHotelId",
        null, "PI");
    verify(reservationOutPort).updateBusinessItems(createBusinessItemsPI("BFADCT"),
        List.of(reservationsDetails.getReservationByIdList().get(1).getReservationId()),
        "TestHotelId",
        null, "PI");
    //verify(basketOhipOutPort).updateReservationBillingAddress(any(), any());
    verify(basketOutPort, times(1)).updateBasket(basket);
  }

  @Test
  void initiatePayment__invalidPaymentOption() {
    var mockPaymentRequest = mockPaymentRequest("INVALID_PAYMENT_TYPE");
    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(mockFreshBasket());
    mockCheckInOnlineFeatureFlag(false);

    //Assert
    assertThrows(PaymentOptionNotValidException.class,
        () -> underTest.initiatePaymentProcess(BASKET_REFERENCE,
            mockPaymentRequest));
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__reserveWithoutCard() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse = underTest.initiatePaymentProcess(BASKET_REFERENCE,
        mockPaymentRequest(RESERVE_WITHOUT_CARD.name()));

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.NOT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails(), nullValue());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verify(basketOhipOutPort).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort, basketOhipOutPort);

  }

  @Test
  void initiatePayment__reserveWithoutCard_blankBillingAddress() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentRequest =  mockPaymentRequest(RESERVE_WITHOUT_CARD.name());
    paymentRequest.getPayment().getBilling().setAddress(null);
    var paymentResponse = underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.NOT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails(), nullValue());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);

  }

  @Test
  void initiatePayment__reserveWithoutCard_blankAddressType() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentRequest =  mockPaymentRequest(RESERVE_WITHOUT_CARD.name());
    paymentRequest.getPayment().getBilling().getAddress().setAddressType(null);
    var paymentResponse = underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.NOT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails(), nullValue());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);

  }

  @Test
  void initiatePayment__reserveWithoutCard_UkHotel() {
    var basket = mockFreshBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse_UK());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse = underTest.initiatePaymentProcess(BASKET_REFERENCE,
        mockPaymentRequest(RESERVE_WITHOUT_CARD.name()));

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.NOT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails(), nullValue());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment__reserveWithoutCard_amend() {
    var basket = mockTempBasket();

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse = underTest.initiatePaymentProcess(BASKET_REFERENCE,
        mockPaymentRequest(RESERVE_WITHOUT_CARD.name()));

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.NOT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails(), nullValue());
    verify(basketOutPort, times(1)).updateBasketPayment(basket);
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verify(basketOhipOutPort).updateReservationBillingAddress(any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    verifyNoMoreInteractions(basketOutPort, reservationOutPort, basketOrderOutPort, paymentOutPort, basketOhipOutPort);
  }

  private CompanyQuestionAndAnswer createCompanyQuestionAndAnswer() {
    var companyQuestionAndAnswer = new CompanyQuestionAndAnswer();
    companyQuestionAndAnswer.setQuestion("Who am I?");
    companyQuestionAndAnswer.setAnswer("Test");
    return companyQuestionAndAnswer;
  }

  @Test
  void initiatePayment__payUpdateCompanyQuestionAndAnswerDetailsWithUserDefined() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequestMoto();

    List<CompanyQuestionAndAnswer> companyQuestionAndAnswer = of(createCompanyQuestionAndAnswer());

    CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails = new CompanyQuestionAndAnswerDetails();

    companyQuestionAndAnswerDetails.setUserDefinedQuestionAndAnswers(companyQuestionAndAnswer);

    paymentRequest.setCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetails);

    var paymentResponseMock = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void initiatePayment__payUpdateCompanyQuestionAndAnswerDetailsWithCustomerReference() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequestMoto();

    List<CompanyQuestionAndAnswer> companyQuestionAndAnswer = of(createCompanyQuestionAndAnswer());

    CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails = new CompanyQuestionAndAnswerDetails();

    companyQuestionAndAnswerDetails
        .setCustomerReferenceQuestionAndAnswer(companyQuestionAndAnswer.get(0));

    paymentRequest.setCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetails);

    var paymentResponseMock = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void initiatePayment__payUpdateCompanyQuestionAndAnswerDetailsWithPurchaseOrder() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequestMoto();

    List<CompanyQuestionAndAnswer> companyQuestionAndAnswer = of(createCompanyQuestionAndAnswer());

    CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails = new CompanyQuestionAndAnswerDetails();

    companyQuestionAndAnswerDetails
        .setPurchaseOrderQuestionAndAnswer(companyQuestionAndAnswer.get(0));

    paymentRequest.setCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetails);

    var paymentResponseMock = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void initiatePayment__payNowSpecialRequests() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequestMoto();
    paymentRequest.setBookingNotes(List.of("Note 1", "Note 2"));
    paymentRequest.setSpecialRequests(List.of("1", "2"));
    var paymentResponseMock = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponseMock.setPaymentStatus("FAILURE");

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    doNothing().when(reservationOutPort).updateSpecialRequests(any(), any(), any(), any());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void initiatePayment__payNowSpecialRequests2() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequestMoto();
    paymentRequest.setBookingNotes(List.of("Note 1", "Note 2"));
    paymentRequest.setSpecialRequests(List.of());
    var paymentResponseMock = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponseMock.setPaymentStatus("FAILURE");

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    doNothing().when(reservationOutPort).updateSpecialRequests(any(), any(), any(), any());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @Test
  void initiatePayment__amendNoTotalCost() {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    var basket = mockTempBasket();
    var paymentRequest = mockPaymentRequest(PAY_NOW.name());

    var paymentCard = PaymentCard
        .builder()
        .cardHolderName("cardholdername")
        .cardType("VA")
        .expirationDate("2100-12-12")
        .token("token")
        .cardNumberMasked("xxxxxxxxxxxxxxxx")
        .build();

    var reservations = mockReservationsDetails();
    reservations.setTotalCost(null);
    reservations.setBalanceOutstanding(null);
    reservations.getReservationByIdList().forEach(reservation -> {
      reservation.setPaymentCard(paymentCard);
    });
    var message = String.format("Total reservation costs missing for basketReference=%s",
        basket.getBasketId());

    //Arrange
    when(basketOutPort.getBasketById(any())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(any(),
        any(), anyBoolean(), anyBoolean())).thenReturn(reservations);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .thenReturn(true);

    //Assert
    var exception = assertThrows(PaymentException.class,
        () -> underTest.initiatePaymentProcess(BASKET_REFERENCE,
            paymentRequest));

    // Assert
    assertEquals(ErrorCode.DIGITAL_INVALID_TOTAL_COST_EXCEPTION.getCode(),
        exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_INVALID_TOTAL_COST_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void initiatePayment__payNowSpecialRequests3() {
    var basket = mockFreshBasket();
    var paymentRequest = mockPaymentRequestMoto();
    paymentRequest.setBookingNotes(List.of());
    paymentRequest.setSpecialRequests(List.of("1", "2"));
    var paymentResponseMock = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponseMock.setPaymentStatus("FAILURE");

    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(
        mockReservationsDetails());
    doNothing().when(reservationOutPort).updateSpecialRequests(any(), any(), any(), any());
    mockCheckInOnlineFeatureFlag(false);

    //Act
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
  }

  @ParameterizedTest(name = "routing={0}, paymentMethod={1}, balanceOutstanding={2}")
  @CsvSource({
      "1, BU, 0",
      "1, BD, 0",
      "1, VA, 1",
      "0, VA, 1"
  })
  void initiatePayment__payNow_CiolRoutingNotZero(
      Integer routing, String paymentMethod, Integer balance) {

    var basket = mockFreshBasket();
    basket.setIdContext("3rd Party");
    var basketItem = new BasketItem();
    basketItem.setSourceId("RES123");
    basket.setItems(List.of(basketItem));

    when(basketOutPort.getBasketById(anyString()))
        .thenReturn(basket);

    var request = mockPaymentRequest(PAY_NOW.name());
    request.setCiol(true);

    // Feature flag mocks
    var mockedFeatureFlag = mock(FeatureFlag.class);
    var mockedFeature = mock(Feature.class);

    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);

    when(mockedFeatureFlag.getCheckInOnline())
        .thenReturn(mockedFeature);

    when(mockedFeatureFlag.getSaveSecureBooking())
        .thenReturn(mockedFeature);

    when(unleashWrapper.isEnabled(any(Feature.class)))
        .thenReturn(false);

    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());

    // Reservation details mock
    RateInfoSummary summary = RateInfoSummary.builder().routing(BigDecimal.valueOf(routing))
        .build();

    RateInfo rateInfo = new RateInfo();
    rateInfo.setSummary(summary);

    Reservation reservationById = Reservation.builder().rateInfo(rateInfo)
        .roomStay(mockRoomStay())
        .build();

    ReservationByBasketRefResponse reservationDetails = ReservationByBasketRefResponse.builder()
        .totalCost(valueOf(400))
        .paymentMethod(paymentMethod)
        .reservationByIdList(List.of(reservationById))
        .currencyCode("EUR")
        .build();
    when(reservationOutPort.getReservationsByBasketReference(any(), any(), anyBoolean(),
        anyBoolean())).thenReturn(reservationDetails);

    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));

    assertDoesNotThrow(() ->
        underTest.initiatePaymentProcess(BASKET_REFERENCE, request));
  }


  @Test
  void initiatePayment__payNow_CiolRoutingThrowsEx() {

    var basket = mockFreshBasket();
    basket.setIdContext("3rd Party");

    var basketItem = new BasketItem();
    basketItem.setSourceId("RES123");
    basket.setItems(List.of(basketItem));

    when(basketOutPort.getBasketById(anyString()))
        .thenReturn(basket);

    // Reservation details mock
    RateInfoSummary summary = RateInfoSummary.builder().routing(BigDecimal.valueOf(1))
        .build();

    RateInfo rateInfo = new RateInfo();
    rateInfo.setSummary(summary);

    Reservation reservationById = Reservation.builder().rateInfo(rateInfo)
        .roomStay(mockRoomStay())
        .build();

    ReservationByBasketRefResponse reservationDetails = ReservationByBasketRefResponse.builder()
        .totalCost(valueOf(400))
        .balanceOutstanding(BigDecimal.valueOf(0))
        .paymentMethod("VA")
        .reservationByIdList(List.of(reservationById))
        .build();
    when(reservationOutPort.getReservationsByBasketReference(any(), any(), anyBoolean(),
        anyBoolean())).thenReturn(reservationDetails);
    
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(true);

    //Act
    var request = mockPaymentRequest(PAY_NOW.name());
    request.setCiol(true);

    var exception = assertThrows(
        PaymentOptionNotValidException.class,
        () -> underTest.initiatePaymentProcess(
            BASKET_REFERENCE,
            request)
    );

    assertTrue(
        exception.getMessage().contains("CIOL payment failed for 3rdParty.")
    );
    // Assert
    verify(basketOutPort).getBasketById(anyString());
    verify(reservationOutPort).getReservationsByBasketReference(any(), any(), anyBoolean(),
        anyBoolean());
  }

  private PaymentResponse mockPaymentResponse(String paymentId, String reference,
      String bookingReference) {
    return PaymentResponse.builder()
        .paymentId(paymentId)
        .payment(mockPayment())
        .bookingReference(bookingReference)
        .paymentStatus(ThreecPaymentStatus.SUCCESS.getStatus())
        .providerResponse(mockProviderResponse())
        .booking(
            uk.co.whitbread.basket.domain.model.payments.out.Booking.builder().reference(reference)
                .language("en").channel("PI").build())
        .build();
  }

  private uk.co.whitbread.basket.domain.model.payments.out.Payment mockPayment() {
    return uk.co.whitbread.basket.domain.model.payments.out.Payment.builder()
        .billing(Billing.builder().address(
            Address.builder().countryCode("gb")
                .build()).firstName("Sm9obg==").lastName("U21pdGg=").build())
        .amount(uk.co.whitbread.basket.domain.model.payments.out.Amount.builder()
            .currency("EUR")
            .minorUnits(BigDecimal.TEN)
            .build())
        .build();
  }

  private ProviderResponse mockProviderResponse() {
    return ProviderResponse.builder().threecResponse(
        mockThreeCResponse()
    ).build();
  }

  private ThreeCResponse mockThreeCResponse() {
    return ThreeCResponse.builder()
        .providerResult("0")
        .fraudCheckResultReason("NOT REJECTED")
        .expiry("01/80")
        .cardSchemeId("VS")
        .build();
  }

  private PaymentResponse mockPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId(PAYMENT_ID)
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .iPageHtml(PAGE_HTML).sessionId(SESSION_ID).template(TEMPLATE)
                .build())
            .build())
        .build();
  }

  private ReservationByBasketRefResponse mockReservationsDetails() {
    return ReservationByBasketRefResponse.builder()
        .totalCost(valueOf(400))
        .balanceOutstanding(valueOf(321))
        .currencyCode("EUR")
        .reservationByIdList(mockReservations())
        .build();
  }

  private List<Reservation> mockReservations() {
    List<Reservation> reservationInfos = new LinkedList<>();

    reservationInfos.add(Reservation.builder()
        .reservationId("1234567")
        .roomStay(mockRoomStay())
        .reservationPackageList(mockPackages("BFADBF"))
        .build());
    reservationInfos.add(Reservation.builder()
        .reservationId("8901234")
        .roomStay(mockRoomStay())
        .reservationPackageList(mockPackages("BFADCT"))
        .build());
    return reservationInfos;
  }

  private RoomStay mockRoomStay() {
    return RoomStay.builder()
        .roomType("TestRoomType")
        .ratePlanCode("TestRatePlanCode")
        .adultsNumber(1)
        .build();
  }

  private List<ReservationPackagesDetails> mockPackages(String packageCode) {
    return List.of(ReservationPackagesDetails.builder().packageCode(packageCode).build());
  }

  private PaymentRequest mockPaymentRequest(String type) {
    return mockPaymentRequest(type, false);
  }

  private PaymentRequest mockPaymentRequest(String type, boolean isCiol) {
    return PaymentRequest.builder()
        .payment(Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(valueOf(1000))
                .build())
            .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                    .line1("120 Holborn")
                    .line2("")
                    .line3("")
                    .line4("")
                    .countryCode("gb")
                    .postalCode("EC1N 2TD")
                    .addressType("HOME")
                    .build())
                .email("email@whitbread.com")
                .firstName("Samuel")
                .lastName("Whitbread")
                .telephone("0777777777")
                .title("Mr")
                .differentBillingAddress(true)
                .bookerIsNotGuest(true)
                .build())
            .card(Card.builder()
                .cardholderName("Samuel Whitbread")
                .cardType("cardType")
                .cnpRequired(false)
                .expiryMonth("01")
                .expiryYear("24")
                .logoUrl("https://www.premierinn.com/logo")
                .token("4943056398164344242")
                .type("type")
                .build())
            .environment("https://www.premierinn.com")
            .subType("ECOMM")
            .type("CARD")
            .pibaCardPresent(true)
            .build())
        .booking(Booking.builder()
            .arrivalDate("2022-05-17")
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .location("London")
                .name("London Holborn Premier Inn")
                .type("HOTEL")
                .build())
            .channel("PI")
            .departureDate("2022-05-20")
            .journey("BOOKING")
            .language("en")
            .leadGuest(Guest.builder()
                .name("Samuel Whitbread")
                .previousBookings(2)
                .registered(true)
                .registeredSince(LocalDate.of(2022, 4, 1))
                .build())
            .reference("reference")
            .rooms(of(RoomType.builder()
                .adultsNumber(1)
                .rate("SV344")
                .type("DB")
                .build()))
            .type(type)
            .build())
        .isCiol(isCiol)
        .useCache(false)
        .build();
  }

  private PaymentRequest mockPaymentRequestWithCardBilling(String type) {
    return PaymentRequest.builder()
            .payment(Payment.builder()
                    .amount(Amount.builder()
                            .currency("GBP")
                            .minorUnits(valueOf(1000))
                            .build())
                    .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                            .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                                    .line1("120 Holborn")
                                    .line2("")
                                    .line3("")
                                    .line4("")
                                    .countryCode("gb")
                                    .postalCode("EC1N 2TD")
                                    .addressType("HOME")
                                    .build())
                            .cardBillingAddress(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                                    .line1("Tann Main")
                                    .line2("")
                                    .line3("")
                                    .line4("")
                                    .countryCode("gb")
                                    .postalCode("TANN HG3")
                                    .addressType("BUSINESS")
                                    .build())
                            .email("email@whitbread.com")
                            .firstName("Samuel")
                            .lastName("Whitbread")
                            .telephone("0777777777")
                            .title("Mr")
                            .differentBillingAddress(false)
                            .bookerIsNotGuest(true)
                            .build())
                    .card(Card.builder()
                            .cardholderName("Samuel Whitbread")
                            .cardType("cardType")
                            .cnpRequired(false)
                            .expiryMonth("01")
                            .expiryYear("24")
                            .logoUrl("https://www.premierinn.com/logo")
                            .token("4943056398164344242")
                            .type("type")
                            .build())
                    .environment("https://www.premierinn.com")
                    .subType("ECOMM")
                    .type("CARD")
                    .pibaCardPresent(true)
                    .build())
            .booking(Booking.builder()
                    .arrivalDate("2022-05-17")
                    .businessSite(BusinessSite.builder()
                            .identifier("LONHOL")
                            .location("London")
                            .name("London Holborn Premier Inn")
                            .type("HOTEL")
                            .build())
                    .channel("BB")
                    .departureDate("2022-05-20")
                    .journey("BOOKING")
                    .language("en")
                    .leadGuest(Guest.builder()
                            .name("Samuel Whitbread")
                            .previousBookings(2)
                            .registered(true)
                            .registeredSince(LocalDate.of(2022, 4, 1))
                            .build())
                    .reference("reference")
                    .rooms(of(RoomType.builder()
                            .adultsNumber(1)
                            .rate("SV344")
                            .type("DB")
                            .build()))
                    .type(type)
                    .build())
            .build();
  }

  private PaymentRequest mockPaymentRequestWithNewCardPayment(String type) {
    return PaymentRequest.builder()
            .payment(Payment.builder()
                    .amount(Amount.builder()
                            .currency("GBP")
                            .minorUnits(valueOf(1000))
                            .build())
                    .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                            .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                                    .line1("123 Main St")
                                    .line2("")
                                    .line3("")
                                    .line4("")
                                    .countryCode("gb")
                                    .postalCode("EC1N 2TD")
                                    .addressType("BUSINESS")
                                    .companyName("Company A")
                                    .build())
                            .cardBillingAddress(null)
                            .email("email@whitbread.com")
                            .firstName("Samuel")
                            .lastName("Whitbread")
                            .telephone("0777777777")
                            .title("Mr")
                            .differentBillingAddress(false)
                            .bookerIsNotGuest(true)
                            .build())
                    .card(Card.builder()
                            .cardholderName("Samuel Whitbread")
                            .cardType("cardType")
                            .cnpRequired(false)
                            .expiryMonth("01")
                            .expiryYear("24")
                            .logoUrl("https://www.premierinn.com/logo")
                            .token("4943056398164344242")
                            .type("type")
                            .build())
                    .environment("https://www.premierinn.com")
                    .subType("ECOMM")
                    .type("CARD")
                    .pibaCardPresent(true)
                    .build())
            .booking(Booking.builder()
                    .arrivalDate("2022-05-17")
                    .businessSite(BusinessSite.builder()
                            .identifier("LONHOL")
                            .location("London")
                            .name("London Holborn Premier Inn")
                            .type("HOTEL")
                            .build())
                    .channel("BB")
                    .departureDate("2022-05-20")
                    .journey("BOOKING")
                    .language("en")
                    .leadGuest(Guest.builder()
                            .name("Samuel Whitbread")
                            .previousBookings(2)
                            .registered(true)
                            .registeredSince(LocalDate.of(2022, 4, 1))
                            .build())
                    .reference("reference")
                    .rooms(of(RoomType.builder()
                            .adultsNumber(1)
                            .rate("SV344")
                            .type("DB")
                            .build()))
                    .type(type)
                    .build())
            .build();
  }

  private PaymentRequest mockPaymentRequestWithCentrallyStoredPayment(String type) {
    return PaymentRequest.builder()
            .payment(Payment.builder()
                    .amount(Amount.builder()
                            .currency("GBP")
                            .minorUnits(valueOf(1000))
                            .build())
                    .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                            .address(null)
                            .cardBillingAddress(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                                    .line1("123 Main St")
                                    .line2("")
                                    .line3("")
                                    .line4("")
                                    .countryCode("gb")
                                    .postalCode("EC1N 2TD")
                                    .addressType("BUSINESS")
                                    .companyName("Company A")
                                    .build())
                            .email("email@whitbread.com")
                            .firstName("Samuel")
                            .lastName("Whitbread")
                            .telephone("0777777777")
                            .title("Mr")
                            .differentBillingAddress(false)
                            .bookerIsNotGuest(true)
                            .build())
                    .card(Card.builder()
                            .cardholderName("Samuel Whitbread")
                            .cardType("cardType")
                            .cnpRequired(false)
                            .expiryMonth("01")
                            .expiryYear("24")
                            .logoUrl("https://www.premierinn.com/logo")
                            .token("4943056398164344242")
                            .type("type")
                            .build())
                    .environment("https://www.premierinn.com")
                    .subType("ECOMM")
                    .type("CARD")
                    .pibaCardPresent(true)
                    .build())
            .booking(Booking.builder()
                    .arrivalDate("2022-05-17")
                    .businessSite(BusinessSite.builder()
                            .identifier("LONHOL")
                            .location("London")
                            .name("London Holborn Premier Inn")
                            .type("HOTEL")
                            .build())
                    .channel("BB")
                    .departureDate("2022-05-20")
                    .journey("BOOKING")
                    .language("en")
                    .leadGuest(Guest.builder()
                            .name("Samuel Whitbread")
                            .previousBookings(2)
                            .registered(true)
                            .registeredSince(LocalDate.of(2022, 4, 1))
                            .build())
                    .reference("reference")
                    .rooms(of(RoomType.builder()
                            .adultsNumber(1)
                            .rate("SV344")
                            .type("DB")
                            .build()))
                    .type(type)
                    .build())
            .build();
  }

  private PaymentRequest mockPaypalPaymentRequest(String type) {
    return PaymentRequest.builder()
        .payment(Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(valueOf(1000))
                .build())
            .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                    .line1("120 Holborn")
                    .line2("")
                    .line3("")
                    .line4("")
                    .countryCode("gb")
                    .postalCode("EC1N 2TD")
                    .addressType("HOME")
                    .build())
                .email("email@whitbread.com")
                .firstName("Samuel")
                .lastName("Whitbread")
                .telephone("0777777777")
                .title("Mr")
                .differentBillingAddress(true)
                .bookerIsNotGuest(true)
                .build())
            .card(Card.builder()
                .cardholderName("Samuel Whitbread")
                .cardType("cardType")
                .cnpRequired(false)
                .expiryMonth("01")
                .expiryYear("24")
                .logoUrl("https://www.premierinn.com/logo")
                .token("4943056398164344242")
                .type("type")
                .build())
            .environment("https://www.premierinn.com")
            .subType("MIT")
            .type("PAYPAL")
            .build())
        .booking(Booking.builder()
            .arrivalDate("2022-05-17")
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .location("London")
                .name("London Holborn Premier Inn")
                .type("HOTEL")
                .build())
            .channel("PI")
            .departureDate("2022-05-20")
            .journey("BOOKING")
            .language("en")
            .leadGuest(Guest.builder()
                .name("Samuel Whitbread")
                .previousBookings(2)
                .registered(true)
                .registeredSince(LocalDate.of(2022, 4, 1))
                .build())
            .reference("reference")
            .rooms(of(RoomType.builder()
                .adultsNumber(1)
                .rate("SV344")
                .type("DB")
                .build()))
            .type(type)
            .build())
        .build();
  }

  private PaymentRequest mockPibaPaymentRequest(String type) {
    return PaymentRequest.builder()
        .payment(Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(valueOf(1000))
                .build())
            .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                    .line1("120 Holborn")
                    .line2("")
                    .line3("")
                    .line4("")
                    .countryCode("gb")
                    .postalCode("EC1N 2TD")
                    .addressType("HOME")
                    .build())
                .email("email@whitbread.com")
                .firstName("Samuel")
                .lastName("Whitbread")
                .telephone("0777777777")
                .title("Mr")
                .differentBillingAddress(true)
                .build())
            .card(Card.builder()
                .cardholderName("Samuel Whitbread")
                .cardType("cardType")
                .cnpRequired(false)
                .expiryMonth("01")
                .expiryYear("24")
                .logoUrl("https://www.premierinn.com/logo")
                .token("4943056398164344242")
                .type("type")
                .build())
            .businessItems(BusinessItems.builder()
                .customReferenceNumber("123456")
                .purchaseOrderNumber("98765")
                .businessAllowances(List.of(
                    BusinessAllowance.builder()
                        .allowance("carParking")
                        .isAuthorised(true)
                        .build(),
                    BusinessAllowance.builder()
                        .allowance("premierInnBreakfast")
                        .isAuthorised(true)
                        .build()))
                .build())
            .pibaCardPresent(Boolean.FALSE)
            .environment("https://www.premierinn.com")
            .subType("ECOMM")
            .type("PIBA")
            .build())
        .booking(Booking.builder()
            .arrivalDate("2022-05-17")
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .location("London")
                .name("London Holborn Premier Inn")
                .type("HOTEL")
                .build())
            .channel("PI")
            .departureDate("2022-05-20")
            .journey("BOOKING")
            .language("en")
            .leadGuest(Guest.builder()
                .name("Samuel Whitbread")
                .previousBookings(2)
                .registered(true)
                .registeredSince(LocalDate.of(2022, 4, 1))
                .build())
            .reference("reference")
            .rooms(of(RoomType.builder()
                .adultsNumber(1)
                .rate("SV344")
                .type("DB")
                .build()))
            .type(type)
            .build())
        .build();
  }

  private Basket mockCompletedBasket() {
    return Basket.builder().hotelId("TestHotelId").reference(REFERENCE)
        .status(BasketStatus.COMPLETED).build();
  }

  private Basket mockExpiredBasket() {
    return Basket.builder().status(BasketStatus.OPEN).createdAt("2021-01-01T00:00:00Z").build();
  }

  private Basket mockFreshBasket() {
    return Basket.builder()
        .hotelId("TestHotelId")
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .item(BasketItem.builder()
            .sourceId("1234567")
            .build())
        .status(BasketStatus.OPEN)
        .channel("PI").createdAt(
            Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()).build();
  }

  private Account mockAccount() {
    return Account.builder().bartId("12345").build();
  }

  private Basket mockNoRefNoHotelIdBasket() {
    return Basket.builder()
        .basketId(BASKET_REFERENCE)
        .item(BasketItem.builder()
            .sourceId("1234567")
            .build())
        .status(BasketStatus.OPEN)
        .channel("PI").createdAt(
            Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()).build();
  }

  private Basket mockTempBasket() {
    return Basket.builder()
        .hotelId("TestHotelId")
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .item(BasketItem.builder()
            .sourceId("1234567")
            .build())
        .status(BasketStatus.OPEN)
        .originalBasketId("originalBasketId")
        .channel("PI").createdAt(
            Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()).build();
  }

  private HotelPaymentInformation mockHotelPaymentInfoResponse() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(
            AcceptedCreditCard.builder().code("DL").code3CP("VS").codeOpera("VA").build(),
            AcceptedCreditCard.builder().code("EL").code3CP("VS").codeOpera("VA").build()
        ))
        .address(uk.co.whitbread.basket.domain.model.content.out.Address.builder().country("Germany").build())
        .build();
  }

  private HotelPaymentInformation mockHotelPaymentInfoResponse_UK() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(
            AcceptedCreditCard.builder().code("DL").code3CP("VS").codeOpera("VA").build(),
            AcceptedCreditCard.builder().code("EL").code3CP("VS").codeOpera("VA").build()
        ))
        .address(uk.co.whitbread.basket.domain.model.content.out.Address.builder().country("United Kingdom").build())
        .build();
  }

  @Test
  void initiatePayment__POA_Moto__Success() {
    var basket = mockFreshBasket();
    var businessItems = createBusinessItemsDistr();
    //Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), anyBoolean())).thenReturn(mockReservationsDetails());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);

    //Act
    var paymentRequest = mockPaymentRequestMoto();
    paymentRequest.getPayment().setPibaCardPresent(Boolean.FALSE);
    var paymentResponse =
        underTest.initiatePaymentProcess(BASKET_REFERENCE, paymentRequest);

    //Assert
    assertThat(paymentResponse, notNullValue());
    assertThat(paymentResponse.getStatus(), is(PaymentStatus.NOT_REQUIRED));
    assertThat(paymentResponse.getPaymentRequiredDetails(), nullValue());
    verify(basketOutPort, times(1)).updateBasketStatus(BASKET_REFERENCE, BasketStatus.PROCESSING,
        Optional.empty());
    verify(basketOrderOutPort, times(1)).processOrder(any(), any(), any(), any());
    verify(reservationOutPort, times(1)).updateBusinessItems(businessItems,
        List.of("1234567"), "TestHotelId", null, "DISTR");
    verify(basketOutPort, times(1)).updateBasket(basket);
    verifyNoMoreInteractions(contentOutPort, basketOrderOutPort, paymentOutPort);
  }

  @Test
  void initiatePayment_isSecureBooking() {
    //Arrange
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(mockCompletedBasket());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(reservationOutPort.getReservationsByBasketReference(any(), any(), anyBoolean(), anyBoolean()))
        .thenReturn(mockReservationsDetails());
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(true);
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));

    //Act
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name(), false);
    mockPaymentRequest.setSecureBooking(true);
    var result = underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest);

    //Assert
    assertNotNull(result);
    assertEquals(mockPaymentRequest.getPayment().getSubType(),
        SubTypeEnum.SECURE_BOOKING.getValue());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void initiatePaypalPayment_updatesBasketIfSecureBookingEnabled(boolean isSecureBooking) {
    var basket = mockFreshBasket();
    basket.setIsSecureBooking(isSecureBooking);

    // Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(
        reservationOutPort.getReservationsByBasketReference(anyString(), anyString(), anyBoolean(), anyBoolean()))
        .thenReturn(mockReservationsDetails());
    when(paymentOutPort.createPayment(any()))
        .thenReturn(mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE));
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInfoResponse());

    mockSecureBookingFeatureFlag(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);

    // Act
    var request = mockPaypalPaymentRequest(PAY_NOW.name());
    request.setSecureBooking(isSecureBooking);
    var paymentResponse = underTest.initiatePaypalPaymentProcess(BASKET_REFERENCE, request);

    // Assert
    assertThat(paymentResponse, notNullValue());
    if (isSecureBooking) {
      verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
    }

    verifyNoMoreInteractions(reservationOutPort);
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void paymentWebhook_shouldSetBasketStatusFailedAndSkipValidityCheck_whenSecureBookingFails(
      boolean isSecureBooking) {
    // Arrange
    final var hotelId = "ABC";
    final var userId = "userId";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSaveSecureBooking()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(isSecureBooking);
    final var basket = Basket.builder()
        .hotelId(hotelId)
        .status(BasketStatus.COMPLETED)
        .userId(userId)
        .reference(REFERENCE)
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PAY_ON_ARRIVAL.name())
        .channel("PI")
        .isSecureBooking(true)
        .build();

    final var paymentsConfirmation = mockPaymentsConfirmation();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    final var paymentResponse = mockPaymentResponse(PAYMENT_ID, BASKET_REFERENCE, REFERENCE);
    paymentResponse.setPaymentStatus("FAILURE");
    when(paymentResponseWebhookMapper.toPaymentsResponseModel(any()))
        .thenReturn(paymentResponse);

    // Act
    PaymentResponse response = underTest.paymentWebhook(BASKET_REFERENCE, paymentsConfirmation);

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getBooking(), notNullValue());
    assertThat(response.getBooking().getReference(), is(BASKET_REFERENCE));
    assertThat(response.getBookingReference(), is(REFERENCE));
    assertThat(response.getPaymentId(), is(PAYMENT_ID));
    if (isSecureBooking) {
      assertEquals(BasketStatus.COMPLETED,basket.getStatus());
    }
  }

  @Test
  void initiatePayment_validate_unique_promo_success() {
    //Arrange
    var basket = mockFreshBasket();
    basket.setPromotionCode("TEST");
    basket.setPromoKind(
        uk.co.whitbread.basket.domain.model.basket.out.PromoKind.UNIQUE);

    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(reservationOutPort.getReservationsByBasketReference(any(), any(), anyBoolean(),
        anyBoolean())).thenReturn(mockReservationsDetails());
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(false);
    when(paymentOutPort.createPayment(any())).thenReturn((mockPaymentResponse()));

    //Act
    var mockPaymentRequest = mockPaymentRequest(PAY_NOW.name(), false);
    var result = underTest.initiatePaymentProcess(BASKET_REFERENCE, mockPaymentRequest);

    //Assert
    assertNotNull(result);
  }


  private PaymentRequest mockPaymentRequestMoto() {
    return PaymentRequest.builder()
        .payment(Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(valueOf(1000))
                .build())
            .billing(uk.co.whitbread.basket.domain.model.payments.in.Billing.builder()
                .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                    .line1("120 Holborn")
                    .line2("")
                    .line3("")
                    .line4("")
                    .countryCode("gb")
                    .postalCode("EC1N 2TD")
                    .build())
                .email("email@whitbread.com")
                .firstName("Samuel")
                .lastName("Whitbread")
                .telephone("0777777777")
                .title("Mr")
                .build())
            .card(Card.builder()
                .cardholderName("Samuel Whitbread")
                .cardType("VS")
                .cnpRequired(false)
                .expiryMonth("01")
                .expiryYear("24")
                .logoUrl("https://www.premierinn.com/logo")
                .token("4943056398164344242")
                .type("type")
                .last4Digits("4242")
                .build())
            .environment("https://www.premierinn.com")
            .subType("MOTO")
            .type("CARD")
            .pibaCardPresent(true)
            .build())
        .booking(Booking.builder()
            .arrivalDate("2022-05-17")
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .location("London")
                .name("London Holborn Premier Inn")
                .type("HOTEL")
                .build())
            .channel("DISTR")
            .departureDate("2022-05-20")
            .journey("BOOKING")
            .language("en")
            .leadGuest(Guest.builder()
                .name("Samuel Whitbread")
                .previousBookings(2)
                .registered(true)
                .registeredSince(LocalDate.of(2022, 4, 1))
                .build())
            .reference("reference")
            .rooms(of(RoomType.builder()
                .adultsNumber(1)
                .rate("SV344")
                .type("DB")
                .build()))
            .type("PAY_ON_ARRIVAL")
            .build())
        .businessAccount(
            BusinessAccount.builder().customerReference("123456").purchaseOrder("7891011")
                .carParkingAllowed("Yes").build())
        .build();
  }

  private BusinessItems createBusinessItemsDistr() {
    return BusinessItems.builder()
        .purchaseOrderNumber("7891011")
        .customReferenceNumber("123456")
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("carParking")
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("BFADBF")
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("accommodation")
                .isAuthorised(true)
                .build()))
        .businessNotes(
            "Please charge the Credit card used to secure the reservation for all booked items which are included in the final total rate.\n"
                +
                "The following Allowances are to be charged to the Credit card used to secure the reservation if authorised:\n"
                + "Premier Inn Breakfast is Pre-Booked and Authorised.\n"
                + "Dinner Allowance is NOT Authorised.\n"
                + "Alcohol is NOT Authorised with the evening meal.\n"
                + "Car Parking is Authorised.\n"
                + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.")
        .build();
  }

  private BusinessItems createBusinessItemsPI(String packageCode) {
    return BusinessItems.builder()
        .purchaseOrderNumber("98765")
        .customReferenceNumber("123456")
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("carParking")
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance(packageCode)
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("accommodation")
                .isAuthorised(true)
                .build()))
        .businessNotes(mockBusinessNotes(packageCode))
        .build();
  }

  private String mockBusinessNotes(String packageCode) {
    var businessNotes = new StringBuilder(
        "Please charge the PIBA card used to secure the reservation for all booked items which are "
            + "included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if "
            + "authorised:\n");
    switch (packageCode) {
      case "BFADBF" -> businessNotes
          .append("Premier Inn Breakfast is Pre-Booked and Authorised.\n");
      case "BFADCT" -> businessNotes
          .append("Continental Breakfast is Pre-Booked and Authorised.\n");
      default -> businessNotes.append(" ");
    }
    businessNotes.append("Dinner Allowance is NOT Authorised.\n"
        + "Alcohol is NOT Authorised with the evening meal.\n"
        + "Car Parking is Authorised.\n"
        + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.");
    return businessNotes.toString();
  }

  private BusinessNotesResponse mockBusinessNotes() {
    return BusinessNotesResponse.builder()
        .headers(List.of(
            Note.builder().id("authorizedCharges").value(
                "Please charge the {cardType} card used to secure the reservation for all booked items which are "
                    + "included in the final total rate.").build())
        )
        .allowances(List.of(
            Note.builder().id("authorizedAllowances").value(
                "The following Allowances are to be charged to the {cardType} card used to secure the reservation "
                    + "if authorised:").build())
        )
        .businessNotes(List.of(
            BusinessNote.builder().id("carParking").lang("en").allow("Car Parking is Authorised.")
                .deny("Car Parking is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("ultimateWifi").lang("en")
                .allow("Wi-Fi Access is authorised.")
                .deny("WiFi is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("dinner").lang("en")
                .allow("{price} Dinner Allowance is Authorised.")
                .deny("Dinner Allowance is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("alcohol").lang("en")
                .allow("Alcohol is Authorised with the evening meal.")
                .deny("Alcohol is NOT Authorised with the evening meal.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang("en").allow("Other charges "
                + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast")
                .value("Premier Inn Breakfast is Pre-Booked and Authorised.")
                .build(),
            Note.builder().id("continentalBreakfast")
                .value("Continental Breakfast is Pre-Booked and Authorised.")
                .build())
        )
        .cardTypes(List.of(
            Note.builder().id("card").value("Credit").build(),
            Note.builder().id("piba").value("PIBA").build()
            )
        )
        .footers(
            List.of(
                Note.builder().id("cnpAuthorization").value(
                    "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.")
                    .build()
            )
        )
        .build();
  }

  private BusinessAllowanceRuleResponse mockBusinessAllowanceRules() {
    return BusinessAllowanceRuleResponse.builder().businessAllowances(
            List.of(
                BusinessAllowanceRule.builder().sourceId("BFADBF").aemId("premierInnBreakfast").sourceType("PACKAGE")
                    .pms("PMS").targetId("155").isTransactionCode(true).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("BFADCT").aemId("continentalBreakfast").sourceType("PACKAGE")
                    .pms("PMS").targetId("11").isTransactionCode(true).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("CITYTAX").aemId("cityTax").sourceType("PACKAGE")
                    .pms("PMS").targetId("CITY").isTransactionCode(false).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("dinner").aemId("dinner").sourceType("ALLOWANCE")
                    .pms("PMS").targetId("FBNA").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("alcohol").aemId("alcohol").sourceType("ALLOWANCE")
                    .pms("PMS").targetId("FB").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("carParking").aemId("carParking").sourceType("ALLOWANCE")
                    .pms("PMS").targetId("PARK").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("otherCharges").aemId("otherCharges").sourceType("ALLOWANCE")
                    .pms("PMS").targetId("PARK").isTransactionCode(false).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("ultimateWifi").aemId("ultimateWifi").sourceType("ALLOWANCE")
                    .pms("PMS").targetId("WIFI").isTransactionCode(false).isNotesMandatory(false).build())
        )
        .build();
  }

  private PaymentsConfirmation mockPaymentsConfirmation() {
    return PaymentsConfirmation.builder().paymentId("12234")
        .paymentStatus("SUCCES").bookingReference("GAL123456").reference("GAL123456")
        .countryCode("GB")
        .channel("PI").language("EN").build();
  }

  private void mockCheckInOnlineFeatureFlag(boolean isOn) {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(isOn);
  }

  private void mockSecureBookingFeatureFlag(boolean isSecured) {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getSaveSecureBooking()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveSecureBooking())).thenReturn(isSecured);
  }

  private Jwt getJwt(){
    return new Jwt("authorization", Instant.now(), Instant.now().plusSeconds(60),
            Map.of("header1", "header2"), Map.of("Claim1", "Claim2"));
  }

  private void mockInstantNow() {
    mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);
    var clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    var mockedInstant = Instant.now(clock);
    mockedStatic.when(Instant::now).thenReturn(mockedInstant);
    setMockInstant = true;
  }
}
