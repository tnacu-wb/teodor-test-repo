package uk.co.whitbread.payments.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.model.out.BasketReservation;
import uk.co.whitbread.payments.domain.model.out.ChargeType;
import uk.co.whitbread.payments.domain.model.out.Deposits;
import uk.co.whitbread.payments.domain.model.out.DepositsResponse;
import uk.co.whitbread.payments.domain.model.out.PaymentActionsResponse;
import uk.co.whitbread.payments.domain.model.out.RateInfoSummary;
import uk.co.whitbread.payments.domain.ports.secondary.HotelEntityPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;

@ExtendWith(MockitoExtension.class)
class PaymentActionsPortImplTest {

  @Mock
  private HotelEntityPort hotelEntityPort;

  @Mock
  private ReservationsPort reservationPort;

  @InjectMocks
  private PaymentActionsPortImpl paymentActionsPortImpl;

  private BasketReservation reservation;
  private RateInfoSummary rateInfoSummary;

  private static final String BASKET_REFERENCE = "BASKET-123";
  private static final String HOTEL_ID = "HOTEL-456";
  private static final String RESERVATION_ID = "RES-789";
  private static final String UK_CODE = "GB";
  private static final String DE_CODE = "DE";
  private static final String CURRENCY = "GBP";
  private static final BigDecimal TOTAL_COST = new BigDecimal("100.00");
  private static final BigDecimal OUTSTANDING_BALANCE = new BigDecimal("50.00");
  private static final BigDecimal GUEST_PAY_AMOUNT = new BigDecimal("30.00");
  private static final BigDecimal ROUTING_AMOUNT = new BigDecimal("20.00");

  @BeforeEach
  void setUp() {

    rateInfoSummary = RateInfoSummary.builder()
        .reservationId(RESERVATION_ID)
        .build();

    reservation = BasketReservation.builder()
        .hotelId(HOTEL_ID)
        .reservationIds(List.of(RESERVATION_ID))
        .totalCost(TOTAL_COST)
        .outstandingBalance(OUTSTANDING_BALANCE)
        .currencyCode(CURRENCY)
        .paymentMethod("VI")
        .rateInfoList(Set.of(rateInfoSummary))
        .build();
  }

  @Test
  void testDeterminePaymentActions_ReservationNotFound_ThrowsException() {
    // Arrange
    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(null);

    // Act & Assert
    PaymentMethodsException exception = assertThrows(PaymentMethodsException.class,
        () -> paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE));

