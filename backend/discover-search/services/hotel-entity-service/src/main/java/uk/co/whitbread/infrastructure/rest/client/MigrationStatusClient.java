package uk.co.whitbread.infrastructure.rest.client;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.infrastructure.config.MigrationStatusProperties;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.exception.MigrationStatusException;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.in.MigrationStatusRequest;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.out.MigrationStatus;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.out.MigrationStatusResponse;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class MigrationStatusClient {

  private final WebClient migrationStatusWebClient;

  private final MigrationStatusProperties migrationStatusProperties;

  /**
   * Retrieving the status of the onSale flag from ohip-adapter now instead of the hotel-migration-status-service.
   *
   * @deprecated when implementing DNRQ-87378 for decommissioning the hotel-migration-status-service
   */
  @Deprecated(since = "implementing DNRQ-87378", forRemoval = true)
  public MigrationStatus getMigrationStatusResponse(
      MigrationStatusRequest migrationStatusRequest) {
    log.debug(
        "Entered getMigrationStatusResponse with migrationStatusRequest={}",
        migrationStatusRequest);

    var migrationResponse = migrationStatusWebClient.get().uri(uriBuilder -> uriBuilder
            .path(migrationStatusProperties.getMigrationStatusEndpoint())
            .queryParam("hotelIds",
                String.join(",", migrationStatusRequest.getHotelIds()))
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            WebClientUtils.logErrorResponse(log, clientResponse)
                .then(Mono.error(new MigrationStatusException(ErrorCode.MIGRATION_STATUS_EXCEPTION,
                    "Migration status returned \"Invalid Hotel Id\" error message!")))
        )
        .bodyToMono(MigrationStatusResponse[].class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to create migration status response for migrationStatusRequest=%s",
            migrationStatusRequest)))
        .block();
    return new MigrationStatus(Arrays.asList(migrationResponse));
  }
}
