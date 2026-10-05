package uk.co.whitbread.review.infrastructure.rest.client.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.review.infrastructure.rest.client.review.exceptions.ErrorCode;
import uk.co.whitbread.review.infrastructure.rest.client.review.exceptions.HotelReviewServiceException;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorData;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReviews;
import uk.co.whitbread.review.infrastructure.rest.client.review.properties.TripAdvisorProperties;
import uk.co.whitbread.review.infrastructure.rest.utils.WebClientUtils;


@Component
@Slf4j
@RequiredArgsConstructor
public class TripAdvisorClient {

  private static final String TRIPADVISOR_DATA_EXCEPTION_MSG =
      "Error while trying to get trip data. hotelCode=%s not found in TripAdvisor API";

  private static final String TRIPADVISOR_REVIEWS_EXCEPTION_MSG =
      "Error while trying to get reviews. hotelCode=%s not found in TripAdvisor API";

  private final TripAdvisorProperties tripAdvisorProperties;
  private final WebClient tripAdvisorWebClient;

  public TripAdvisorData getTripAdvisorData(String hotelCode, String lang, Integer limit) {
    log.debug("Invoke Client DATA - getTripAdvisorData : {} hotelCode={}, lang={}, limit={}",
        tripAdvisorProperties.getDataUrl(), hotelCode, lang, limit);
    return tripAdvisorWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(tripAdvisorProperties.getDataUrl())
            .queryParam("key", tripAdvisorProperties.getApiKey())
            .queryParam("lang", lang)
            .queryParam("limit", limit)
            .build(hotelCode))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReviewServiceException(
              ErrorCode.TRIPADVISOR_DATA_EXCEPTION,
              String.format(TRIPADVISOR_DATA_EXCEPTION_MSG, hotelCode)));
        })
        .bodyToMono(TripAdvisorData.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public TripAdvisorReviews getTripAdvisorReviews(String hotelCode, String lang, Integer limit) {
    log.debug("Invoke Client REVIEWS  - getTripAdvisorReviews : {} hotelCode={}, lang={}, limit={}",
        tripAdvisorProperties.getReviewsUrl(), hotelCode, lang, limit);
    return tripAdvisorWebClient
        .get().uri(uriBuilder -> uriBuilder.path(tripAdvisorProperties.getReviewsUrl())
            .queryParam("key", tripAdvisorProperties.getApiKey())
            .queryParam("lang", lang)
            .queryParam("limit", limit)
            .build(hotelCode))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new HotelReviewServiceException(ErrorCode.TRIPADVISOR_REVIEWS_EXCEPTION,
                  String.format(TRIPADVISOR_REVIEWS_EXCEPTION_MSG, hotelCode)));
        })
        .bodyToMono(TripAdvisorReviews.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
