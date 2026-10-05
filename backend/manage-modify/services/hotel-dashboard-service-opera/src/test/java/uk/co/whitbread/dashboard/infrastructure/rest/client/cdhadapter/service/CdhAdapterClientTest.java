package uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class CdhAdapterClientTest {

  @InjectMocks
  private CdhAdapterClient cdhAdapterClient;
  @Mock
  private WebClient webClient;
  @Mock
  private CdhAdapterProperties cdhAdapterProperties;
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
  void testRetrieveBooking_success() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
    when(cdhAdapterProperties.getReservationByIdEndpoint()).thenReturn("/reservation");
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    cdhAdapterClient.retrieveBooking("321399321");

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testRetrieveBooking_error() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
    when(cdhAdapterProperties.getReservationByIdEndpoint()).thenReturn("/reservation");
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.error(new Throwable()));
    assertThrows(Throwable.class, () -> cdhAdapterClient.retrieveBooking("321399321"));
  }

  @Test
  void testRetrieveBooking_httpStatusError() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
    when(cdhAdapterProperties.getReservationByIdEndpoint()).thenReturn("/reservation");
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    cdhAdapterClient.retrieveBooking("321399321");

    verifyNoMoreInteractions(webClient);
  }

}
