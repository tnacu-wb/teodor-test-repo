package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import uk.co.whitbread.dashboard.infrastructure.rest.utils.CustomTestResponseSpec;


@ExtendWith(MockitoExtension.class)
class HotelReservationClientTest {

  @InjectMocks
  private HotelReservationClient hotelReservationClient;
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Test
  void testFindBooking_success() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    hotelReservationClient.findBooking("Doe", LocalDate.parse("2025-02-22"), "AH1231231", "en");

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testRetrieveBooking_error() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.error(new Throwable()));
    assertThrows(Throwable.class,
        () -> hotelReservationClient.findBooking("Doe", LocalDate.parse("2025-02-22"),
            "AH1231231", "en"));
  }

  @Test
  void testRetrieveBooking_httpStatusError() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    hotelReservationClient.findBooking("Doe", LocalDate.parse("2025-02-22"),
        "AH1231231", "en");
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetManageBookingInfo_success() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    hotelReservationClient.getManageBookingInfo("AH2312321", "LONEUS", "dummy_token",
        "en", LocalDateTime.now());

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetManageBookingInfo_error() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.error(new Throwable()));
    assertThrows(Throwable.class,
        () -> hotelReservationClient.getManageBookingInfo("AH2312321", "LONEUS", "dummy_token",
            "en", LocalDateTime.now()));
  }

  @Test
  void testGetManageBookingInfo_httpStatusError() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    hotelReservationClient.getManageBookingInfo("AH2312321", "LONEUS", "dummy_token",
        "en", LocalDateTime.now());
    verifyNoMoreInteractions(webClient);
  }

}
