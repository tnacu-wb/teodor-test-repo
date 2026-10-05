package uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip;

import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_CHECKIN_DETAILS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_UPDATE_COMMENTS_RESERVATION_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getOnStatusException;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getRetrySpec;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskChangeReservation;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.exception.CheckInException;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip.properties.CheckInOhipProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipCheckInClient {

  private final WebClient ohipWebClient;
  private final CheckInOhipProperties checkInOhipProperties;

  public CheckInResponse getCheckInResponse(CheckInRequest checkInRequest, String hotelId,
      String reservationId) {

    return ohipWebClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(checkInOhipProperties.getCheckInEndpoint())
            .build(hotelId, reservationId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(checkInRequest), CheckInRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new CheckInException(OHIP_GET_CHECKIN_DETAILS_EXCEPTION,
              "Error while trying to get checkIn details"));
        })
        .bodyToMono(CheckInResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }


  public void sendKioskChangeReservationRequest(String hotelId,
      String reservationId,
      KioskChangeReservation changeReservation) {

    ohipWebClient
        .put()
        .uri(uriBuilder -> uriBuilder
            .path(StringUtils.joinWith("/", checkInOhipProperties.getReservationEndpoint(),
                "{ReservationId}"))
            .build(hotelId, reservationId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(changeReservation), KioskChangeReservation.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            getOnStatusException(log, response, new CheckInException(OHIP_UPDATE_COMMENTS_RESERVATION_EXCEPTION,
                "Error while trying to update Comments to reservation"), OHIP_UPDATE_COMMENTS_RESERVATION_EXCEPTION))
        .toBodilessEntity()
        .retryWhen(getRetrySpec(new CheckInException(OHIP_RETRIES_EXHAUSTED_EXCEPTION,
            "Error while trying to update Comments to reservation. Max retries exhausted")))
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
