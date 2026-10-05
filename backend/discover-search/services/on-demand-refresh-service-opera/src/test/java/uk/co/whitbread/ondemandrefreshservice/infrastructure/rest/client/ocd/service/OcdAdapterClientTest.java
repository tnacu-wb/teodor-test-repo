package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxResponseDto;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OcdAdapterException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service.properties.OcdAdapterProperties;
import uk.co.whitbread.ondemandrefreshservice.utils.CustomTestResponseSpec;

import java.net.URI;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import org.springframework.web.util.UriBuilder;

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
  @Mock
  private OcdAdapterProperties ocdAdapterProperties;

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

  @Test
  void getTax_ShouldBuildCorrectUri() {
    // Arrange
    ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    // Capture the uri function
    when(ocdAdapterProperties.getOfferEndpoint()).thenReturn("uri");
    when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TaxResponseDto.class)).thenReturn(Mono.just(new TaxResponseDto()));
    // Act
    ocdAdapterClient.getTax("hotelId", "2024-10-01", "2024-10-05", 2, "ratePlanCode", "roomType");
    // Assert
    Function<UriBuilder, URI> uriFunction = uriFunctionCaptor.getValue();
    UriBuilder builder = UriComponentsBuilder.newInstance();
    URI uri = uriFunction.apply(builder);
    String uriString = uri.toString();
    assertTrue(uriString.contains("uri"));
    assertTrue(uriString.contains("2024-10-01"));
    assertTrue(uriString.contains("2024-10-05"));
    assertTrue(uriString.contains("ratePlanCode"));
    assertTrue(uriString.contains("roomType"));
    assertTrue(uriString.contains("adults=2"));
  }

}
