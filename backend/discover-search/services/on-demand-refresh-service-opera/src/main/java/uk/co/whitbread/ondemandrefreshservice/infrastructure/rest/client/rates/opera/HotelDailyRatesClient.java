package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.opera;

import static uk.co.whitbread.ondemandrefreshservice.ErrorCode.OPERA_DAILY_RATES_EXCEPTION;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.END_DATE;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.LIMIT;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.START_DATE;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util.WebClientUtils.logErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.exceptions.DailyRatesException;

@RequiredArgsConstructor
@Slf4j
@Component
public class HotelDailyRatesClient {

  private final WebClient operaWebClient;
  private final OperaProperties operaProperties;


  public DailyRates getDailyRates(String hotelId, String ratePlanCode, long limit,
      String startDate, String endDate) {
    return operaWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(operaProperties.getDailyRatePlansEndpoint()).queryParam(LIMIT, limit)
            .queryParam(START_DATE, startDate).queryParam(END_DATE, endDate).build(hotelId, ratePlanCode))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId)).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new DailyRatesException(OPERA_DAILY_RATES_EXCEPTION,
              "Error while trying to get daily rates details."));
        })
        .bodyToMono(DailyRates.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}