package uk.co.whitbread.basket.infrastructure.rest.client.promotion.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.basket.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemPromoCodeResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.exception.PromotionException;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PromoServiceClientTest {

  @InjectMocks
  private PromoServiceClient promoServiceClient;

  @Mock
  private WebClient promoWebClient;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestBodySpec requestBodySpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Test
  void getPromoKind_success() {
    when(promoWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve())
        .thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any()))
        .thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
        .thenReturn(Mono.just(new PromoKindResponseDto()));

    PromoKindResponseDto response =
        promoServiceClient.getPromoKind("TEST");

    assertNotNull(response);
  }

  @Test
  void getPromoKind_error_should_throw_exception() {
    when(promoWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve())
        .thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any()))
        .thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
        .thenReturn(Mono.error(mockPromotionException()));

    assertThrows(
        PromotionException.class,
        () -> promoServiceClient.getPromoKind("TEST")
    );
  }

  @Test
  void redeemPromoCode_success() {
    when(promoWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class)))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any()))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve())
        .thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any()))
        .thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RedeemPromoCodeResponseDto.class))
        .thenReturn(Mono.just(new RedeemPromoCodeResponseDto()));

    RedeemPromoCodeResponseDto response =
        promoServiceClient.redeemPromoCode("TEST", "BOOK123");

    assertNotNull(response);
  }

  @Test
  void redeemPromoCode_error_should_throw_exception() {
    when(promoWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class)))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any()))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve())
        .thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any()))
        .thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RedeemPromoCodeResponseDto.class))
        .thenReturn(Mono.error(mockPromotionException()));

    assertThrows(
        PromotionException.class,
        () -> promoServiceClient.redeemPromoCode("TEST", "BOOK123")
    );
  }

  private PromotionException mockPromotionException() {
    return new PromotionException(
        "Promotion service error",
        "Promotion service returned an error response",
        null,
        500
    );
  }
}