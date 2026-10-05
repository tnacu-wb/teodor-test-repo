package uk.co.whitbread.infrastructure.rest.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.infrastructure.config.PromoServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.promotion.PromoServiceClient;
import uk.co.whitbread.infrastructure.rest.client.promotion.exceptions.PromotionException;
import uk.co.whitbread.promo.generated.models.promotion.PromoKindResponseDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.URI;

import org.springframework.web.util.UriBuilder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PromoServiceClientTest {

  @Mock
  private WebClient promoServiceWebClient;

  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @InjectMocks
  private PromoServiceClient promoServiceClient;

  @Mock
  private UriBuilder uriBuilder;

  @Mock
  private PromoServiceProperties promoServiceProperties;

  @Test
  void getPromoKind_shouldReturnResponse() {
    // Arrange
    var promoCode = "FX10R";
    var expected = new PromoKindResponseDto();

    ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor =
            ArgumentCaptor.forClass(Function.class);

    when(promoServiceProperties.getPromoKindEndpoint())
            .thenReturn("/promo-kind");

    when(promoServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(uriCaptor.capture()))
            .thenReturn(requestHeadersSpec);

    when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
    when(uriBuilder.queryParam(anyString(), any(Object[].class))).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create("http://localhost"));

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
            .thenReturn(Mono.just(expected));

    // Act
    PromoKindResponseDto result =
            promoServiceClient.getPromoKind(promoKindRequest(promoCode));

    // Execute the captured URI builder lambda
    uriCaptor.getValue().apply(uriBuilder);

    // Assert
    assertNotNull(result);
    assertSame(expected, result);

    verify(uriBuilder).path(anyString());
    verify(uriBuilder).queryParam("promoCode", "FX10R");
    verify(uriBuilder).queryParam("country", "GB");
    verify(uriBuilder).queryParam("channel", "PI");
    verify(uriBuilder).queryParam("subChannel", "WEB");
    verify(uriBuilder).build();
  }

  @Test
  void getPromoKind_shouldThrowPromotionException_onErrorStatus() {
    var promoCode = "PROMOCODE";
    var exception = mock(PromotionException.class);

    when(promoServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    ArgumentCaptor<Function<ClientResponse, Mono<? extends Throwable>>> captor =
        ArgumentCaptor.forClass(Function.class);
    when(responseSpec.onStatus(any(), captor.capture()))
        .thenAnswer(invocation -> {
          var fn = captor.getValue();

          ClientResponse mockResponse = mock(ClientResponse.class);
          ClientResponse.Headers headers = mock(ClientResponse.Headers.class);

          when(mockResponse.headers()).thenReturn(headers);
          when(mockResponse.bodyToMono(any(Class.class)))
              .thenReturn(Mono.error(exception));

          fn.apply(mockResponse);
          return responseSpec;
        });

    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
        .thenReturn(Mono.error(exception));

    PromoKindRequest request = promoKindRequest(promoCode);
    assertThrows(PromotionException.class,
            () -> promoServiceClient.getPromoKind(request));
  }


  @Test
  void getPromoKind_shouldPropagateRuntimeException() {
    var promoCode = "FX10R";

    when(promoServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
        .thenReturn(Mono.error(new RuntimeException("Connection failed")));

    PromoKindRequest request = promoKindRequest(promoCode);
    assertThrows(RuntimeException.class,
            () -> promoServiceClient.getPromoKind(request));
  }

  private PromoKindRequest promoKindRequest(String promoCode) {
    return PromoKindRequest.builder()
            .promoCode(promoCode)
            .country("GB")
            .channel("PI")
            .subChannel("WEB")
            .build();
  }
}

