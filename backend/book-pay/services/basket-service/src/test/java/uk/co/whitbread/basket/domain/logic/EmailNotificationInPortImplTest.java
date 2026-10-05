package uk.co.whitbread.basket.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.email.in.EmailRequest;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.reservation.out.Billing;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;

@ExtendWith(MockitoExtension.class)
class EmailNotificationInPortImplTest {

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private HotelReservationOutPort reservationOutPort;

  @Mock
  private EmailNotificationService emailNotificationService;

  @InjectMocks
  private EmailNotificationInPortImpl emailInPort;

  @Test
  void triggerEmailNotificationInvoice() {
    var emailRequest = EmailRequest.builder().bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464").build();
    var basket = mockBasket();
    var reservation = Reservation.builder()
        .roomStay(RoomStay.builder().departureDate("2022-02-02").build())
        .billing(Billing.builder().email("test@gmail.com").build())
        .build();
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(Collections.singletonList(reservation))
        .build();

    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false))).thenReturn(reservationResponse);

    emailInPort.triggerEmailNotification(emailRequest);

    verify(emailNotificationService, times(1)).sendEmailNotificationEvent(any(Basket.class),
        any(EmailNotificationEventType.class), eq(reservation.getBilling().getEmail()), anyBoolean());
  }

  @Test
  void triggerEmailNotificationInvoiceForDistribution() {
    var emailRequest = EmailRequest.builder()
        .bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .email("test@whitbread.com").build();
    var basket = Basket.builder()
        .basketId("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .reference("BKR12345")
        .channel("DISTR")
        .build();
    var reservation = Reservation.builder()
        .roomStay(RoomStay.builder().departureDate("2022-02-02").build())
        .billing(Billing.builder().email("test@gmail.com").build())
        .build();
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(Collections.singletonList(reservation))
        .build();

    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false))).thenReturn(reservationResponse);

    emailInPort.triggerEmailNotification(emailRequest);

    verify(emailNotificationService, times(1)).sendEmailNotificationEvent(any(Basket.class),
        any(EmailNotificationEventType.class), eq(emailRequest.getEmail()), anyBoolean());
  }

  @Test
  void triggerEmailNotificationConfResend() {
    var emailRequest = EmailRequest.builder().bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464").build();
    var basket = Basket.builder().basketId("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464").reference("BKR12345")
        .status(BasketStatus.COMPLETED).build();
    var reservation = Reservation.builder()
        .roomStay(RoomStay.builder().departureDate("2200-02-02").build())
        .billing(Billing.builder().email("test@gmail.com").build())
        .build();
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(Collections.singletonList(reservation))
        .build();

    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false))).thenReturn(reservationResponse);

    emailInPort.triggerEmailNotification(emailRequest);

    verify(emailNotificationService, times(1)).sendEmailNotificationEvent(any(Basket.class),
        any(EmailNotificationEventType.class), anyString(), anyBoolean());
  }

  @Test
  void triggerEmailNotificationAmend_Success() {
    var deposits = mockDeposits();
    var emailRequest = mockEmailRequest(deposits, EmailNotificationEventType.AMEND.toString());
    var basket = mockBasket();
    var reservationResponse = mockReservationByBasketRefResponse();

    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false))).thenReturn(reservationResponse);

    emailInPort.triggerEmailNotification(emailRequest);

    verify(emailNotificationService, times(1)).sendEmailNotificationEvent(basket, EmailNotificationEventType.AMEND,
        emailRequest.getEmail(), false, deposits);
  }

  @Test
  void triggerEmailNotificationAmend_Failure() {
    var emailRequest = EmailRequest.builder()
        .bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .email("test@gmail.com")
        .build();
    var basket = mockBasket();
    var reservationResponse = mockReservationByBasketRefResponse();

    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false))).thenReturn(reservationResponse);

    emailInPort.triggerEmailNotification(emailRequest);

    verify(emailNotificationService, times(0)).sendEmailNotificationEvent(basket, EmailNotificationEventType.AMEND,
        emailRequest.getEmail(), Boolean.TRUE);
  }

  @Test
  void triggerEmailNotification_BasketReferenceNotValidException() {
    var emailRequest = EmailRequest.builder()
        .bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .email("test@gmail.com")
        .build();
    var message = String.format("Basket is no longer valid: %s", emailRequest.getBookingReference());
    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(null);

    var exception = assertThrows(BasketReferenceNotValidException.class,
        () -> emailInPort.triggerEmailNotification(emailRequest));

    // Assert
    assertEquals(ErrorCode.DIGITAL_EMAIL_BASKET_INVALID_EXCEPTION.getCode(),
        exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_EMAIL_BASKET_INVALID_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void triggerEmailNotificationCancel() {
    var deposits = mockDeposits();
    var emailRequest = mockEmailRequest(deposits, EmailNotificationEventType.CANCEL.toString());
    var basket = mockBasket();
    ReservationByBasketRefResponse reservationResponse = mockReservationByBasketRefResponse();

    when(basketOutPort.getBasketById(emailRequest.getBookingReference())).thenReturn(basket);
    when(reservationOutPort.getReservationsByBasketReference(anyString(),
        anyString(), eq(false))).thenReturn(reservationResponse);

    emailInPort.triggerEmailNotification(emailRequest);

    verify(emailNotificationService).sendEmailNotificationEvent(basket, EmailNotificationEventType.CANCEL,
        emailRequest.getEmail(), false, deposits);
  }


  private static ReservationByBasketRefResponse mockReservationByBasketRefResponse() {
    var reservation = Reservation.builder()
        .roomStay(RoomStay.builder()
            .departureDate(LocalDate.ofInstant(
                Instant.now().plus(1, ChronoUnit.DAYS),
                ZoneId.of("UTC")
            ).format(DateTimeFormatter.ISO_LOCAL_DATE))
            .build())
        .billing(Billing.builder()
            .email("test@gmail.com")
            .build())
        .build();
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(Collections.singletonList(reservation))
        .build();
    return reservationResponse;
  }

  private static Basket mockBasket() {
    return Basket.builder()
        .basketId("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .reference("BKR12345")
        .build();
  }

  private static EmailRequest mockEmailRequest(List<Deposits> deposits, String emailRequestType) {
    return EmailRequest.builder()
        .bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .email("test@gmail.com")
        .emailRequestType(emailRequestType)
        .deposits(deposits)
        .build();
  }

  private static List<Deposits> mockDeposits() {
    return List.of(Deposits.builder()
        .paymentReference("123")
        .build());
  }
}
