package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.migration.opera;

import static uk.co.whitbread.ondemandrefreshservice.ErrorCode.OPERA_HOTEL_MIGRATION_STATUS_EXCEPTION;
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
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.migrationstatus.OperaHotelDetailsList;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.migration.exceptions.MigrationStatusException;

@RequiredArgsConstructor
@Slf4j
@Component
public class HotelMigrationStatusClient {

  private final WebClient operaWebClient;
  private final OperaProperties operaProperties;

  public OperaHotelDetailsList getHotelMigrationStatus(String hotelId) {
    return operaWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(operaProperties.getHotelMigrationStatusEndpoint()).build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId)).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new MigrationStatusException(OPERA_HOTEL_MIGRATION_STATUS_EXCEPTION,
              "Error while trying to get migrations status details."));
        })
        .bodyToMono(OperaHotelDetailsList.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}