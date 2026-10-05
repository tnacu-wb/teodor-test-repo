package uk.co.whitbread.dashboard.infrastructure.rest.client.content.service;

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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.dashboard.infrastructure.rest.utils.CustomTestResponseSpec;


@ExtendWith(MockitoExtension.class)
class ContentClientTest {

  @InjectMocks
  private ContentClient contentClient;
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
    contentClient.getRoomTypeInformation("gb", "en", "pi");

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
        () -> contentClient.getRoomTypeInformation("gb", "en", "pi"));
  }
}