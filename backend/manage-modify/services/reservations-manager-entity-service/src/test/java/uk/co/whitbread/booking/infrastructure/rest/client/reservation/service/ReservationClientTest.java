package uk.co.whitbread.booking.infrastructure.rest.client.reservation.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationBookingInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationCancelRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowancesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationCancelInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties.ReservationProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ReservationClientTest {

  private static final String AUTHORIZATION = "authorization";
  private static final String BOOKING_REFERENCE = "bookingReference";
  private static final String BASKET_REFERENCE = "bookingReference";
  private static final String PAYMENT_OPTION = "paymentOption";
  private static final String ARRIVAL = "2023-11-25";
  private static final String HOTEL_ID = "hotelId";
  private static final String BOOKING_SUBCHANNEL = "subchannel";
  private static final String BOOKING_CHANNEL = "channel";
  private static final String COUNTRY = "country";
  private static final String LANGUAGE = "language";

  @Mock
  ReservationProperties properties;
  @InjectMocks
  private ReservationClient reservationClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void getReservationInformationAuth_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationResponseDto.class)).thenReturn(
        Mono.just(new ReservationResponseDto()));

    // Act
    var response = reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationInformation_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationResponseDto.class)).thenReturn(
        Mono.just(new ReservationResponseDto()));

    // Act
    var response = reservationClient.getReservationInformation(BOOKING_REFERENCE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationInformation_throwsInternalBasketException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));

    // Assert
    assertThrows(InternalBasketException.class,
        () -> reservationClient.getReservationInformation(BOOKING_REFERENCE));
  }

  @Test
  void getReservationInformation_throwsBasketException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));

    // Assert
    assertThrows(InternalBasketException.class,
        () -> reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE));
  }

  @Test
  void getReservationCancelInformation_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationCancelInfoResponseDto.class)).thenReturn(
        Mono.just(new ReservationCancelInfoResponseDto()));

    // Act
    var response = reservationClient.getCancelReservationInformation(AUTHORIZATION, mockReservationCancelInfoRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void findBooking_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Object.class)).thenReturn(
        Mono.just(new Object()));

    // Act
    var response = reservationClient.findBooking(ReservationBookingInfoRequestDto.builder().resNo("1234").build());

    // Assert
    assertNotNull(response);
  }

  @Test
  void findBooking_throwsEx() {
    // Arrange
    var requestDto = ReservationBookingInfoRequestDto.builder().build();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(Object.class)).thenReturn(Mono.error(
        new InternalBasketException("exp", "message", new Exception(), 100)));

    // Act
    // Assert
    assertThrows(InternalBasketException.class,
        () -> reservationClient.findBooking(requestDto));
  }

  @Test
  void getReservationCancelInformation_throwsBasketException() {
    var mockReservationCancelInfoRequest = ReservationCancelRequestDto.builder().build();
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationCancelInfoResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));

    // Assert
    assertThrows(InternalBasketException.class,
        () -> reservationClient.getCancelReservationInformation(BASKET_REFERENCE, mockReservationCancelInfoRequest));
  }

  @Test
  void getReservationDinnerAllowancesInformation_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationAllowancesDto.class)).thenReturn(
        Mono.just(new ReservationAllowancesDto()));

    // Act
    var response = reservationClient.getDinnerAllowances(BASKET_REFERENCE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationDinnerAllowancesInformation_throwsBasketException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationAllowancesDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));
    // Assert
    assertThrows(InternalBasketException.class,
        () -> reservationClient.getDinnerAllowances(BASKET_REFERENCE));
  }

  @Test
  void cancelReservation_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getOperaCancelReservationEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancelReservationResponseDto.class)).thenReturn(
        Mono.just(new CancelReservationResponseDto()));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = reservationClient.cancelReservation(AUTHORIZATION, mockCancelReservationRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void cancelReservation_throwsBasketException() {
    var mockCancelReservationRequest = CancelReservationRequestDto.builder().build();
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getOperaCancelReservationEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(CancelReservationResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));
    // Assert
    assertThrows(InternalBasketException.class,
        () -> reservationClient.cancelReservation(BASKET_REFERENCE, mockCancelReservationRequest));
  }

  private ReservationCancelRequestDto mockReservationCancelInfoRequest() {
    return ReservationCancelRequestDto
        .builder()
        .channel(BOOKING_CHANNEL)
        .subchannel(BOOKING_SUBCHANNEL)
        .basketReference(BASKET_REFERENCE)
        .hotelId(HOTEL_ID)
        .language(LANGUAGE)
        .country(COUNTRY)
        .userDateTime(ARRIVAL)
        .build();
  }

  private CancelReservationRequestDto mockCancelReservationRequest() {
    return CancelReservationRequestDto
        .builder()
        .basketReference(BASKET_REFERENCE)
        .paymentOption(PAYMENT_OPTION)
        .hotelId(HOTEL_ID)
        .build();
  }
}