    // Verify the error details
    assertNotNull(exception);
    assertNotNull(exception.getDebugMessage());
    assertTrue(exception.getDebugMessage().contains("Reservation not found"));
    assertTrue(exception.getDebugMessage().contains(BASKET_REFERENCE));
    verify(reservationPort).findBasketReservations(BASKET_REFERENCE);
  }

  @ParameterizedTest(name = "Rule1 country={0}")
  @MethodSource("rule1CountryCases")
  void testRule1_NoPaymentOrAuthorizeCard(
      String countryCode,
      boolean expectedDisplayPaymentPage,
      ChargeType expectedChargeType) {
    // Rule 1/2 depends on guestPay/routing and country, not on outstanding balance.
    rateInfoSummary.setGuestPayAmount(BigDecimal.ZERO);
    rateInfoSummary.setRoutingAmount(BigDecimal.ZERO);
    reservation.setPaymentMethod("VI");
    reservation.setOutstandingBalance(new BigDecimal("50.00"));
    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(countryCode);

    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    assertNotNull(response);
    assertEquals(expectedDisplayPaymentPage, response.isDisplayPaymentPage());
    assertEquals(1, response.getPaymentActions().size());
    assertEquals(expectedChargeType, response.getPaymentActions().get(0).getChargeType());
    assertEquals(BigDecimal.ZERO, response.getPaymentActions().get(0).getPrice().getAmount());

    verify(reservationPort).findBasketReservations(BASKET_REFERENCE);
    verify(hotelEntityPort).findHotelCountry(HOTEL_ID);
  }

  private static Stream<Arguments> rule1CountryCases() {
    return Stream.of(
        Arguments.of(UK_CODE, false, ChargeType.NO_PAYMENT),
        Arguments.of(DE_CODE, true, ChargeType.AUTHORIZE_CARD)
    );
  }


  @ParameterizedTest(name = "{0}")
  @MethodSource("rule2PaymentMethodCases")
  void testRule2_RoutingAndPaymentMethod_NoPayment(
      String scenario,
      String paymentMethod,
      ChargeType expectedChargeType) {
    rateInfoSummary.setGuestPayAmount(BigDecimal.ZERO);
    rateInfoSummary.setRoutingAmount(ROUTING_AMOUNT);
    reservation.setPaymentMethod(paymentMethod);

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(reservationPort.getDepositFolios(HOTEL_ID, List.of(RESERVATION_ID))).thenReturn(null);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(UK_CODE);

    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    assertNotNull(response);
    assertEquals(expectedChargeType, response.getPaymentActions().get(0).getChargeType());
    assertFalse(response.isDisplayPaymentPage());
  }

  private static Stream<Arguments> rule2PaymentMethodCases() {
    return Stream.of(
        Arguments.of("BU payment method with routing -> NO_PAYMENT", "BU", ChargeType.NO_PAYMENT),
        Arguments.of("BD payment method with routing -> NO_PAYMENT", "BD", ChargeType.NO_PAYMENT),
        Arguments.of("non BU/BD payment method with routing -> CARD_ON_FILE", "VI", ChargeType.CARD_ON_FILE)
    );
  }

  @ParameterizedTest(name = "Rule3 country={0}")
  @MethodSource("rule3CountryCases")
  void testRule3_CreditCardOnly(String countryCode) {
    rateInfoSummary.setGuestPayAmount(GUEST_PAY_AMOUNT);
    rateInfoSummary.setRoutingAmount(BigDecimal.ZERO);
    reservation.setPaymentMethod("VI");
    reservation.setOutstandingBalance(new BigDecimal("50.00"));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(countryCode);

    // Act
    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    // Assert
    assertNotNull(response);
    assertTrue(response.isDisplayPaymentPage());
    assertEquals(1, response.getPaymentActions().size());
    assertEquals(ChargeType.CREDIT_CARD, response.getPaymentActions().get(0).getChargeType());
    assertEquals(GUEST_PAY_AMOUNT, response.getPaymentActions().get(0).getPrice().getAmount());
  }

  private static Stream<Arguments> rule3CountryCases() {
    return Stream.of(
        Arguments.of(UK_CODE),
        Arguments.of(DE_CODE)
    );
  }

  @ParameterizedTest(name = "Rule4 country={0}")
  @MethodSource("rule4CountryCases")
  void testRule4_CombinedPayment(String countryCode) {
    rateInfoSummary.setGuestPayAmount(GUEST_PAY_AMOUNT);
    rateInfoSummary.setRoutingAmount(ROUTING_AMOUNT);
    reservation.setPaymentMethod("VI");
    reservation.setOutstandingBalance(new BigDecimal("50.00"));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(reservationPort.getDepositFolios(HOTEL_ID, List.of(RESERVATION_ID)))
        .thenReturn(null);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(countryCode);

    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    assertNotNull(response);
    assertTrue(response.isDisplayPaymentPage());
    assertEquals(2, response.getPaymentActions().size());
    assertEquals(ChargeType.CARD_ON_FILE, response.getPaymentActions().get(0).getChargeType());
    assertEquals(ROUTING_AMOUNT, response.getPaymentActions().get(0).getPrice().getAmount());
    assertEquals(1, response.getPaymentActions().get(0).getOrder());
    assertEquals(ChargeType.CREDIT_CARD, response.getPaymentActions().get(1).getChargeType());
    assertEquals(GUEST_PAY_AMOUNT, response.getPaymentActions().get(1).getPrice().getAmount());
    assertEquals(2, response.getPaymentActions().get(1).getOrder());
  }

  private static Stream<Arguments> rule4CountryCases() {
    return Stream.of(
        Arguments.of(UK_CODE),
        Arguments.of(DE_CODE)
    );
  }

  @ParameterizedTest(name = "Rule5 country={0}")
  @MethodSource("rule5CountryCases")
  void testRule5_CardOnFileOnly(String countryCode) {
    rateInfoSummary.setGuestPayAmount(BigDecimal.ZERO);
    rateInfoSummary.setRoutingAmount(ROUTING_AMOUNT);
    reservation.setPaymentMethod("VI");
    reservation.setOutstandingBalance(new BigDecimal("50.00"));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(reservationPort.getDepositFolios(HOTEL_ID, List.of(RESERVATION_ID)))
        .thenReturn(new DepositsResponse(Collections.emptyList()));
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(countryCode);

    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    assertNotNull(response);
    assertFalse(response.isDisplayPaymentPage());
    assertEquals(1, response.getPaymentActions().size());
    assertEquals(ChargeType.CARD_ON_FILE, response.getPaymentActions().get(0).getChargeType());
    assertEquals(ROUTING_AMOUNT, response.getPaymentActions().get(0).getPrice().getAmount());
  }

  private static Stream<Arguments> rule5CountryCases() {
    return Stream.of(
        Arguments.of(UK_CODE),
        Arguments.of(DE_CODE)
    );
  }

  @Test
  void testDeterminePaymentActions_NullCurrency_UsesDefault() {
    // Arrange
    rateInfoSummary.setGuestPayAmount(BigDecimal.ZERO);
    rateInfoSummary.setRoutingAmount(BigDecimal.ZERO);
    reservation.setCurrencyCode(null);

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(UK_CODE);

    // Act
    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    // Assert
    assertNotNull(response);
    assertEquals("GBP", response.getPaymentActions().get(0).getPrice().getCurrency());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("depositAndReservationContext")
  void testDeterminePaymentActions_DepositAndReservationContext(
      String scenario,
      DepositsResponse depositsResponse,
      BigDecimal outstandingBalance,
      String paymentMethod,
      ChargeType expectedChargeType,
      boolean expectedDisplayPaymentPage) {
    rateInfoSummary.setGuestPayAmount(BigDecimal.ZERO);
    rateInfoSummary.setRoutingAmount(ROUTING_AMOUNT);
    reservation.setOutstandingBalance(outstandingBalance);
    reservation.setPaymentMethod(paymentMethod);

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(reservationPort.getDepositFolios(HOTEL_ID, List.of(RESERVATION_ID))).thenReturn(depositsResponse);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(UK_CODE);

    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    assertNotNull(response);
    assertEquals(expectedDisplayPaymentPage, response.isDisplayPaymentPage());
    assertEquals(expectedChargeType, response.getPaymentActions().get(0).getChargeType());
  }

  private static Stream<Arguments> depositAndReservationContext() {
    return Stream.of(
        Arguments.of(
            "deposits null -> noDepositFolios true -> CARD_ON_FILE",
            null,
            new BigDecimal("50.00"),
            "VI",
            ChargeType.CARD_ON_FILE,
            false),
        Arguments.of(
            "deposits empty -> noDepositFolios true -> CARD_ON_FILE",
            new DepositsResponse(Collections.emptyList()),
            new BigDecimal("50.00"),
            "VI",
            ChargeType.CARD_ON_FILE,
            false),
        Arguments.of(
            "deposits present + outstanding>0 -> Rule6 CREDIT_CARD",
            new DepositsResponse(List.of(new Deposits("p1", null))),
            new BigDecimal("50.00"),
            "VI",
            ChargeType.CREDIT_CARD,
            true),
        Arguments.of(
            "deposits present + outstanding null -> default zero -> NO_PAYMENT",
            new DepositsResponse(List.of(new Deposits("p1", null))),
            null,
            "VI",
            ChargeType.NO_PAYMENT,
            false),
        Arguments.of(
            "routing with BU payment method -> Rule2 NO_PAYMENT",
            null,
            new BigDecimal("50.00"),
            "BU",
            ChargeType.NO_PAYMENT,
            false)
    );
  }



  @Test
  void testDeterminePaymentActions_MultipleRooms_AggregatedAmounts() {
    // Arrange: Multiple rooms with different guestPay and routing amounts
    RateInfoSummary room1 = RateInfoSummary.builder()
        .reservationId("RES-001")
        .guestPayAmount(new BigDecimal("30.00"))
        .routingAmount(BigDecimal.ZERO)
        .build();

    RateInfoSummary room2 = RateInfoSummary.builder()
        .reservationId("RES-002")
        .guestPayAmount(new BigDecimal("20.00"))
        .routingAmount(BigDecimal.ZERO)
        .build();

    reservation.setRateInfoList(Set.of(room1, room2));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(UK_CODE);

    // Act
    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    // Assert - Should return ONE response with aggregated amounts
    assertNotNull(response);
    assertEquals(1, response.getPaymentActions().size());
    assertEquals(ChargeType.CREDIT_CARD, response.getPaymentActions().get(0).getChargeType());

    // Verify aggregated amount: 30.00 + 20.00 = 50.00
    assertEquals(new BigDecimal("50.00"),
        response.getPaymentActions().get(0).getPrice().getAmount(),
        "Should aggregate guestPay across all rooms");
  }

  @Test
  void testDeterminePaymentActions_MultipleRooms_CombinedPayment() {
    // Arrange: Multiple rooms with both guestPay and routing amounts
    RateInfoSummary room1 = RateInfoSummary.builder()
        .reservationId("RES-001")
        .guestPayAmount(new BigDecimal("30.00"))
        .routingAmount(new BigDecimal("20.00"))
        .build();

    RateInfoSummary room2 = RateInfoSummary.builder()
        .reservationId("RES-002")
        .guestPayAmount(new BigDecimal("15.00"))
        .routingAmount(new BigDecimal("10.00"))
        .build();

    RateInfoSummary room3 = RateInfoSummary.builder()
        .reservationId("RES-003")
        .guestPayAmount(new BigDecimal("25.00"))
        .routingAmount(new BigDecimal("30.00"))
        .build();

    reservation.setRateInfoList(Set.of(room1, room2, room3));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(UK_CODE);

    // Act
    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    // Assert - Should trigger Rule 4 with aggregated amounts
    assertNotNull(response);
    assertTrue(response.isDisplayPaymentPage());
    assertEquals(2, response.getPaymentActions().size(), "Should have both CARD_ON_FILE and CREDIT_CARD");

    // Verify CARD_ON_FILE (order 1) with aggregated routing: 20 + 10 + 30 = 60
    assertEquals(ChargeType.CARD_ON_FILE, response.getPaymentActions().get(0).getChargeType());
    assertEquals(new BigDecimal("60.00"),
        response.getPaymentActions().get(0).getPrice().getAmount(),
        "Should aggregate routing amounts across all rooms");
    assertEquals(1, response.getPaymentActions().get(0).getOrder());

    // Verify CREDIT_CARD (order 2) with aggregated guestPay: 30 + 15 + 25 = 70
    assertEquals(ChargeType.CREDIT_CARD, response.getPaymentActions().get(1).getChargeType());
    assertEquals(new BigDecimal("70.00"),
        response.getPaymentActions().get(1).getPrice().getAmount(),
        "Should aggregate guestPay amounts across all rooms");
    assertEquals(2, response.getPaymentActions().get(1).getOrder());
  }

  @Test
  void testDeterminePaymentActions_MultipleRooms_OnlyRouting() {
    // Arrange: Multiple rooms with only routing amounts (Rule 5)
    RateInfoSummary room1 = RateInfoSummary.builder()
        .reservationId("RES-001")
        .guestPayAmount(BigDecimal.ZERO)
        .routingAmount(new BigDecimal("40.00"))
        .build();

    RateInfoSummary room2 = RateInfoSummary.builder()
        .reservationId("RES-002")
        .guestPayAmount(BigDecimal.ZERO)
        .routingAmount(new BigDecimal("60.00"))
        .build();

    reservation.setRateInfoList(Set.of(room1, room2));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(UK_CODE);

    // Act
    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    // Assert - Should trigger Rule 5 with aggregated routing
    assertNotNull(response);
    assertFalse(response.isDisplayPaymentPage());
    assertEquals(1, response.getPaymentActions().size());
    assertEquals(ChargeType.CARD_ON_FILE, response.getPaymentActions().get(0).getChargeType());

    // Verify aggregated routing amount: 40 + 60 = 100
    assertEquals(new BigDecimal("100.00"),
        response.getPaymentActions().get(0).getPrice().getAmount(),
        "Should aggregate routing amounts across all rooms");
  }

  @Test
  void testDeterminePaymentActions_MultipleRooms_NullAmounts() {
    // Arrange: Multiple rooms with some null amounts
    RateInfoSummary room1 = RateInfoSummary.builder()
        .reservationId("RES-001")
        .guestPayAmount(new BigDecimal("50.00"))
        .routingAmount(null)
        .build();

    RateInfoSummary room2 = RateInfoSummary.builder()
        .reservationId("RES-002")
        .guestPayAmount(null)
        .routingAmount(new BigDecimal("30.00"))
        .build();

    RateInfoSummary room3 = RateInfoSummary.builder()
        .reservationId("RES-003")
        .guestPayAmount(new BigDecimal("25.00"))
        .routingAmount(new BigDecimal("15.00"))
        .build();

    reservation.setRateInfoList(Set.of(room1, room2, room3));

    when(reservationPort.findBasketReservations(BASKET_REFERENCE)).thenReturn(reservation);
    when(hotelEntityPort.findHotelCountry(HOTEL_ID)).thenReturn(DE_CODE);

    // Act
    PaymentActionsResponse response = paymentActionsPortImpl.determinePaymentActions(BASKET_REFERENCE);

    // Assert - Should handle nulls and aggregate: guestPay = 50 + 25 = 75, routing = 30 + 15 = 45
    assertNotNull(response);
    assertEquals(2, response.getPaymentActions().size());

    // CARD_ON_FILE with aggregated routing (ignoring nulls): 30 + 15 = 45
    assertEquals(new BigDecimal("45.00"),
        response.getPaymentActions().get(0).getPrice().getAmount());

    // CREDIT_CARD with aggregated guestPay (ignoring nulls): 50 + 25 = 75
    assertEquals(new BigDecimal("75.00"),
        response.getPaymentActions().get(1).getPrice().getAmount());
  }
}


