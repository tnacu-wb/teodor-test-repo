package uk.co.whitbread.basket.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doThrow;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.logic.mapper.DepositFolioResponseMapper;
import uk.co.whitbread.basket.domain.logic.utils.TokenUtils;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.model.payments.out.Amount;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.Booking;
import uk.co.whitbread.basket.domain.model.payments.out.Payment;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.CurrencyAmount;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFolioResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.Address;
import uk.co.whitbread.basket.domain.model.reservation.out.Billing;
import uk.co.whitbread.basket.domain.model.reservation.out.Guest;
import uk.co.whitbread.basket.domain.model.reservation.out.PaymentCard;
import uk.co.whitbread.basket.domain.model.reservation.out.RateInfo;
import uk.co.whitbread.basket.domain.model.reservation.out.RateInfoSummary;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationPackagesDetails;
import uk.co.whitbread.basket.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.basket.domain.model.rules.out.TransactionCode;
import uk.co.whitbread.basket.domain.model.rules.out.VatRuleResponse;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.domain.logic.config.ThirdpartyBookingProperties;

@ExtendWith(MockitoExtension.class)
class BackgroundChargeInPortImplTest {

  private static final String CONTEXT = "3rd Party";
  @Mock
  private HotelReservationOutPort reservationOutPort;
  @Mock
  private PaymentOutPort paymentOutPort;
  @Mock
  private RulesAgentOutPort rulesAgentOutPort;
  @Mock
  private BasketOutPort basketOutPort;
  @Mock
  private BasketInPort basketInPort;
  @Mock
  private RefundOutPort refundOutPort;
  @Mock
  private CleanUpTime cleanUpTime;
  @Mock
  private DepositFolioResponseMapper depositsMapper;
  @Mock
  private ThirdpartyBookingProperties thirdpartyBookingProperties;

  @InjectMocks
  private BackgroundChargeInPortImpl underTest;

