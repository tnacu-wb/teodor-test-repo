package uk.co.whitbread.basket.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohAddress;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohAgent;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohAmount;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohBilling;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohBooking;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohBusinessSite;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.PaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.Billing;
import uk.co.whitbread.basket.domain.model.payments.out.Booking;
import uk.co.whitbread.basket.domain.model.payments.out.Card;
import uk.co.whitbread.basket.domain.model.payments.out.EckohResponse;
import uk.co.whitbread.basket.domain.model.payments.out.Payment;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiEckohOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;

@ExtendWith(MockitoExtension.class)
class CcuiEckohInPortImplTest {

  @Mock
  private BasketOutPort basketOutPort;
  @Mock
  private PaymentOutPort paymentOutPort;
  @Mock
  private CcuiEckohOutPort ccuiEckohOutPort;
  @Mock
  private HotelReservationOutPort hotelReservationOutPort;
  @InjectMocks
  private CcuiEckohInPortImpl ccuiEckohInPort;

  private final static String REFERENCE = "abc-4cb9e2b5-4dda-44db-b5cb-09a7e85b7b21";
  private final static String BOOKING_REFERENCE = "ABC1234567";
  private final static String PAYMENT_ID = "456543D";

  @Test
  void getPaymentStatus_PaymentAbandoned() {
    //Arrange
    when(basketOutPort.getBasketById(REFERENCE))
        .thenReturn(createBasketNoPaymentID().toBuilder().status(BasketStatus.FAILED).build())
        .thenReturn(createBasketNoPaymentID().toBuilder().status(BasketStatus.CANCELLED).build());

    //Act
    var response_failed = ccuiEckohInPort.paymentStatus(REFERENCE);
    var response_cancelled = ccuiEckohInPort.paymentStatus(REFERENCE);

    //Assert
    assertThat(response_failed, notNullValue());
    assertThat(response_cancelled, notNullValue());
    assertThat(response_failed.getStatus(), is(PaymentStatus.ABANDONED));
    assertThat(response_cancelled.getStatus(), is(PaymentStatus.ABANDONED));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoInteractions(paymentOutPort);
  }

