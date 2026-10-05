package uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip;

import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_NEGOTIATED_RATES_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_PROMOTION_CODE_DETAILS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_RATEPLANINFO_DETAILS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_RATEPLAN_DETAILS_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HUB_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PROMOTION_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RATE_PLAN_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.NegotiatedRates;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.RatePlanInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PropertyPromotionCodes;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlansSummary;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.exceptions.RatePlansException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.properties.RatePlansOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@RequiredArgsConstructor
@Slf4j
@Component
public class OhipRatePlansClient {

  private final WebClient ohipWebClient;
  private final RatePlansOhipProperties ratePlansOhipProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "RatePlansCacheOhip")
  public Mono<RatePlansSummary> getRatePlans(List<String> ratePlans, String hotelId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(
                    ratePlansOhipProperties.getRatePlansEndpoint())
                .queryParam(RATE_PLAN_CODE, ratePlans)
                .build())
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new RatePlansException(OHIP_GET_RATEPLAN_DETAILS_EXCEPTION,
              "Error while trying to get rate plan details."));
        })
        .bodyToMono(RatePlansSummary.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "NegotiatedRatesForProfileIdCache", key = "#profileId")
  public Mono<NegotiatedRates> getNegotiatedRatesForProfileId(final String profileId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(
                    ratePlansOhipProperties.getNegotiatedRatesByProfileIdEndpoint())
                .build(profileId))
        .headers(httpHeaders -> httpHeaders.add(HUB_ID_HEADER, ratePlansOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new RatePlansException(OHIP_GET_NEGOTIATED_RATES_EXCEPTION,
              "Error while trying to get negotiated rates"));
        })
        .bodyToMono(NegotiatedRates.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "RatePlansInfoPromoCacheOhip", key = "#ratePlan + ',' + #hotelId")
  public RatePlanInfo getRatePlanInfo(String ratePlan, String hotelId) {
    return ohipWebClient.get().uri(
                      uriBuilder -> uriBuilder.path(
                                      ratePlansOhipProperties.getRatePlanPromoEndpoint())
                              .build(hotelId, ratePlan))
        .headers(httpHeaders -> {
          httpHeaders.add(HOTEL_ID_HEADER, hotelId);
          httpHeaders.add(RATE_PLAN_CODE, ratePlan);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new RatePlansException(OHIP_GET_RATEPLANINFO_DETAILS_EXCEPTION,
              "Error while trying to get rate plan info details."));
        })
        .bodyToMono(RatePlanInfo.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "PromotionCodeCacheOhip")
  public PropertyPromotionCodes getPromotionCode(
      List<String> promotionCode,
      String hotelId) {

    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(ratePlansOhipProperties.getPromotionCodeEndpoint())
            .queryParam(PROMOTION_CODE, promotionCode)
            .build(hotelId))
        .headers(headers ->
            headers.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorResponse -> {
          logErrorResponse(log, errorResponse);
          return Mono.error(new RatePlansException(
              OHIP_GET_PROMOTION_CODE_DETAILS_EXCEPTION,
              "The selected hotel is currently unavailable."));
        })
        .bodyToMono(PropertyPromotionCodes.class)
        .doOnError(exception ->
            ExceptionLogger.log(log, exception))
        .block();
  }
}