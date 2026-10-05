package uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.out.ConfirmReservationResponse;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.exception.HotelReservationException;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.properties.ReservationProperties;

@Component
@Slf4j
public class ReservationClient {

  private final WebClient reservationWebClient;
  private final ReservationProperties reservationProperties;

  public ReservationClient(
      @Qualifier("reservationWebClient") WebClient reservationWebClient,
      ReservationProperties reservationProperties) {
    this.reservationWebClient = reservationWebClient;
    this.reservationProperties = reservationProperties;
  }

  public ConfirmReservationResponse makeDeposit(
      ConfirmReservationRequest confirmReservationRequest) {
    return reservationWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(reservationProperties.getConfirmReservation())
            .build())
        .body(Mono.just(confirmReservationRequest), ConfirmReservationRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(HotelReservationException.class))
        .bodyToMono(ConfirmReservationResponse.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, "Error while trying to make deposit"))
        .block();
  }

}
