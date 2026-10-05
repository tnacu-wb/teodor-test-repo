package uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.HotelReservationException;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmAmendRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationByBasketRefResponseDto;

@ExtendWith(MockitoExtension.class)
class ReservationsClientTest {

  HotelReservationException hotelReservationException =
      new HotelReservationException("message", "debug message", new Exception(), 100);

  @InjectMocks
  private ReservationsClient reservationsClient;
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void testConfirmReservation__success() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ConfirmReservationResponseDto.class))
        .thenReturn(mockReservationResponse());

    //Act
    ConfirmReservationRequestDto confirmReservationRequestDto = new ConfirmReservationRequestDto();
    var confirmReservationResponseDto = reservationsClient.confirmReservation(
        confirmReservationRequestDto);

    //Assert
    assertThat(confirmReservationResponseDto, notNullValue());
    assertEquals(confirmReservationResponseDto.getHotelId(), "MANOLD");
  }

  @Test
  void testSendAcknowledge__failure() {
    // Arrange
    var request = new ConfirmReservationRequestDto();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ConfirmReservationResponseDto.class)).thenReturn(
        Mono.error(
            new HotelReservationException("message", "debug message", new Exception(), 100)));
    //Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationsClient
            .confirmReservation(request));
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals("debug message", debugMessage);

  }

  @Test
  void testCancelReservation__success() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancelReservationResponseDto.class))
        .thenReturn(mockCancelReservationResponse());

    //Act
    CancelReservationRequestDto cancelReservationRequestDto = new CancelReservationRequestDto();
    var cancelReservationResponseDto = reservationsClient.cancelReservation(
        cancelReservationRequestDto);

    //Assert
    assertThat(cancelReservationResponseDto, notNullValue());
  }

  @Test
  void testCancelReservation_error_4xx() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(CancelReservationResponseDto.class)).thenReturn(
        Mono.error(
            new HotelReservationException("message", "debug message", new Exception(), 100)));
    //Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationsClient.cancelReservation(new CancelReservationRequestDto()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals("debug message", debugMessage);
  }

  @Test
  void testConfirmAmend__success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class))
        .thenReturn(mockReservationByBasketResponse());

    //Act
    ConfirmAmendRequestDto confirmAmendRequestDto = new ConfirmAmendRequestDto();
    var reservationByBasketRefResponseDto = reservationsClient.confirmAmend(confirmAmendRequestDto);

    //Assert
    assertThat(reservationByBasketRefResponseDto, notNullValue());
  }

  @Test
  void testConfirmAmend_AmendReservationException() {
    // Arrange
    var request = new ConfirmAmendRequestDto();
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(
        responseSpecMock);
    when(responseSpecMock.bodyToMono(ReservationByBasketRefResponseDto.class))
        .thenReturn(Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationsClient.confirmAmend(request));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals("debug message", debugMessage);
  }

  private Mono<ConfirmReservationResponseDto> mockReservationResponse() {

    var confirmationResponseDto = new ConfirmReservationResponseDto();
    confirmationResponseDto.setHotelId("MANOLD");
    return Mono.just(confirmationResponseDto);
  }

  private Mono<ReservationByBasketRefResponseDto> mockReservationByBasketResponse() {
    return Mono.just(new ReservationByBasketRefResponseDto());
  }

  private Mono<CancelReservationResponseDto> mockCancelReservationResponse() {
    return Mono.just(new CancelReservationResponseDto());
  }
}