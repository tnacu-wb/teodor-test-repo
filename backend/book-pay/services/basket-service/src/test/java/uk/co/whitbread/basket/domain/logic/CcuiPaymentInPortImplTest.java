package uk.co.whitbread.basket.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBasketBookingAllowances;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PAY_PENDING;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.NEW_CARD;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.NEW_PIBA;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.PAY_NOW;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiOption.RESERVE_WITHOUT_CARD;
import static uk.co.whitbread.basket.domain.model.payments.out.ThreecPaymentStatus.SUCCESS;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.CompanyIdNotFoundException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentBusinessException;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.logic.config.ThreecProp;
import uk.co.whitbread.basket.domain.logic.mapper.CcuiPaymentDomainMapperImpl;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.business.in.BusinessAllowance;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AccountCompanyItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AddressCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.BillingCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CardCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiRequest;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNote;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.content.out.Note;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.EckohResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationPackagesDetails;
import uk.co.whitbread.basket.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOhipOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiPaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.generated.models.ohip.NegotiatedRatesResponseDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class CcuiPaymentInPortImplTest {

  private static final String RESERVATION_ID = "123456";
  private static final String BASKET_REFERENCE = "NEW-e2be3bd8-f962-44a0-a1c1-043053626a73";
  private static final String BOOKING_REFERENCE = "NEW2345678";
  private static final String PAYMENT_ID = "12345D";
  private static final String HOTEL_ID = "NEWDRO";
  public static final String PAYMENT_TYPE_CARD = "CARD";
  public static final String PAYMENT_TYPE_PIBA = "PIBA";
  public static final String SUBPAYMENT_TYPE_PIBAGB = "PIBAGB";
  private static final long CLEAN_UP_TIME_LONG = 5000;
  private final Long basketValidity = 1680000L;
  private BasketOrderOutPort basketOrderOutPort;
  private BasketOutPort basketOutPort;
  private HotelReservationOutPort reservationOutPort;
  private PaymentOutPort paymentOutPort;
  private CcuiPaymentOutPort ccuiPaymentOutPort;
  private ContentOutPort contentOutPort;
  private EmailNotificationService emailNotificationService;
  private RefundOutPort refundOutPort;
  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);

  private RulesAgentOutPort rulesAgentOutPort;
  private BasketOhipOutPort basketOhipOutPort;
  private CcuiPaymentInPortImpl ccuiPaymentInPort;
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  private AuthenticatedUserService authenticatedUserService;
  private CleanUpTime cleanUpTime;
  private ThreecProp threecProperties;

  @BeforeEach
  public void setup() {
    basketOrderOutPort = Mockito.mock(BasketOrderOutPort.class);
    basketOutPort = Mockito.mock(BasketOutPort.class);
    reservationOutPort = Mockito.mock(HotelReservationOutPort.class);
    paymentOutPort = Mockito.mock(PaymentOutPort.class);
    ccuiPaymentOutPort = Mockito.mock(CcuiPaymentOutPort.class);
    contentOutPort = Mockito.mock(ContentOutPort.class);
    emailNotificationService = Mockito.mock(EmailNotificationService.class);
    refundOutPort = Mockito.mock(RefundOutPort.class);
    rulesAgentOutPort = Mockito.mock(RulesAgentOutPort.class);
    threecProperties= Mockito.mock(ThreecProp.class);
    basketOhipOutPort = Mockito.mock(BasketOhipOutPort.class);
    unleashWrapper = Mockito.mock(UnleashWrapper.class);
    authenticatedUserService = Mockito.mock(AuthenticatedUserService.class);
    cleanUpTime = Mockito.mock(CleanUpTime.class);

    ccuiPaymentInPort =
        new CcuiPaymentInPortImpl(basketValidity, basketOrderOutPort, reservationOutPort,
            paymentOutPort,
            basketOutPort, ccuiPaymentOutPort, contentOutPort, emailNotificationService,
            refundOutPort, rulesAgentOutPort, threecProperties, basketOhipOutPort,
            new CcuiPaymentDomainMapperImpl(), concurrentTracer,
            unleashWrapper, authenticatedUserService, cleanUpTime);
  }

  @Test
  void initiateCcuiPayment_PayNow_shouldReturnOK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(paymentOutPort.updateToken(any())).thenReturn(null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_PayNow_shouldReturnOK_UK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(paymentOutPort.updateToken(any())).thenReturn(null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponseUK());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_PayNow_BlankBillingAddress() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequestBlankCardAddress(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(paymentOutPort.updateToken(any())).thenReturn(null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequestBlankCardAddress(NEW_CARD.name(), PAY_NOW.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_PayOnArrival_shouldReturnOK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPayment3cp(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_NON_shouldReturnOK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    doNothing().when(basketOrderOutPort)
        .processOrder(basket, BasketRequestAction.COMMIT.getReqAction(), null, null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(RESERVE_WITHOUT_CARD.name(), RESERVE_WITHOUT_CARD.name(),
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(1000), null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_NON_shouldReturnOK_UK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    doNothing().when(basketOrderOutPort)
        .processOrder(basket, BasketRequestAction.COMMIT.getReqAction(), null, null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponseUK());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(RESERVE_WITHOUT_CARD.name(), RESERVE_WITHOUT_CARD.name(),
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(1000), null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_NON_BlankBillingAddress() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    doNothing().when(basketOrderOutPort)
        .processOrder(basket, BasketRequestAction.COMMIT.getReqAction(), null, null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequestBlankCardAddress(RESERVE_WITHOUT_CARD.name(), RESERVE_WITHOUT_CARD.name(),
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(1000), null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_RWC_CNP() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    doNothing().when(basketOrderOutPort)
            .processOrder(basket, BasketRequestAction.COMMIT.getReqAction(), null, null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var ccuiPaymentRequest = createCcuiPaymentRequest(RESERVE_WITHOUT_CARD.name(), RESERVE_WITHOUT_CARD.name(),
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(1000), null);
    ccuiPaymentRequest.getCcuiExtraItems().setCardPresent(false);
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE, ccuiPaymentRequest);
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @ParameterizedTest
  @CsvSource({
      "false, 1",
      "true, 2"
  })
  void initiateCcuiPayment_amend_a2c(Boolean saveAllowancesFlagValue, int basketInvocationTimes) {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    basket.setOriginalBasketId("originalBasketId");

    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    doNothing().when(basketOhipOutPort).updateCustomReferenceNumber(any());
    doNothing().when(basketOrderOutPort)
        .processAmend(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveAllowancesFlagValue);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
        .thenReturn(false);
    //Act

    var ccuiPaymentRequest = createAmendCcuiPaymentRequest("ACCOUNT_COMPANY");
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        ccuiPaymentRequest);

    var bookingAllowancesBothAuthorized = createAmendCcuiPaymentRequest("ACCOUNT_COMPANY")
        .getCcuiExtraItems().getBusinessItems().getBusinessAllowances();
    var bookingAllowancesOnlyOneAuthorized = createAmendCcuiPaymentRequest("ACCOUNT_COMPANY")
        .getCcuiExtraItems().getBusinessItems().getBusinessAllowances();
    bookingAllowancesOnlyOneAuthorized.get(0).setIsAuthorised(false);
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    assertThat(response.getStatus(), is(AMENDING));
    verify(basketOutPort, times(basketInvocationTimes)).updateBasket(basket);
    assertThat(buildBasketBookingAllowances(bookingAllowancesBothAuthorized).size(), is(2));
    assertThat(buildBasketBookingAllowances(bookingAllowancesOnlyOneAuthorized).size(), is(1));

    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_amend_a2c_UnimplementedPaymentMethod() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    basket.setOriginalBasketId("originalBasketId");
    //Act

    var ccuiPaymentRequest = createAmendCcuiPaymentRequest("INVALID");
    Exception exception = assertThrows(Exception.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );
    //Assert

    assertThat(exception, notNullValue());
  }

  @Test
  void initiateCcuiPayment_ATC_shouldReturnOK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    doNothing().when(reservationOutPort).attachProfileToReservations(anyString(), anyString(), any());
    doNothing().when(basketOrderOutPort)
        .processOrder(basket, BasketRequestAction.COMMIT.getReqAction(), null, null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    when(basketOhipOutPort.getNegotiatedRates(any())).thenReturn(mock(NegotiatedRatesResponseDto.class));

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(ACCOUNT_COMPANY.name(), ACCOUNT_COMPANY.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_ATC_UKHotel() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    doNothing().when(reservationOutPort).attachProfileToReservations(anyString(), anyString(), any());
    doNothing().when(basketOrderOutPort)
        .processOrder(basket, BasketRequestAction.COMMIT.getReqAction(), null, null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponseUK());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(ACCOUNT_COMPANY.name(), ACCOUNT_COMPANY.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_emptyPaymentId_ShouldThrowException() {
    //Arrange
    final var exceptedMessage = "Error while validate Eckoh the paymentId is null for basketReference=NEW2345678.The Eckoh Iframe has not been launched!";
    final var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, null, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    final var ccuiPaymentRequest = createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
        BigDecimal.valueOf(1000), null, null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(basketOutPort.updateBasket(any())).thenReturn(null);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);


    //Act
    Exception exception = assertThrows(PaymentException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(basketOutPort);
  }

  @Test
  void initiateCcuiPayment_emptyHotelId_ShouldThrowException() {
    //Arrange
    final var exceptedMessage =
        "Error while validating basket, basketReference or hotelId are empty, " + BOOKING_REFERENCE;
    final var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, null,
        RESERVATION_ID, BasketStatus.OPEN);
    final var ccuiPaymentRequest = createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
        BigDecimal.valueOf(1000), null, null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);

    //Act
    Exception exception = assertThrows(PaymentException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE, ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(basketOutPort);
  }

  @Test
  void initiateCcuiPayment_emptyReference_ShouldThrowException() {
    //Arrange
    final var exceptedMessage =
        "Error while validating basket, basketReference or hotelId are empty, null";
    final var basket = createBasket(null, null, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    final var ccuiPaymentRequest = createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
        BigDecimal.valueOf(10), null, null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);

    //Act
    Exception exception = assertThrows(PaymentException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(basketOutPort);
  }

  @Test
  void initiateCcuiPayment_basketCompleted_ShouldThrowException() {
    //Arrange
    final var exceptedMessage = "Error while validating basket the status is COMPLETED for basketReference=" + BASKET_REFERENCE;
    final var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, COMPLETED);
    final var ccuiPaymentRequest = createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
        BigDecimal.valueOf(1000), null, null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    //Act
    Exception exception = assertThrows(BasketReferenceNotValidException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(basketOutPort);
  }

  @Test
  void initiateCcuiPayment_ShouldThrowException() {
    //Arrange
    var message = String.format("Basket is no longer valid: %s", BASKET_REFERENCE);
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_PIBA))).thenReturn(
        createPayment3cp(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_PIBA));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_PIBA))).thenReturn(
        createPaymentResponse());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    var request = createCcuiPaymentRequestWithCcuiExtraItems(NEW_PIBA.name(), PAY_ON_ARRIVAL.name(),
        BigDecimal.valueOf(1000), null, SUBPAYMENT_TYPE_PIBAGB, true);
    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    //Act
    CcuiPaymentInPortImpl port =
        new CcuiPaymentInPortImpl(-1L, basketOrderOutPort, reservationOutPort,
            paymentOutPort,
            basketOutPort, ccuiPaymentOutPort, contentOutPort, emailNotificationService,
            refundOutPort, rulesAgentOutPort,  threecProperties, basketOhipOutPort,
            new CcuiPaymentDomainMapperImpl(), concurrentTracer,
            unleashWrapper, authenticatedUserService, cleanUpTime);
    // Assert
    var exception = assertThrows(BasketReferenceNotValidException.class,
        () -> port.initiateCcuiPaymentProcess(BASKET_REFERENCE, request));

    // Assert
    verify(basketOutPort, times(3)).updateBasket(captor.capture());
    // Get the list of captured arguments
    List<Basket> capturedBaskets = captor.getAllValues();
    final var basketEntityResult = capturedBaskets.get(capturedBaskets.size() - 1);
    assertEquals(CLEAN_UP_TIME_LONG, basketEntityResult.getCleanUpTime());
    assertEquals(BasketStatus.FAILED.name(), basketEntityResult.getStatus().toString());
    assertEquals(ErrorCode.DIGITAL_CCUI_INVALID_BASKET_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_CCUI_INVALID_BASKET_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void initiateCcuiPayment_wrongPaymentType_ShouldThrowException() {
    //Arrange
    final var exceptedMessage =
        "Error while processing payment for CCUI with basketReference=" + BOOKING_REFERENCE
            + ".The payment option WRONG is not valid for basket " + BOOKING_REFERENCE;
    final var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    final var ccuiPaymentRequest =
        createCcuiPaymentRequest("WRONG", "WRONG", BigDecimal.valueOf(1000),
            null, null);
    when(basketOutPort.updateBasket(any())).thenReturn(null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);

    //Act
    Exception exception = assertThrows(PaymentException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
  }

  @Test
  void initiateCcuiPayment_emptyCost_ShouldThrowException() {
    //Arrange
    final var exceptedMessage = "Total reservation costs missing for basket " + BOOKING_REFERENCE;
    final var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    final var ccuiPaymentRequest = createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
        BigDecimal.valueOf(1000), null, null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(null, null));
    when(basketOutPort.updateBasket(any())).thenReturn(null);
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD));
    when(ccuiPaymentOutPort.getPaymentConfirmation(basket.getPaymentID())).thenReturn(
        mockEckohPaymentResponse());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);

    //Act
    Exception exception = assertThrows(PaymentException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
  }

  @Test
  void initiateCcuiPaymentProcess_withPIBAAsPaymentOption() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false))
        .thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_PIBA))).thenReturn(
        createPayment3cp(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_PIBA));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_PIBA))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequestWithCcuiExtraItems(NEW_PIBA.name(), PAY_ON_ARRIVAL.name(),
            BigDecimal.valueOf(1000), null, SUBPAYMENT_TYPE_PIBAGB, true));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));

    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPaymentProcess_withPIBAAndCNPAsPaymentOption() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    var reservations = createReservation(BigDecimal.valueOf(1000), null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(reservations);
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_PIBA))).thenReturn(
        createPayment3cp(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_PIBA));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_PIBA))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(contentOutPort.getBusinessNotes(any())).thenReturn(mockBusinessNotesResponse());
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequestWithCcuiExtraItems(NEW_PIBA.name(), PAY_ON_ARRIVAL.name(),
            BigDecimal.valueOf(1000), null, SUBPAYMENT_TYPE_PIBAGB, false));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verify(reservationOutPort).updateBusinessItems(createBusinessItemsWithPackagesAndAccomodation("BFADBF"),
        List.of(reservations.getReservationByIdList().get(0).getReservationId()), HOTEL_ID, null, "CCUI");
    verify(reservationOutPort).updateBusinessItems(createBusinessItemsWithPackagesAndAccomodation("BFADCT"),
        List.of(reservations.getReservationByIdList().get(1).getReservationId()), HOTEL_ID, null, "CCUI");
    verify(basketOutPort, times(3)).updateBasket(basket);

    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void updateDiscount_success() {
    // Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    var discountRequest = createDiscountRequest();
    when(reservationOutPort.getReservationsByBasketReference(BASKET_REFERENCE,
        "false", false)).thenReturn(
        createReservation(null, null));

    // Act
    ccuiPaymentInPort.updateDiscount(discountRequest);

    // Assert
    verify(reservationOutPort, times(1)).getReservationsByBasketReference(BASKET_REFERENCE,
        "false", false);
    verify(reservationOutPort, times(1)).updateDiscount(discountRequest, List.of(RESERVATION_ID), HOTEL_ID, "GB");
    verifyNoMoreInteractions(basketOutPort);
  }

  @ParameterizedTest
  @MethodSource(value = "ccuiPaymentRequestProvider")
  void initiateCcuiPayment_ccuiExtraItemsAndNestedObjectsValidation_ShouldThrowException(
      CcuiPaymentRequest ccuiPaymentRequest) {
    //Arrange
    final var exceptedMessage = "Company id not found for payment option: " + ACCOUNT_COMPANY;
    final var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
        RESERVATION_ID, BasketStatus.OPEN);

    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);

    //Act
    Exception exception = assertThrows(CompanyIdNotFoundException.class, () ->
        ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            ccuiPaymentRequest)
    );

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(exceptedMessage));
    verifyNoMoreInteractions(reservationOutPort);
  }

  @Test
  void initiateCcuiPaymentProcess_changePaymentFlow_A2C() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, null, HOTEL_ID,
        RESERVATION_ID, COMPLETED);
    basket.setPaymentOption("RESERVE_WITHOUT_CARD");
    var reservations = createReservation(BigDecimal.valueOf(1000), null);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(reservations);
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(ACCOUNT_COMPANY.name(), ACCOUNT_COMPANY.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verify(basketOutPort, times(2)).updateBasket(basket);

    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPaymentProcess_changePaymentFlow_CC() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, null, HOTEL_ID,
        RESERVATION_ID, PAY_PENDING);
    basket.setPaymentOption("RESERVE_WITHOUT_CARD");
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(BigDecimal.valueOf(1000), null));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
        createPaymentResponseForTest());
    basket.setPaymentID(PAYMENT_ID);
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPayment3cp(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD));
    when(paymentOutPort.createPayment(createPayment3cp(PAY_ON_ARRIVAL.name(),
        createReservation(BigDecimal.valueOf(100000), null), PAYMENT_TYPE_CARD))).thenReturn(
        createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(basketOutPort.updateBasket(basket)).thenReturn(basket);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog())
            .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog()))
            .thenReturn(true);
    //Act

    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
        createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
            BigDecimal.valueOf(1000), null, null));
    //Assert

    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(reservationOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_Window3_shouldReturnOK() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
            RESERVATION_ID, BasketStatus.OPEN);
    var paymentRequest = createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD);
    paymentRequest.getPayment().getBilling().setBookerIsNotGuest(true);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
            basket.getBasketId(),
            "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
            createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
            paymentRequest);
    when(paymentOutPort.createPayment(paymentRequest)).thenReturn(
            createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(paymentOutPort.updateToken(any())).thenReturn(null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(mock(Feature.class));
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree()).thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree())).thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
                    BigDecimal.valueOf(1000), null, null));

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(paymentOutPort);
    verifyNoMoreInteractions(basketOrderOutPort);
  }

  @Test
  void initiateCcuiPayment_Window3_notPayNow() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
            RESERVATION_ID, BasketStatus.OPEN);
    var paymentRequest = createPayment3cp(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD);
    paymentRequest.getPayment().getBilling().setBookerIsNotGuest(true);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
            basket.getBasketId(),
            "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
            createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_ON_ARRIVAL.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
            paymentRequest);
    when(paymentOutPort.createPayment(paymentRequest)).thenReturn(
            createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            createCcuiPaymentRequest(NEW_CARD.name(), PAY_ON_ARRIVAL.name(),
                    BigDecimal.valueOf(1000), null, null));

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(reservationOutPort);
  }

  @Test
  void initiateCcuiPayment_Window3_featureFlagOff() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
            RESERVATION_ID, BasketStatus.OPEN);
    var paymentRequest = createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD);
    paymentRequest.getPayment().getBilling().setBookerIsNotGuest(true);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
            basket.getBasketId(),
            "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
            createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
            paymentRequest);
    when(paymentOutPort.createPayment(paymentRequest)).thenReturn(
            createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(paymentOutPort.updateToken(any())).thenReturn(null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            mockHotelPaymentInfoResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(mock(Feature.class));
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree()).thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree())).thenReturn(false);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
                    BigDecimal.valueOf(1000), null, null));

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(reservationOutPort);
  }

  @Test
  void initiateCcuiPayment_Window3_notGermanyHotel() {
    //Arrange
    var basket = createBasket(BASKET_REFERENCE, BOOKING_REFERENCE, PAYMENT_ID, HOTEL_ID,
            RESERVATION_ID, BasketStatus.OPEN);
    var paymentRequest = createPayment3cp(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD);
    var hotelPaymentInformation = mockHotelPaymentInfoResponse();
    hotelPaymentInformation.getAddress().setCountry("test");
    paymentRequest.getPayment().getBilling().setBookerIsNotGuest(true);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(
            basket.getBasketId(),
            "false", true, false))
        .thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(any())).thenReturn(
            createPaymentResponseForTest());
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
            createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
            paymentRequest);
    when(paymentOutPort.createPayment(paymentRequest)).thenReturn(
            createPaymentResponse());
    doNothing().when(basketOrderOutPort).processOrder(any(), any(), any(), any());
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    when(paymentOutPort.updateToken(any())).thenReturn(null);
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
            hotelPaymentInformation);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(mock(Feature.class));
    when(mockedFeatureFlag.getSavePaymentInstructionFolioThree()).thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree())).thenReturn(true);

    //Act
    var response = ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE,
            createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
                    BigDecimal.valueOf(1000), null, null));

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getReference(), is(basket.getBasketId()));
    assertThat(response.getBookingReference(), is(basket.getReference()));
    verifyNoMoreInteractions(reservationOutPort);
  }

  @ParameterizedTest
  @CsvSource({
      "123, 123, PAYMENT_CONTACT_BANK_EXCEPTION_VALUE, FAILURE, true",
      "456, 456, PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE, NO_PAYMENT_ATTEMPT, true",
      "789, 789, PAYMENT_TRY_AGAIN_EXCEPTION_VALUE, FAILURE, true",
      "123, 'A failure has occurred from 3CP for paymentID 12345D, provider reason: null', internal.server.exception, FAILURE, false",
  })
  void testInitiateCcuiPayment_paymentFailure_returnCode(String returnCode,
      String expectedErrorCode, String expectedErrorDescription, String paymentStatus,
      Boolean mockedFeatureThreecpReturnCodesMappingFf) {
    // Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    var mockedFeatureThreecpReturnCodesMapping = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(mock(Feature.class));
    when(mockedFeatureFlag.getThreecpReturnCodesMapping()).thenReturn(
        mockedFeatureThreecpReturnCodesMapping);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getThreecpReturnCodesMapping())).thenReturn(
        mockedFeatureThreecpReturnCodesMappingFf);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(true);
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        mockHotelPaymentInfoResponse());
    var paymentRequest = createPayment3cp(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD);
    when(ccuiPaymentOutPort.createPaymentRequest(createPaymentRequest(PAY_NOW.name(),
        createReservation(BigDecimal.valueOf(1000), null), PAYMENT_TYPE_CARD))).thenReturn(
        paymentRequest);

    final var ccuiPaymentRequest = createCcuiPaymentRequest(NEW_CARD.name(), PAY_NOW.name(),
        BigDecimal.valueOf(1000), null, null);

    final var basket = Basket.builder()
        .hotelId("ABC")
        .status(BasketStatus.PAY_PENDING)
        .userId("userId")
        .reference("ABC123456")
        .basketId(BASKET_REFERENCE)
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .sendMail(true).paymentID(PAYMENT_ID).paymentOption(PaymentOption.PAY_ON_ARRIVAL.name())
        .channel("PI").build();

    when(reservationOutPort.getReservationsByBasketReference(
        basket.getBasketId(),
        "false", true, false)).thenReturn(createReservation(null, BigDecimal.valueOf(1000)));
    when(ccuiPaymentOutPort.getPaymentConfirmation(basket.getPaymentID())).thenReturn(
        mockEckohPaymentResponse());
    when(paymentOutPort.createPayment(paymentRequest)).thenReturn(
        createPaymentResponse());

    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    Map<String, Set<Integer>> returnCodes = new HashMap<>();
    returnCodes.put("contactBankCodes", Set.of(123));
    returnCodes.put("incorrectCardDetailsCodes", Set.of(456));
    returnCodes.put("tryAgainCodes", Set.of(789));

    if (mockedFeatureThreecpReturnCodesMappingFf) {
      when(threecProperties.getReturnCodes()).thenReturn(returnCodes);
    }

    var paymentResponse = createPaymentResponse();
    paymentResponse.getProviderResponse().getThreecResponse().setProviderResult(returnCode);
    paymentResponse.setPaymentStatus(paymentStatus);

    when(paymentOutPort.createPayment(paymentRequest)).thenReturn(paymentResponse);
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    // Act - expect PaymentBusinessException for return codes 123, 456, 789
    // and PaymentException for other cases
    if (mockedFeatureThreecpReturnCodesMappingFf) {
      PaymentBusinessException thrownException = assertThrows(PaymentBusinessException.class,
          () -> ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE, ccuiPaymentRequest));

      // Assert
      assertEquals(expectedErrorCode, thrownException.getDebugMessage());
      assertEquals(expectedErrorDescription, thrownException.getGlobalErrTextTemplate());
    } else {
      PaymentException thrownException = assertThrows(PaymentException.class,
          () -> ccuiPaymentInPort.initiateCcuiPaymentProcess(BASKET_REFERENCE, ccuiPaymentRequest));

      // Assert
      assertEquals(expectedErrorCode, thrownException.getDebugMessage());
      assertEquals(expectedErrorDescription, thrownException.getGlobalErrTextTemplate());
    }
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  private static Stream<Arguments> ccuiPaymentRequestProvider() {
    // Test case 1: ccuiExtraItems is null
    var ccuiPaymentRequest1 = getCcuiPaymentRequest();
    ccuiPaymentRequest1.setCcuiExtraItems(null);

    // Test case 2: accountCompanyItems is null
    var ccuiPaymentRequest2 = getCcuiPaymentRequest();
    ccuiPaymentRequest2.getCcuiExtraItems().setAccountCompanyItems(null);

    // Test case 3: companyId is null
    var ccuiPaymentRequest3 = getCcuiPaymentRequest();
    ccuiPaymentRequest3.getCcuiExtraItems().getAccountCompanyItems().setCompanyId(null);

    // Test case 4: companyId is empty
    var ccuiPaymentRequest4 = getCcuiPaymentRequest();
    ccuiPaymentRequest4.getCcuiExtraItems().getAccountCompanyItems().setCompanyId("");

    return Stream.of(
        Arguments.of(ccuiPaymentRequest1),
        Arguments.of(ccuiPaymentRequest2),
        Arguments.of(ccuiPaymentRequest3),
        Arguments.of(ccuiPaymentRequest4));
  }

  private static CcuiPaymentRequest getCcuiPaymentRequest() {
    return ccuiPaymentRequestForParameterizedTest(ACCOUNT_COMPANY.name());
  }

  private BusinessNotesResponse mockBusinessNotesResponse() {
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
            BusinessNote.builder().id("ultimateWifi").lang("en").allow("Wi-Fi Access is authorised.")
                .deny("WiFi is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("dinner").lang("en").allow("{price} Dinner Allowance is Authorised.")
                .deny("Dinner Allowance is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("alcohol").lang("en").allow("Alcohol is Authorised with the evening meal.")
                .deny("Alcohol is NOT Authorised with the evening meal.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang("en").allow("Other charges "
                    + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast").value("Premier Inn Breakfast is Pre-Booked and Authorised.")
                .build(),
            Note.builder().id("continentalBreakfast").value("Continental Breakfast is Pre-Booked and Authorised.")
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
                    .pms("OP").targetId("BREAK").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("BFADCT").aemId("continentalBreakfast").sourceType("PACKAGE")
                    .pms("OP").targetId("BREAK").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("CITYTAX").aemId("cityTax").sourceType("PACKAGE")
                    .pms("OP").targetId("CITY").isTransactionCode(false).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("dinner").aemId("dinner").sourceType("ALLOWANCE")
                    .pms("OP").targetId("FBNA").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("alcohol").aemId("alcohol").sourceType("ALLOWANCE")
                    .pms("OP").targetId("FB").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("carParking").aemId("carParking").sourceType("ALLOWANCE")
                    .pms("OP").targetId("PARK").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("otherCharges").aemId("otherCharges").sourceType("ALLOWANCE")
                    .pms("OP").targetId("PARK").isTransactionCode(false).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("ultimateWifi").aemId("ultimateWifi").sourceType("ALLOWANCE")
                    .pms("OP").targetId("WIFI").isTransactionCode(false).isNotesMandatory(false).build())
        )
        .build();
  }

  private static BusinessItems createBusinessItems() {
    return BusinessItems.builder()
        .purchaseOrderNumber("1234")
        .customReferenceNumber("1234")
        .businessAllowances(List.of(BusinessAllowance.builder()
            .allowance("dinner")
            .budget(BigDecimal.TEN)
            .isAuthorised(true).build()))
        .businessNotes("Please charge the Credit card used to secure the reservation for all booked items which are "
            + "included in the final total rate.\n"
            + "The following Allowances are to be charged to the Credit card used to secure the reservation if "
            + "authorised:\n"
            + "10 Dinner Allowance is Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.")
        .build();
  }

  private static BusinessItems createBusinessItemsWithPackagesAndAccomodation(String packageCode) {
    return BusinessItems.builder()
        .purchaseOrderNumber("1234")
        .customReferenceNumber("1234")
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("dinner")
                .budget(BigDecimal.TEN)
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

  private static String mockBusinessNotes(String packageCode) {
    var businessNotes = new StringBuilder("Please charge the PIBA card used to secure the reservation for all booked items which are "
        + "included in the final total rate.\n"
        + "The following Allowances are to be charged to the PIBA card used to secure the reservation if "
        + "authorised:\n");
    switch (packageCode) {
      case "BFADBF" -> businessNotes.append("Premier Inn Breakfast is Pre-Booked and Authorised.\n");
      case "BFADCT" -> businessNotes.append("Continental Breakfast is Pre-Booked and Authorised.\n");
      default -> businessNotes.append(" ");
    }
    businessNotes.append("10 Dinner Allowance is Authorised.\n"
        + "Alcohol is NOT Authorised with the evening meal.\n"
        + "Car Parking is NOT Authorised.\n"
        + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.");
    return businessNotes.toString();
  }

  private PaymentResponse mockEckohPaymentResponse() {
    return PaymentResponse.builder()
        .providerResponse(ProviderResponse.builder()
            .eckohResponse(EckohResponse.builder()
                .token("1234567890")
                .expiry("0229")
                .build())
            .build())
        .build();
  }

  private PaymentResponse createPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId("12345D")
        .bookingReference(BOOKING_REFERENCE)
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .fraudCheckDecision("ACCEPT")
                .providerResult("01")
                .expiry("1228")
                .cardSchemeId("VS")
                .build())
            .build())
        .booking(uk.co.whitbread.basket.domain.model.payments.out.Booking.builder()
            .language("en")
            .channel("CCUI")
            .reference(BASKET_REFERENCE)
            .build())
        .paymentStatus(SUCCESS.getStatus())
        .payment(uk.co.whitbread.basket.domain.model.payments.out.Payment.builder().billing(
            uk.co.whitbread.basket.domain.model.payments.out.Billing.builder().firstName("Sm9obg==")
                .lastName("U21pdGg=").build()).build())
        .build();
  }

  private CcuiPaymentRequest createCcuiPaymentRequest(
      String paymentOption, String paymentType,
      BigDecimal totalCost, BigDecimal balanceOut,
      String subPaymentType) {
    return CcuiPaymentRequest.builder()
        .paymentOption(paymentOption)
        .subPaymentType(subPaymentType)
        .ccuiExtraItems(CcuiExtraItems.builder()
            .cardPresent(true)
            .accountCompanyItems(AccountCompanyItems.builder()
                .companyId("123")
                .build())
            .build())
        .paymentRequest(createPaymentRequest(paymentType, createReservation(totalCost, balanceOut), PAYMENT_TYPE_CARD))
        .useCache(false)
        .build();
  }

  private CcuiPaymentRequest createCcuiPaymentRequestBlankCardAddress(
      String paymentOption, String paymentType,
      BigDecimal totalCost, BigDecimal balanceOut,
      String subPaymentType) {
    return CcuiPaymentRequest.builder()
        .paymentOption(paymentOption)
        .subPaymentType(subPaymentType)
        .ccuiExtraItems(CcuiExtraItems.builder()
            .cardPresent(true)
            .accountCompanyItems(AccountCompanyItems.builder()
                .companyId("123")
                .build())
            .build())
        .paymentRequest(createPaymentRequestBlankCardAddress(paymentType, createReservation(totalCost, balanceOut)
            , PAYMENT_TYPE_CARD))
        .useCache(false)
        .build();
  }

  private CcuiPaymentRequest createAmendCcuiPaymentRequest(String paymentOption) {
    return CcuiPaymentRequest.builder()
        .paymentOption(paymentOption)
        .ccuiExtraItems(createCcuiExtraItems())
        .paymentRequest(createPaymentRequest())
        .build();
  }

  private static PaymentCcuiRequest createPaymentRequest() {
    return PaymentCcuiRequest.builder()
        .booking(Booking.builder()
            .language("en")
            .channel("CCUI")
            .journey("AMEND")
            .type("PAY_ON_ARRIVAL")
            .businessSite(BusinessSite.builder()
                .identifier(HOTEL_ID)
                .type("HOTEL")
                .build())
            .build())
        .payment(PaymentCcui.builder()
            .type("Account to company")
            .subType("MOTO")
            .billing(BillingCcui.builder()
                .email("test@whitbread.com")
                .build())
            .build())
        .build();
  }

  private static CcuiExtraItems createCcuiExtraItems() {
    return CcuiExtraItems.builder()
        .businessItems(BusinessItems.builder()
            .businessAllowances(List.of(
                BusinessAllowance.builder()
                    .allowance("premierInnBreakfast")
                    .budget(new BigDecimal(0))
                    .isAuthorised(true)
                    .build(),
                BusinessAllowance.builder()
                    .allowance("carParking")
                    .budget(new BigDecimal(0))
                    .isAuthorised(true)
                    .build()))
            .customReferenceNumber("TestReferenceNumber")
            .build())
        .cardPresent(false)
        .accountCompanyItems(AccountCompanyItems.builder()
            .charges("carParking,premierInnBreakfast")
            .companyId("2425205")
            .build())
        .build();
  }

  private static CcuiPaymentRequest ccuiPaymentRequestForParameterizedTest(String paymentOption) {
    return CcuiPaymentRequest.builder()
        .paymentOption(paymentOption)
        .ccuiExtraItems(CcuiExtraItems.builder()
            .accountCompanyItems(AccountCompanyItems.builder()
                .companyId("123")
                .build())
            .build())
        .useCache(false)
        .build();
  }

  private CcuiPaymentRequest createCcuiPaymentRequestWithCcuiExtraItems(
      String paymentOption, String paymentType,
      BigDecimal totalCost, BigDecimal balanceOut,
      String subPaymentType, boolean cardPresent) {

    var ccuiExtraItems = CcuiExtraItems.builder()
        .businessItems(createBusinessItems()).cardPresent(cardPresent).build();
    return CcuiPaymentRequest.builder()
        .paymentOption(paymentOption)
        .subPaymentType(subPaymentType)
        .paymentRequest(createPaymentRequest(paymentType, createReservation(totalCost, balanceOut),
            PAYMENT_TYPE_PIBA))
        .ccuiExtraItems(ccuiExtraItems)
        .useCache(false)
        .build();
  }

  private PaymentRequest createPayment3cp(
      String paymentType,
      ReservationByBasketRefResponse reservation, String type) {
    return PaymentRequest.builder()
        .requestId("randomInt")
        .payment(Payment.builder()
            .billing(Billing.builder()
                .title("Mr")
                .firstName("test")
                .lastName("test")
                .address(Address.builder()
                    .line1("line1")
                    .postalCode("postalCode")
                    .countryCode("GB")
                    .build())
                .build())
            .type(type)
            .subType("MOTO")
            .amount(Amount.builder()
                .minorUnits(reservation.getTotalCost() == null ? reservation.getBalanceOutstanding()
                    : reservation.getTotalCost())
                .currency("GB")
                .build())
            .card(Card.builder()
                .expiryYear("26")
                .expiryMonth("12")
                .token("434343434343434343")
                .build())
            .build())
        .booking(Booking.builder()
            .language("en")
            .channel("CCUI")
            .journey("BOOKING")
            .type(paymentType)
            .arrivalDate("9999-12-30")
            .departureDate("9999-12-31")
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .type("HOTEL")
                .name("London Holborn Premier Inn")
                .location("London")
                .build())
            .reference(BASKET_REFERENCE)
            .bookingReference(BOOKING_REFERENCE)
            .rooms(List.of(RoomType.builder()
                    .adultsNumber(1)
                    .type("DB")
                    .rate("SEMIFLEX")
                    .build(),
                RoomType.builder()
                    .adultsNumber(1)
                    .type("DB")
                    .rate("SEMIFLEX")
                    .build()))
            .build())
        .build();
  }

  private PaymentCcuiRequest createPaymentRequest(
      String paymentType,
      ReservationByBasketRefResponse reservation, String type) {
    return PaymentCcuiRequest.builder()
        .requestId("randomInt")
        .payment(PaymentCcui.builder()
            .billing(BillingCcui.builder()
                .title("Mr")
                .firstName("test")
                .lastName("test")
                .email("email@gmail.com")
                .differentBillingAddress(true)
                .address(AddressCcui.builder()
                    .line1("line1")
                    .postalCode("postalCode")
                    .countryCode("GB")
                    .build())
                .build())
            .type(type)
            .subType("MOTO")
            .amount(Amount.builder()
                .minorUnits(reservation.getTotalCost() == null ? reservation.getBalanceOutstanding()
                    : reservation.getTotalCost())
                .currency("GB")
                .build())
            .card(CardCcui.builder()
                .cardHolderFirstName("test")
                .cardHolderLastName("test2")
                .cardHolderAddress(AddressCcui.builder()
                    .line1("line1")
                    .postalCode("postalCode")
                    .countryCode("GB")
                    .addressType("HOME")
                    .build())
                .build())
            .build())
        .booking(Booking.builder()
            .language("en")
            .channel("CCUI")
            .journey("BOOKING")
            .type(paymentType)
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .type("HOTEL")
                .name("London Holborn Premier Inn")
                .location("London")
                .build())
            .reference(BASKET_REFERENCE)
            .rooms(List.of(RoomType.builder()
                .adultsNumber(1)
                .type("DB")
                .rate("SEMIFLEX")
                .build()))
            .build())
        .build();
  }

  private PaymentCcuiRequest createPaymentRequestBlankCardAddress(
      String paymentType,
      ReservationByBasketRefResponse reservation, String type) {
    return PaymentCcuiRequest.builder()
        .requestId("randomInt")
        .payment(PaymentCcui.builder()
            .billing(BillingCcui.builder()
                .title("Mr")
                .firstName("test")
                .lastName("test")
                .email("email@gmail.com")
                .differentBillingAddress(true)
                .address(AddressCcui.builder()
                    .line1("line1")
                    .postalCode("postalCode")
                    .countryCode("GB")
                    .build())
                .build())
            .type(type)
            .subType("MOTO")
            .amount(Amount.builder()
                .minorUnits(reservation.getTotalCost() == null ? reservation.getBalanceOutstanding()
                    : reservation.getTotalCost())
                .currency("GB")
                .build())
            .card(CardCcui.builder()
                .cardHolderFirstName("test")
                .cardHolderLastName("test2")
                .cardHolderAddress(null)
                .build())
            .build())
        .booking(Booking.builder()
            .language("en")
            .channel("CCUI")
            .journey("BOOKING")
            .type(paymentType)
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .type("HOTEL")
                .name("London Holborn Premier Inn")
                .location("London")
                .build())
            .reference(BASKET_REFERENCE)
            .rooms(List.of(RoomType.builder()
                .adultsNumber(1)
                .type("DB")
                .rate("SEMIFLEX")
                .build()))
            .build())
        .build();
  }

  private ReservationByBasketRefResponse createReservation(BigDecimal totalCost,
      BigDecimal balanceOut) {
    return ReservationByBasketRefResponse.builder()
        .totalCost(totalCost)
        .balanceOutstanding(balanceOut)
        .currencyCode("GB")
        .reservationByIdList(List.of(createSingleReservation("BFADBF"),
            createSingleReservation("BFADCT")))
        .hotelId(HOTEL_ID)
        .build();
  }

  private Reservation createSingleReservation(String packageCode) {
    return Reservation.builder()
        .reservationId(RESERVATION_ID)
        .roomStay(RoomStay.builder()
            .adultsNumber(1)
            .roomType("DB")
            .ratePlanCode("SEMIFLEX")
            .arrivalDate("9999-12-30")
            .departureDate("9999-12-31")
            .build())
        .reservationPackageList(List.of(ReservationPackagesDetails.builder().packageCode(packageCode).build()))
        .build();
  }

  private Basket createBasket(String basketRef, String bookingRef, String paymentId, String hotelId,
      String reservationId, BasketStatus status) {
    return Basket.builder()
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .reference(bookingRef)
        .basketId(basketRef)
        .paymentID(paymentId)
        .hotelId(hotelId)
        .status(status)
        .channel("CCUI")
        .originalBasketId(null)
        .items(Collections.singleton(BasketItem.builder().sourceId(RESERVATION_ID).build()))
        .build();
  }

  private DiscountRequest createDiscountRequest() {
    return DiscountRequest.builder()
        .basketReference(BASKET_REFERENCE)
        .discountAmount(BigDecimal.TEN)
        .build();
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

  private HotelPaymentInformation mockHotelPaymentInfoResponseUK() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(
            AcceptedCreditCard.builder().code("DL").code3CP("VS").codeOpera("VA").build(),
            AcceptedCreditCard.builder().code("EL").code3CP("VS").codeOpera("VA").build()
        ))
        .address(uk.co.whitbread.basket.domain.model.content.out.Address.builder().country("London").build())
        .build();
  }

  private static PaymentResponse createPaymentResponseForTest() {
    return PaymentResponse.builder()
        .providerResponse(ProviderResponse.builder()
            .eckohResponse(EckohResponse.builder()
                .token("434343434343434343")
                .expiry("1226").build())
            .build())
        .build();
  }
}