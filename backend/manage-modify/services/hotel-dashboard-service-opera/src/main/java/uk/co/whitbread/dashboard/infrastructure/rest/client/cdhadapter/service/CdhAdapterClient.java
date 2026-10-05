package uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.CdhReservationSearchDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.CdhAdapterException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdhAdapterClient {
  private final WebClient cdhAdapterWebClient;

  private final CdhAdapterProperties cdhAdapterProperties;

  public CdhReservationSearchDto retrieveBooking(final String reservationId) {
    return cdhAdapterWebClient.get()
        .uri(cdhAdapterProperties.getReservationByIdEndpoint(), reservationId)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(CdhAdapterException.class))
        .bodyToMono(CdhReservationSearchDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format("Error while trying to retrieve reservation from CDH "
                + "for reservationId = %s, ", reservationId)))
        .block();
  }
}
