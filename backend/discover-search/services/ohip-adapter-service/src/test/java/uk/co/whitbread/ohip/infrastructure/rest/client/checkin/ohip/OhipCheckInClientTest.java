package uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskChangeReservation;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.exception.CheckInException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipCheckInClientTest {

  @InjectMocks
  OhipCheckInClient ohipCheckInClient;
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
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Test
  void getCheckInResponse__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CheckInResponse.class)).thenReturn(mockCheckInResponse());

    //Act
    CheckInResponse checkInResponse =
        ohipCheckInClient.getCheckInResponse(new CheckInRequest(), "HOTEL_ID", "RSV_ID");

    //Assert
    assertThat(checkInResponse, notNullValue());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getCheckInResponse_error_4xx() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(CheckInException.class,
        () -> ohipCheckInClient.getCheckInResponse(new CheckInRequest(), "HOTEL_ID", "RSV_ID"));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get checkIn details", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendKioskChangeReservationRequest__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    ohipCheckInClient.sendKioskChangeReservationRequest("LONEUS", "12345",
        new KioskChangeReservation());

    //Assert
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void sendKioskChangeReservationRequest_error_4xx() {
    // Arrange
    CheckInException ex = mock(CheckInException.class);
    when(ex.getMessage()).thenReturn("Error while trying to update Comments to reservation");
    when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.error(ex));
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(CheckInException.class,
        () -> ohipCheckInClient.sendKioskChangeReservationRequest("HOTEL_ID", "RES_ID",
            new KioskChangeReservation()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to update Comments to reservation", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  private Mono<CheckInResponse> mockCheckInResponse() {
    return Mono.just(new CheckInResponse());
  }
}