  @Test
  void getPaymentStatus_usingEckohResponse_ShouldReturnSuccess() {
    //Arrange
    when(basketOutPort.getBasketById(REFERENCE)).thenReturn(createBasketWithEckoh(PAYMENT_ID)
        .toBuilder()
        .status(BasketStatus.OPEN)
        .build());
    when(paymentOutPort.getPaymentConfirmation(createBasket(PAYMENT_ID).getPaymentID())).thenReturn(
        createPaymentResponseEckoh(100, PaymentStatus.SUCCESS.toString(), Boolean.TRUE));

    //Act
    var response = ccuiEckohInPort.paymentStatus(REFERENCE);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(PaymentStatus.SUCCESS));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(paymentOutPort);
  }

  @Test
  void getPaymentStatus_usingBasketStatus_ShouldReturnSuccess() {
    //Arrange
    when(basketOutPort.getBasketById(REFERENCE)).thenReturn(
        createBasketWithEckoh(PAYMENT_ID)
            .toBuilder().status(BasketStatus.COMPLETED).build());
    //Act
    var response = ccuiEckohInPort.paymentStatus(REFERENCE);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(PaymentStatus.SUCCESS));
    verifyNoMoreInteractions(basketOutPort);
  }

  @Test
  void getPaymentStatus_ShouldReturnPending() {
    //Arrange
    var hotelCode = "mock-hotel";
    when(basketOutPort.getBasketById(REFERENCE)).thenReturn(
        createBasketWithEckoh(PAYMENT_ID)
            .toBuilder().hotelId(hotelCode).status(BasketStatus.PAY_PENDING).build());
    when(paymentOutPort.getPaymentConfirmation(createBasket(PAYMENT_ID).getPaymentID())).thenReturn(
        createPaymentResponseEckoh(0, null, Boolean.FALSE));

    //Act
    var response = ccuiEckohInPort.paymentStatus(REFERENCE);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(PaymentStatus.PENDING));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(paymentOutPort);
  }

  @Test
  void getPaymentStatus_ShouldReturnNotFound() {
    //Arrange
    when(basketOutPort.getBasketById(REFERENCE)).thenReturn(
        createBasketWithEckoh(null)
            .toBuilder().status(BasketStatus.PAY_PENDING).build());

    //Act
    var response = ccuiEckohInPort.paymentStatus(REFERENCE);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(PaymentStatus.NOT_FOUND));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoInteractions(paymentOutPort);
  }

  @Test
  void getPaymentStatus_ShouldReturnFailed() {
    //Arrange
    when(basketOutPort.getBasketById(REFERENCE)).thenReturn(
        createBasketWithEckoh(PAYMENT_ID).toBuilder()
            .status(BasketStatus.OPEN)
            .build());
    when(paymentOutPort.getPaymentConfirmation(createBasket(PAYMENT_ID).getPaymentID())).thenReturn(
        createPaymentResponseEckoh(101, Mockito.anyString(), Boolean.TRUE));

    //Act
    var response = ccuiEckohInPort.paymentStatus(REFERENCE);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(PaymentStatus.FAILED));
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(paymentOutPort);
  }

  @Test
  void getEckohPayment_shouldReturnOk() {
    //Arrange
    final var basket = createBasket(PAYMENT_ID);
    final var basketWithEckohResponse = createBasket(PAYMENT_ID);
    when(hotelReservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false)))
        .thenReturn(createReservation());
    when(basketOutPort.getBasketById(REFERENCE)).thenReturn(basketWithEckohResponse);
    when(ccuiEckohOutPort.createPaymentRequest(any(EckohPaymentRequest.class)))
        .thenReturn(createPaymentRequestEckoh());
    when(paymentOutPort.createPayment(any(PaymentRequest.class)))
        .thenReturn(createPaymentResponseEckoh());
    when(basketOutPort.updateBasket(any(Basket.class)))
        .thenReturn(createBasket(createPaymentResponseEckoh().getPaymentId()));

    //Act
    var response = ccuiEckohInPort.getEckohPayment(REFERENCE, createEckohPaymentRequest());

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getPaymentId());
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(ccuiEckohOutPort);
    verifyNoMoreInteractions(paymentOutPort);

  }

  private PaymentResponse createPaymentResponseEckoh() {
    return PaymentResponse.builder()
        .paymentId(PAYMENT_ID)
        .build();
  }

  private PaymentRequest createPaymentRequestEckoh() {
    return PaymentRequest.builder()
        .booking(uk.co.whitbread.basket.domain.model.payments.in.Booking.builder()
            .type("type")
            .journey("journey")
            .channel("channel")
            .businessSite(BusinessSite.builder()
                .identifier("identifier")
                .type("type")
                .build())
            .build())
        .requestId("test")
        .payment(uk.co.whitbread.basket.domain.model.payments.in.Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(BigDecimal.ZERO)
                .build())
            .environment("N/A")
            .subType("ECKOH")
            .type("ECKOH")
            .build())
        .build();
  }

  private EckohPaymentRequest createEckohPaymentRequest() {
    return EckohPaymentRequest.builder()
        .requestId("test")
        .payment(uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPayment.builder()
            .amount(EckohAmount.builder()
                .currency("GBP")
                .minorUnits(0)
                .build())
            .environment("N/A")
            .subType("ECKOH")
            .type("ECKOH")
            .billing(EckohBilling.builder()
                .address(EckohAddress.builder()
                    .postalCode("N/A")
                    .line1("N/A")
                    .build())
                .build())
            .build())
        .booking(EckohBooking.builder()
            .language("en")
            .channel("CCUI")
            .agent(EckohAgent.builder()
                .name("TEST")
                .email("test@test.com")
                .build())
            .businessSite(EckohBusinessSite.builder()
                .type("type")
                .identifier("iden")
                .name("name")
                .country("DE")
                .location("location")
                .build())
            .build())
        .build();
  }

  private PaymentResponse createPaymentResponseEckoh(int resultCode, String result,
      Boolean hasEckoh) {
    if (hasEckoh) {
      return PaymentResponse.builder()
          .paymentId(PAYMENT_ID)
          .bookingReference(REFERENCE)
          .providerResponse(ProviderResponse.builder()
              .threecResponse(ThreeCResponse.builder()
                  .fraudCheckDecision("REJECTED")
                  .fraudCheckResult("202")
                  .build())
              .eckohResponse(EckohResponse.builder()
                  .result(result)
                  .paymentId(PAYMENT_ID)
                  .resultCode(resultCode)
                  .build())
              .build())
          .booking(Booking.builder()
              .journey("SECURITY_CHECK")
              .reference(REFERENCE)
              .build())
          .payment(createPayment())
          .build();
    }
    return PaymentResponse.builder()
        .paymentId(PAYMENT_ID)
        .bookingReference(BOOKING_REFERENCE)
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .fraudCheckDecision("REJECTED")
                .fraudCheckResult("202")
                .build())
            .build())
        .booking(Booking.builder()
            .journey("SECURITY_CHECK")
            .reference(REFERENCE)
            .build())
        .payment(createPayment())
        .build();
  }

  private Payment createPayment() {
    return Payment.builder()
        .billing(Billing.builder()
            .email("test")
            .build()
        )
        .card(Card.builder()
            .expiryMonth("02")
            .expiryYear("24")
            .token("xx2232")
            .build())
        .build();
  }

  private Basket createBasket(String paymentID) {
    return Basket.builder()
        .paymentID(paymentID)
        .status(BasketStatus.OPEN)
        .reference(BOOKING_REFERENCE)
        .basketId(REFERENCE)
        .build();
  }

  private Basket createBasketNoPaymentID() {
    return Basket.builder()
        .reference(BOOKING_REFERENCE)
        .basketId(REFERENCE)
        .build();
  }

  private Basket createBasketWithEckoh(String paymentID) {
    return Basket.builder()
        .paymentID(paymentID)
        .reference(BOOKING_REFERENCE)
        .basketId(REFERENCE)
        .build();
  }

  private ReservationByBasketRefResponse createReservation() {
    return ReservationByBasketRefResponse.builder()
        .totalCost(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.ZERO)
        .currencyCode("GB")
        .reservationByIdList(List.of(createSingleReservation()))
        .hotelId("MANOLD")
        .build();
  }

  private Reservation createSingleReservation() {
    return Reservation.builder()
        .roomStay(RoomStay.builder()
            .adultsNumber(1)
            .roomType("DB")
            .ratePlanCode("SEMIFLEX")
            .arrivalDate("9999-12-31")
            .build())
        .build();
  }
}