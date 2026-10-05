package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.raterestriction.opera;

import static uk.co.whitbread.ondemandrefreshservice.ErrorCode.OPERA_HOTEL_RATE_RESTRICTION_EXCEPTION;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util.WebClientUtils.logErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.raterestriction.exceptions.RateRestrictionException;

@RequiredArgsConstructor
@Slf4j
@Component
public class HotelRateRestrictionClient {

  private final WebClient operaWebClient;
  private final OperaProperties operaProperties;


  public RateRestrictionResponse searchRateRestrictionCriteria(String hotelId, String startDate, String endDate) {
    return operaWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(operaProperties.getSearchRateRestrictionCriteriaEndpoint())
            .queryParam("hotelId", hotelId).queryParam("restrictionSearchCriteriaStartDate", startDate)
            .queryParam("end", endDate).build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId)).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new RateRestrictionException(OPERA_HOTEL_RATE_RESTRICTION_EXCEPTION,
              "Error while trying to get rate restriction details."));
        })
        .bodyToMono(RateRestrictionResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}