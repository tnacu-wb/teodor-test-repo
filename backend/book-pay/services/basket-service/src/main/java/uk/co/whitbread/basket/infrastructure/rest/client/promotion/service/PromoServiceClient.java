package uk.co.whitbread.basket.infrastructure.rest.client.promotion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemPromoCodeRequestDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemPromoCodeResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.exception.PromotionException;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.service.properties.PromoServiceProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class PromoServiceClient {

  private final WebClient promoWebClient;
  private final PromoServiceProperties promoServiceProperties;

  public PromoServiceClient(
      @Qualifier("promoWebClient") WebClient promoWebClient,
      PromoServiceProperties promoServiceProperties) {
    this.promoWebClient = promoWebClient;
    this.promoServiceProperties = promoServiceProperties;
  }

  public PromoKindResponseDto getPromoKind(String promoCode) {
    return promoWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(promoServiceProperties.getPromoKindEndpoint())
            .queryParam("promoCode", promoCode)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(PromotionException.class);
        })
        .bodyToMono(PromoKindResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch promo kind data from promo service."))
        .block();
  }

  public RedeemPromoCodeResponseDto redeemPromoCode(String promoCode,
      String bookingReference) {

    var redeemRequest = new RedeemPromoCodeRequestDto()
        .promoCode(promoCode)
        .bookingReference(bookingReference);

    return promoWebClient
        .post()
        .uri(uriBuilder -> uriBuilder
            .path(promoServiceProperties.getRedeemEndpoint())
            .build())
        .bodyValue(redeemRequest)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(PromotionException.class);
        })
        .bodyToMono(RedeemPromoCodeResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(
                "Error while trying to redeem promo code [%s] with booking reference [%s]",
                promoCode,
                bookingReference
            )
        ))
        .block();
  }

}