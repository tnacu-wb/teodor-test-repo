package uk.co.whitbread.infrastructure.rest.client.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.infrastructure.config.PromoServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.promotion.exceptions.PromotionException;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.promo.generated.models.promotion.PromoKindResponseDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromoServiceClient {

  private final WebClient promoServiceWebClient;
  private final PromoServiceProperties promoServiceProperties;

  public PromoKindResponseDto getPromoKind(PromoKindRequest request) {
    return promoServiceWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(promoServiceProperties.getPromoKindEndpoint())
            .queryParam("promoCode", request.getPromoCode())
            .queryParam("country", request.getCountry())
            .queryParam("channel", request.getChannel())
            .queryParam("subChannel", request.getSubChannel())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(PromotionException.class);
        })
        .bodyToMono(PromoKindResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch promo kind data from promo service."))
        .block();
  }
}
