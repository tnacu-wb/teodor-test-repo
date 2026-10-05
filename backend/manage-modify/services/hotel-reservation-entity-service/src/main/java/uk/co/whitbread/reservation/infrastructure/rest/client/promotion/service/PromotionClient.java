package uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoHeaderDataException;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.properties.PromoServiceProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
public class PromotionClient {

  private final PromoServiceProperties promoServiceProperties;
  private final WebClient promotionWebClient;

  public PromotionClient(@Qualifier("promotionWebClient") WebClient promotionWebClient,
      PromoServiceProperties promoServiceProperties) {
    this.promoServiceProperties = promoServiceProperties;
    this.promotionWebClient = promotionWebClient;
  }

  public PromoKindResponseDto getPromoKind(String promoCode) {
    return promotionWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(promoServiceProperties.getPromoKindEndpoint())
            .queryParam("promoCode", promoCode)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(NoHeaderDataException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(PromoKindResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch promo kind data from promo service."))
        .block();
  }

}
