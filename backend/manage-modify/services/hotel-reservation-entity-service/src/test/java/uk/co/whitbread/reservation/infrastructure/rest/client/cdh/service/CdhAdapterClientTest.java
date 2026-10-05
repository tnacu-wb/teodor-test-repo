package uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GetEmployeeRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
public class CdhAdapterClientTest {

  @InjectMocks
  private CdhAdapterClient cdhAdapterClient;
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
  void testGetEmployee_success() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    cdhAdapterClient.getEmployee(new GetEmployeeRequestDto());

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetEmployee_error() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.error(new Throwable()));
    assertThrows(Throwable.class, () -> cdhAdapterClient.getEmployee(new GetEmployeeRequestDto()));
  }

  @Test
  void testGetEmployee_httpStatusError() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

    cdhAdapterClient.getEmployee(new GetEmployeeRequestDto());

    verifyNoMoreInteractions(webClient);
  }
}