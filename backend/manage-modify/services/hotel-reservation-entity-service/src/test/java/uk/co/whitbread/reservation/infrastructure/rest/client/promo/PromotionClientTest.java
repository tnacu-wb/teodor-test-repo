package uk.co.whitbread.reservation.infrastructure.rest.client.promo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.PromotionException;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.PromotionClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.properties.PromoServiceProperties;

@ExtendWith(MockitoExtension.class)
class PromotionClientTest {

  @Mock

  private WebClient promotionWebClient;

  @Mock
  private PromoServiceProperties promoServiceProperties;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  private PromotionClient promotionClient;

  @BeforeEach
  void setUp() {
    promotionClient = new PromotionClient(promotionWebClient, promoServiceProperties);
  }

  @Test
  void getPromoKind_shouldReturnResponse_whenSuccess() {

    String promoCode = "ABC123";

    PromoKindResponseDto expected = new PromoKindResponseDto();

    when(promotionWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
        .thenReturn(Mono.just(expected));

    PromoKindResponseDto result = promotionClient.getPromoKind(promoCode);

    assertNotNull(result);
    assertEquals(expected, result);
  }

  @Test
  void getPromoKind_shouldThrowNoHeaderDataException_when4xx() {

    when(promotionWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
        .thenReturn(responseSpec);

    when(responseSpec.bodyToMono(PromoKindResponseDto.class))
        .thenReturn(Mono.error(
            new PromotionException(ErrorCode.DIGITAL_PROMOTION_ALREADY_USED_EXCEPTION, "no header")
        ));

    assertThrows(PromotionException.class,
        () -> promotionClient.getPromoKind("BAD"));

  }
}
