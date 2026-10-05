package uk.co.whitbread.digitalkey.infrastructure.rest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.KioskCheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.AxpProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.utils.CustomTestResponseSpec;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import java.util.function.Function;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class OhipAdapterClientTest {

  @Mock
  private WebClient ohipAdapterWebClient;

  @Mock
  private OhipAdapterProperties ohipAdapterProperties;

  @InjectMocks
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @Mock
  private AxpProperties axpProperties;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testDoCheckIn_success() {
    KioskCheckInRequest request = new KioskCheckInRequest();
    CheckInResponse response = new CheckInResponse();

    when(ohipAdapterWebClient.post()).thenReturn(requestBodyUriSpec);
    when(ohipAdapterProperties.getCheckInEndpoint()).thenReturn("/checkin");
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(Mono.class), eq(KioskCheckInRequest.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CheckInResponse.class)).thenReturn(Mono.just(response));

    CheckInResponse result = ohipAdapterClient.doCheckIn(request);
    assertNotNull(result);
  }

  @Test
  void testGetReservationDetails_success() {
    ReservationByBasketRefResponseDto response = new ReservationByBasketRefResponseDto();

    when(ohipAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(ohipAdapterProperties.getReservationsByBasketReservationIds()).thenReturn("/reservations");
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(Mono.just(response));

    ReservationByBasketRefResponseDto result = ohipAdapterClient.getReservationDetails("hotel123", "res123");
    assertNotNull(result);
  }

  @Test
  void testDoCheckIn_error() {
    KioskCheckInRequest request = new KioskCheckInRequest();

    when(ohipAdapterWebClient.post()).thenReturn(requestBodyUriSpec);
    when(ohipAdapterProperties.getCheckInEndpoint()).thenReturn("/checkin");
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(Mono.class), eq(KioskCheckInRequest.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CheckInResponse.class)).thenReturn(Mono.error(new RuntimeException("Check-in failed")));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      ohipAdapterClient.doCheckIn(request);
    });

    assertEquals("Check-in failed", exception.getMessage());
  }

  @Test
  void testGetReservationDetails_error() {
    when(ohipAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(ohipAdapterProperties.getReservationsByBasketReservationIds()).thenReturn("/reservations");
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(Mono.error(new RuntimeException("Reservation fetch failed")));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      ohipAdapterClient.getReservationDetails("hotel123", "res123");
    });

    assertEquals("Reservation fetch failed", exception.getMessage());
  }

  //ALLIANTS OTP TEST CASE
  @Test
  void testGetReservationWithExternalId_success() {
    ReservationDetailsEnhancedDto response = new ReservationDetailsEnhancedDto();

    when(ohipAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(ohipAdapterProperties.getExternalReservationEndpoint()).thenReturn("/external");
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationDetailsEnhancedDto.class)).thenReturn(Mono.just(response));

    ReservationDetailsEnhancedDto result = ohipAdapterClient.sendGetReservationsByExternalReferenceId("hotel123");
    assertNotNull(result);
  }

  @Test
  void testGetReservationExternalId_error() {

    when(ohipAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(ohipAdapterProperties.getExternalReservationEndpoint()).thenReturn("/external");
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationDetailsEnhancedDto.class)).thenReturn(Mono.error(new RuntimeException("Get Reservation - failed")));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      ohipAdapterClient.sendGetReservationsByExternalReferenceId("hotel123");
    });

    assertEquals("Get Reservation - failed", exception.getMessage());
  }

  @Test
  void testUpdateExternalReference_Throws4xxException() {
    // Arrange
    when(ohipAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(ohipAdapterProperties.getExternalReservationEndpoint()).thenReturn("/external");
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    assertThrows(RuntimeException.class,
        () -> ohipAdapterClient.sendGetReservationsByExternalReferenceId("AKN123"));

  }

  @Test
  void testUpdateUdfc20_success() {
        UpdateUdfc20Request request = new UpdateUdfc20Request();

    when(ohipAdapterWebClient.put()).thenReturn(requestBodyUriSpec);
    when(ohipAdapterProperties.getUpdateUdfc20Endpoint()).thenReturn("/update");
    when(requestBodyUriSpec.uri("/update")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(UpdateUdfc20Request.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    // success
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipAdapterClient.updateUdfc20(request);

    // Assert
    assertTrue(true);

    // verify chain invoked
    verify(ohipAdapterWebClient).put();
    verify(requestBodyUriSpec).contentType(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).body(any(Mono.class), eq(UpdateUdfc20Request.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(), any());
    verify(responseSpec).toBodilessEntity();

  }

}