  @Test
  void processBackgroundCharge_returnsWhenReservationListIsEmpty() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(ReservationByBasketRefResponse.builder()
              .reservationByIdList(Collections.emptyList())
              .build());

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(reservationOutPort).getReservationsByBasketReference("BASKET-1", "false", false);
      verifyNoInteractions(paymentOutPort, basketOutPort, basketInPort, refundOutPort);
    }
  }

  @Test
  void processBackgroundCharge_returnsWhenNoRouting() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.ZERO));

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verifyNoInteractions(basketOutPort, paymentOutPort, basketInPort, refundOutPort);
    }
  }

  @Test
  void processBackgroundCharge_returnsWhenBasketIsNotFound() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(null);

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(basketOutPort).getBasketById("BASKET-1");
      verify(paymentOutPort, never()).createMitCcPayment(any());
      verifyNoInteractions(basketInPort, refundOutPort);
    }
  }

  @Test
  void processBackgroundCharge_returnsWhenBasketIsNotThirdParty() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket("DIRECT"));

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(paymentOutPort, never()).createMitCcPayment(any());
      verifyNoInteractions(basketInPort, refundOutPort);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"EUR", ""})
  void processBackgroundCharge_savesChargesAndDepositFoliosWhenValidDataProvided(String currency) {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      var response = mockPaymentResponse();
      response.getPayment().getAmount().setCurrency(currency);
      response.getPayment().getAmount().setMinorUnits(BigDecimal.ONE);
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket(CONTEXT));
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(response);
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(depositsMapper.toDepositRequestModel(any())).thenReturn(Collections.emptyList());

      underTest.processBackgroundCharge("BASKET-1", "token");

      verify(basketInPort).saveCharges(any());
      verify(basketOutPort).updateBasketWithPaymentStatus(any());
      ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);
      verify(basketOutPort).updateBasketWithPaymentStatus(captor.capture());
      var basketUpdated = captor.getValue();
      if (currency == null) {
        assertNull(basketUpdated.getCurrency());
      } else {
        assertEquals(currency, basketUpdated.getCurrency());
      }
      assertEquals("1", basketUpdated.getTotalCost());
      assertEquals(COMPLETED, basketUpdated.getStatus());
      assertEquals("PAY-NEW", basketUpdated.getPaymentID());
      verify(reservationOutPort).saveDepositFolios(any());
      verifyNoInteractions(refundOutPort);
    }
  }

  @Test
  void processBackgroundCharge_refundsAndRethrowsWhenNoDepositsPrepared() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      var basket = mockBasket(CONTEXT);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(basket);
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(
              DepositFoliosResponse.builder().depositFolios(Collections.emptyList()).build());
      when(cleanUpTime.getCleanUpTime(any())).thenReturn(321L);

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(refundOutPort).processRefund(anyString(), any(), anyString());
      verify(basketOutPort).updateBasket(basket);
      assertEquals(BasketStatus.FAILED, basket.getStatus());
      assertEquals(BasketPaymentStatus.REFUNDING, basket.getPaymentStatus());
      assertEquals(321L, basket.getCleanUpTime());
    }
  }

  @Test
  void processBackgroundCharge_wrapsNonPaymentExceptionAndRefundsWhenSaveChargesFails() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      var basket = mockBasket(CONTEXT);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(basket);
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      doThrow(new RuntimeException("save failed")).when(basketInPort).saveCharges(any());
      when(cleanUpTime.getCleanUpTime(any())).thenReturn(444L);

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(refundOutPort).processRefund(anyString(), any(), anyString());
      verify(basketOutPort).updateBasket(basket);
      assertEquals(BasketStatus.FAILED, basket.getStatus());
      assertEquals(BasketPaymentStatus.REFUNDING, basket.getPaymentStatus());
      assertEquals(444L, basket.getCleanUpTime());
    }
  }

  @Test
  void processBackgroundCharge_refundsAndRethrowsWhenSaveDepositFoliosFails() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      var basket = mockBasket(CONTEXT);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(basket);
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      doThrow(new RuntimeException("opera save failed"))
          .when(reservationOutPort).saveDepositFolios(any());

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(refundOutPort).processRefund(anyString(), any(), anyString());
      verify(basketOutPort, atLeast(1)).updateBasket(basket);
      assertEquals(BasketStatus.FAILED, basket.getStatus());
      assertEquals(BasketPaymentStatus.REFUNDING, basket.getPaymentStatus());
    }
  }


  @Test
  void processBackgroundCharge_refundsWhenCityTaxVatRulesAreUnavailable() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      var basket = mockBasket(CONTEXT);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponseWithCityTaxPackage(BigDecimal.TEN));
      when(thirdpartyBookingProperties.getCityTaxList()).thenReturn(List.of("CITYTAX", "CITYEXP"));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(basket);
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(rulesAgentOutPort.getVatCodes(anyString(), any())).thenReturn(null);
      when(cleanUpTime.getCleanUpTime(any())).thenReturn(777L);

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(rulesAgentOutPort).getVatCodes("GB", List.of("CITYTAX"));
      verify(refundOutPort).processRefund(anyString(), any(), anyString());
      verify(basketOutPort).updateBasket(basket);
      assertEquals(BasketStatus.FAILED, basket.getStatus());
      assertEquals(BasketPaymentStatus.REFUNDING, basket.getPaymentStatus());
      assertEquals(777L, basket.getCleanUpTime());
    }
  }

  @Test
  void processBackgroundCharge_buildsPaymentRequestWithBillingAddressAndRoomStayDetails() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponseWithBillingAndRoomStay(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket(CONTEXT));
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(depositsMapper.toDepositRequestModel(any())).thenReturn(Collections.emptyList());

      underTest.processBackgroundCharge("BASKET-1", "token");

      ArgumentCaptor<uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest> captor =
          ArgumentCaptor.forClass(
              uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest.class);
      verify(paymentOutPort).createMitCcPayment(captor.capture());
      var request = captor.getValue();

      assertEquals("BOOK-1", request.getBooking().getBookingReference());
      assertEquals("BASKET-1", request.getBooking().getReference());
      assertEquals("2026-07-01", request.getBooking().getArrivalDate());
      assertEquals("2026-07-03", request.getBooking().getDepartureDate());
      assertEquals(1, request.getBooking().getRooms().size());
      assertEquals("DBL", request.getBooking().getRooms().get(0).getType());
      assertEquals(2, request.getBooking().getRooms().get(0).getAdultsNumber());
      assertEquals("RACK", request.getBooking().getRooms().get(0).getRate());

      assertEquals("Mr", request.getPayment().getBilling().getTitle());
      assertEquals("John", request.getPayment().getBilling().getFirstName());
      assertEquals("Doe", request.getPayment().getBilling().getLastName());
      assertEquals("john.doe@example.com", request.getPayment().getBilling().getEmail());
      assertEquals("Line 1", request.getPayment().getBilling().getAddress().getLine1());
      assertEquals("SW1A 1AA", request.getPayment().getBilling().getAddress().getPostalCode());
      assertEquals("GB", request.getPayment().getBilling().getAddress().getCountryCode());
    }
  }

  @Test
  void processBackgroundCharge_buildsPaymentRequestWithOnlyNonNullRoomStays() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponseWithMixedRoomStay(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket(CONTEXT));
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(depositsMapper.toDepositRequestModel(any())).thenReturn(Collections.emptyList());

      underTest.processBackgroundCharge("BASKET-1", "token");

      var captor = ArgumentCaptor.forClass(
          uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest.class);
      verify(paymentOutPort).createMitCcPayment(captor.capture());
      var request = captor.getValue();

      assertEquals(1, request.getBooking().getRooms().size());
      assertEquals("SGL", request.getBooking().getRooms().get(0).getType());
      assertEquals(1, request.getBooking().getRooms().get(0).getAdultsNumber());
      assertEquals("BAR", request.getBooking().getRooms().get(0).getRate());
      assertNull(request.getBooking().getArrivalDate());
      assertNull(request.getBooking().getDepartureDate());
    }
  }

  @Test
  void processBackgroundCharge_usesOnlyDistinctCityTaxPackageCodesWhenCallingVatRules() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      var basket = mockBasket(CONTEXT);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponseWithMixedPackages(BigDecimal.TEN));
      when(thirdpartyBookingProperties.getCityTaxList()).thenReturn(List.of("CITYTAX", "CITYEXP"));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(basket);
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(rulesAgentOutPort.getVatCodes(anyString(), any())).thenReturn(null);
      when(cleanUpTime.getCleanUpTime(any())).thenReturn(555L);

      assertThrows(PaymentException.class,
          () -> underTest.processBackgroundCharge("BASKET-1", "token"));

      verify(rulesAgentOutPort).getVatCodes("GB", List.of("CITYTAX", "CITYEXP"));
      verify(refundOutPort).processRefund(anyString(), any(), anyString());
    }
  }

  @Test
  void processBackgroundCharge_filtersOutCityTaxChargesAndKeepsNonCityTaxCharges() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponseWithCityTaxPackage(BigDecimal.TEN));
      when(thirdpartyBookingProperties.getCityTaxList()).thenReturn(List.of("CITYTAX"));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket(CONTEXT));
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFoliosWithCityAndNonCityTaxCharges());
      when(rulesAgentOutPort.getVatCodes(anyString(), any()))
          .thenReturn(mockVatRuleResponseForCityTax("CITYTAX", "CITY-TAX-TRX"));
      when(depositsMapper.toDepositRequestModel(any())).thenReturn(Collections.emptyList());

      underTest.processBackgroundCharge("BASKET-1", "token");

      var prepaidCapt = ArgumentCaptor.forClass(PrepaidDepositsRequest.class);
      verify(basketInPort).saveCharges(prepaidCapt.capture());

      var savedCharges = prepaidCapt.getValue().getPrepaidDeposits().get(0).getCharges();
      assertEquals(1, savedCharges.size());
      assertEquals("NON-CITY-TRX", savedCharges.get(0).getTransactionCode());
      verifyNoInteractions(refundOutPort);
    }
  }

  @Test
  void processBackgroundCharge_buildsPaymentRequestWithGuestNameFallbackWhenBillingIsNull() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponseWithGuestListNoBilling(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket(CONTEXT));
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(depositsMapper.toDepositRequestModel(any())).thenReturn(Collections.emptyList());

      underTest.processBackgroundCharge("BASKET-1", "token");

      var captor = ArgumentCaptor.forClass(
          uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest.class);
      verify(paymentOutPort).createMitCcPayment(captor.capture());
      var request = captor.getValue();

      assertEquals("Jane", request.getPayment().getBilling().getFirstName());
      assertEquals("Smith", request.getPayment().getBilling().getLastName());
      assertEquals("UNKNOWN", request.getPayment().getBilling().getAddress().getLine1());
      assertNull(request.getPayment().getBilling().getTitle());
      assertNull(request.getPayment().getBilling().getEmail());
    }
  }

  @Test
  void processBackgroundCharge_setsNoBillingWhenBillingAndGuestListAreBothAbsent() {
    try (MockedStatic<TokenUtils> tokenUtils = mockStatic(TokenUtils.class)) {
      tokenUtils.when(() -> TokenUtils.validateToken(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
          anyBoolean()))
          .thenReturn(mockReservationResponse(BigDecimal.TEN));
      when(basketOutPort.getBasketById("BASKET-1")).thenReturn(mockBasket(CONTEXT));
      when(paymentOutPort.createMitCcPayment(any())).thenReturn(mockPaymentResponse());
      when(reservationOutPort.getPreviewDepositsForReservationId(anyString(), anySet()))
          .thenReturn(mockPreviewDepositFolios());
      when(depositsMapper.toDepositRequestModel(any())).thenReturn(Collections.emptyList());

      underTest.processBackgroundCharge("BASKET-1", "token");

      var captor = ArgumentCaptor.forClass(
          uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest.class);
      verify(paymentOutPort).createMitCcPayment(captor.capture());
      var request = captor.getValue();

      assertNull(request.getPayment().getBilling());
    }
  }

  private ReservationByBasketRefResponse mockReservationResponseWithGuestListNoBilling(
      BigDecimal routing) {
    var summary = new RateInfoSummary(Collections.emptyList(), null, null, null, null, null, null,
        routing, "GBP", null, null, null);
    var reservation = Reservation.builder()
        .reservationId("RES-1")
        .rateInfo(new RateInfo(summary))
        .paymentCard(PaymentCard.builder().token("token").cardType("VI").build())
        .reservationGuestList(List.of(
            Guest.builder().givenName("Jane").surName("Smith").build()))
        .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("HOTEL-1")
        .currencyCode("GBP")
        .bookingReference("BOOK-1")
        .reservationByIdList(List.of(reservation))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationResponse(BigDecimal routing) {
    var summary = new RateInfoSummary(Collections.emptyList(), null, null, null, null, null, null,
        routing, "GBP", null, null, null);
    var reservation = Reservation.builder()
        .reservationId("RES-1")
        .rateInfo(new RateInfo(summary))
        .paymentCard(PaymentCard.builder().token("token").cardType("VI").build())
        .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("HOTEL-1")
        .currencyCode("GBP")
        .bookingReference("BOOK-1")
        .reservationByIdList(List.of(reservation))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationResponseWithCityTaxPackage(
      BigDecimal routing) {
    var summary = new RateInfoSummary(Collections.emptyList(), null, null, null, null, null, null,
        routing, "GBP", null, null, null);
    var reservation = Reservation.builder()
        .reservationId("RES-1")
        .rateInfo(new RateInfo(summary))
        .paymentCard(PaymentCard.builder().token("token").cardType("VI").build())
        .reservationPackageList(
            List.of(ReservationPackagesDetails.builder().packageCode("CITYTAX").build()))
        .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("HOTEL-1")
        .currencyCode("GBP")
        .bookingReference("BOOK-1")
        .reservationByIdList(List.of(reservation))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationResponseWithBillingAndRoomStay(
      BigDecimal routing) {
    var summary = new RateInfoSummary(Collections.emptyList(), null, null, null, null, null, null,
        routing, "GBP", null, null, null);
    var reservation = Reservation.builder()
        .reservationId("RES-1")
        .rateInfo(new RateInfo(summary))
        .paymentCard(PaymentCard.builder()
            .token("token")
            .cardType("VI")
            .cardHolderName("John Doe")
            .expirationDate("2027-06-01")
            .build())
        .billing(Billing.builder()
            .title("Mr")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .address(Address.builder()
                .addressLine1("Line 1")
                .addressLine2("Line 2")
                .cityName("London")
                .postalCode("SW1A 1AA")
                .country("GB")
                .build())
            .build())
        .roomStay(RoomStay.builder()
            .roomType("DBL")
            .adultsNumber(2)
            .ratePlanCode("RACK")
            .arrivalDate("2026-07-01")
            .departureDate("2026-07-03")
            .build())
        .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("HOTEL-1")
        .currencyCode("GBP")
        .bookingReference("BOOK-1")
        .reservationByIdList(List.of(reservation))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationResponseWithMixedRoomStay(
      BigDecimal routing) {
    var summary = new RateInfoSummary(Collections.emptyList(), null, null, null, null, null, null,
        routing, "GBP", null, null, null);

    var reservationWithoutRoomStay = Reservation.builder()
        .reservationId("RES-1")
        .rateInfo(new RateInfo(summary))
        .paymentCard(PaymentCard.builder().token("token").cardType("VI").build())
        .build();

    var reservationWithRoomStay = Reservation.builder()
        .reservationId("RES-2")
        .rateInfo(new RateInfo(summary))
        .roomStay(RoomStay.builder()
            .roomType("SGL")
            .adultsNumber(1)
            .ratePlanCode("BAR")
            .arrivalDate("2026-08-01")
            .departureDate("2026-08-02")
            .build())
        .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("HOTEL-1")
        .currencyCode("GBP")
        .bookingReference("BOOK-1")
        .reservationByIdList(List.of(reservationWithoutRoomStay, reservationWithRoomStay))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationResponseWithMixedPackages(
      BigDecimal routing) {
    var summary = new RateInfoSummary(Collections.emptyList(), null, null, null, null, null, null,
        routing, "GBP", null, null, null);
    var reservation = Reservation.builder()
        .reservationId("RES-1")
        .rateInfo(new RateInfo(summary))
        .paymentCard(PaymentCard.builder().token("token").cardType("VI").build())
        .reservationPackageList(List.of(
            ReservationPackagesDetails.builder().packageCode("CITYTAX").build(),
            ReservationPackagesDetails.builder().packageCode("OTHER").build(),
            ReservationPackagesDetails.builder().packageCode("CITYEXP").build(),
            ReservationPackagesDetails.builder().packageCode("CITYTAX").build()))
        .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("HOTEL-1")
        .currencyCode("GBP")
        .bookingReference("BOOK-1")
        .reservationByIdList(List.of(reservation))
        .build();
  }

  private Basket mockBasket(String idContext) {
    return Basket.builder()
        .basketId("BASKET-1")
        .reference("BOOK-1")
        .hotelId("HOTEL-1")
        .currency("GBP")
        .channel("WEB")
        .paymentID("PAY-OLD")
        .idContext(idContext)
        .build();
  }

  private PaymentResponse mockPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId("PAY-NEW")
        .booking(Booking.builder()
            .channel("WEB")
            .type("PAY_NOW")
            .build())
        .payment(
            Payment.builder().amount(Amount.builder()
                .currency("RON")
                .minorUnits(BigDecimal.TWO).build()).build())
        .build();
  }

  private DepositFoliosResponse mockPreviewDepositFolios() {
    var charge = DepositFolioCharge.builder()
        .currencyAmount(CurrencyAmount.builder().amount(BigDecimal.TEN).currencyCode("GBP").build())
        .quantity(1)
        .reference("POST-1")
        .transactionCode("TRX-1")
        .build();

    var folio = DepositFolioResponse.builder()
        .paymentId("10001")
        .reservationId("RES-1")
        .hotelId("HOTEL-1")
        .vatRegion("GB")
        .charges(List.of(charge))
        .build();

    return DepositFoliosResponse.builder()
        .depositFolios(List.of(folio))
        .build();
  }

  private DepositFoliosResponse mockPreviewDepositFoliosWithCityAndNonCityTaxCharges() {
    var cityTaxCharge = DepositFolioCharge.builder()
        .currencyAmount(CurrencyAmount.builder().amount(BigDecimal.ONE).currencyCode("GBP").build())
        .quantity(1)
        .reference("POST-CITY")
        .transactionCode("CITY-TAX-TRX")
        .build();

    var nonCityCharge = DepositFolioCharge.builder()
        .currencyAmount(CurrencyAmount.builder().amount(BigDecimal.TEN).currencyCode("GBP").build())
        .quantity(1)
        .reference("POST-NON-CITY")
        .transactionCode("NON-CITY-TRX")
        .build();

    var folio = DepositFolioResponse.builder()
        .paymentId("10001")
        .reservationId("RES-1")
        .hotelId("HOTEL-1")
        .vatRegion("GB")
        .charges(List.of(cityTaxCharge, nonCityCharge))
        .build();

    return DepositFoliosResponse.builder()
        .depositFolios(List.of(folio))
        .build();
  }

  private VatRuleResponse mockVatRuleResponseForCityTax(String pkgCode, String tranCode) {
    return VatRuleResponse.builder()
        .vatRegion("GB")
        .tranCodes(List.of(TransactionCode.builder().pkgCode(pkgCode).tranCode(tranCode).build()))
        .build();
  }
}

