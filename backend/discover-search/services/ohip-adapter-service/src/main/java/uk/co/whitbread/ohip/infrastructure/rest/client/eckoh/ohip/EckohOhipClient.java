package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip;

import static uk.co.whitbread.ohip.ErrorCode.OHIP_CHANGE_RESERVATION_ECKOH_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_RESERVATION_BY_BLOCKID_EXCEPYION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getOnStatusException;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getRetrySpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.exceptions.EckohException;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip.properties.EckohOhiPproperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;


@Component
@RequiredArgsConstructor
@Slf4j
public class EckohOhipClient {

  private final WebClient ohipWebClient;
  private final EckohOhiPproperties eckohOhiPproperties;

  private static final String SEARCH_TYPE = "Any";
  private static final String FETCH_INSTRUCTION = "Reservation";

  public ReservationsDetails getReservationsByBlockId(String blockId) {
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(eckohOhiPproperties.getExternalReservationEndpoint())
            .queryParam("searchType", SEARCH_TYPE)
            .queryParam("fetchInstructions", FETCH_INSTRUCTION)
            .queryParam("blockIds", blockId)
            .build()
        )
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HUB_ID_HEADER,
            eckohOhiPproperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new EckohException(OHIP_GET_RESERVATION_BY_BLOCKID_EXCEPYION,
              String.format(
                  "Error while trying to get reservation details for blockId %s", blockId)));
        })
        .bodyToMono(ReservationsDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();

  }

  public ChangeReservationDetails sendChangeReservationRequest(String hotelId,
      String reservationId,
      ChangeReservation changeReservation) {
    return ohipWebClient
        .put()
        .uri(uriBuilder ->
            uriBuilder.path(
                StringUtils.joinWith("/", eckohOhiPproperties.getReservationEndpoint(),
                    "{ReservationId}")).build(hotelId, reservationId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(changeReservation), ChangeReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new EckohException(OHIP_CHANGE_RESERVATION_ECKOH_EXCEPTION,
                String.format("Error while trying to change reservation for hotelId=%s and reservationId=%s",
                    hotelId, reservationId)), OHIP_CHANGE_RESERVATION_ECKOH_EXCEPTION))
        .bodyToMono(ChangeReservationDetails.class)
        .retryWhen(getRetrySpec(new EckohException(OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            String.format("Error while trying to change reservation for hotelId=%s and reservationId=%s. "
                    + "Max retries exhausted", hotelId, reservationId))))
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
