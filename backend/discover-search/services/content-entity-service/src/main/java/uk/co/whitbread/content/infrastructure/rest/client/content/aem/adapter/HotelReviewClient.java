package uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.TRIPADVISOR_HOTEL_INFORMATION_EXCEPTION;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.HotelReviewResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.HotelReviewProperties;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TripAdvisorReviewsDto;


@RequiredArgsConstructor
@Builder
@Component
@Slf4j
public class HotelReviewClient {

  private final HotelReviewProperties hotelReviewProperties;
  private final WebClient hotelReviewWebClient;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "HotelReviewsCache")
  public TripAdvisorReviewsDto getTripAdvisorReviews(String hotelCode, String language) {
    var tripAdvisorReviewsDto = hotelReviewWebClient
        .get().uri(uriBuilder ->
            uriBuilder.path(hotelReviewProperties.getReviewsForSingleHotelEndpoint())
                .queryParam("lang", language)
                .build(hotelCode))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new HotelReviewResponseException(
                  TRIPADVISOR_HOTEL_INFORMATION_EXCEPTION,
                  String.format("hotelCode=%s not found in TripAdvisor API.", hotelCode),
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(TripAdvisorReviewsDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
    log.debug("tripAdvisorReviews : {}", tripAdvisorReviewsDto);
    return tripAdvisorReviewsDto;
  }
}


