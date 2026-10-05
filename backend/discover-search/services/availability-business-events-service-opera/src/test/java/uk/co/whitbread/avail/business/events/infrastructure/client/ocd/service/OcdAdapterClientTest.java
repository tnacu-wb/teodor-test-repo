package uk.co.whitbread.avail.business.events.infrastructure.client.ocd.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import uk.co.whitbread.avail.business.events.infrastructure.client.ocd.exception.OcdAdapterException;
import uk.co.whitbread.avail.business.events.infrastructure.utils.CustomTestResponseSpec;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxResponseDto;

@ExtendWith(MockitoExtension.class)
class OcdAdapterClientTest {

  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @InjectMocks
  private OcdAdapterClient ocdAdapterClient;

  @Test
  void getTax_Success() {

    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TaxResponseDto.class)).thenReturn(Mono.just(new TaxResponseDto()));

    // Act
    var response = ocdAdapterClient.getTax("hotelId", "2024-10-01", "2024-10-05", 2,
        "ratePlanCode", "roomType");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getTax_OcdAdapterException() {
    // Arrange
    OcdAdapterException ex = mock(OcdAdapterException.class);
    Mono<TaxResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(TaxResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(OcdAdapterException.class,
        () -> ocdAdapterClient.getTax("hotelId", "2024-10-01", "2024-10-05", 2,
            "ratePlanCode", "roomType"));
  }
}
