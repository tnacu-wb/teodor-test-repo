package uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.AmendReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.HotelReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.properties.ReservationsClientProperties;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmAmendRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationByBasketRefResponseDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationsClient {

  private static final String ERROR_WAS_RETURNED_BY_RESERVATION_SERVICE =
      "An error was returned by Reservation Service!";
  private static final String TIMEOUT_EXCEPTION = "timeout exception has occurred: ";
  private static final String FAILED_TO_SEND_REQUEST_TO_SERVICE = "failed to send request to service";
  private final WebClient reservationsWebClient;
  private final ReservationsClientProperties reservationsClientProperties;
  private final Duration duration = Duration.ofSeconds(30);

  public ConfirmReservationResponseDto confirmReservation(
      final ConfirmReservationRequestDto confirmReservationRequest) {
    return reservationsWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationsClientProperties.getConfirmReservationEndpoint()).build())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(confirmReservationRequest), ConfirmReservationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(ConfirmReservationResponseDto.class)
        .timeout(duration)
        .onErrorMap(TimeoutException.class, ex -> {
          log.error("ConfirmReservation: timeout exception has occurred: " + ex.getMessage());
          return new HttpTimeoutException("ReadTimeout");
        })
        .doOnError(exception ->
            ExceptionLogger.log(log, exception,
                String.format("ConfirmReservation: %s. %s", FAILED_TO_SEND_REQUEST_TO_SERVICE,
                    ERROR_WAS_RETURNED_BY_RESERVATION_SERVICE)))
        .block();
  }

  public CancelReservationResponseDto cancelReservation(
      final CancelReservationRequestDto cancelReservationRequest) {
    return reservationsWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(reservationsClientProperties.getCancelReservationEndpoint()).build())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(cancelReservationRequest), CancelReservationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(CancelReservationResponseDto.class)
        .timeout(duration)
        .onErrorMap(TimeoutException.class, ex -> {
          log.error(String.format("CancelReservation: %s, %s", TIMEOUT_EXCEPTION, ex.getMessage()));
          return new HttpTimeoutException("ReadTimeout");
        })
        .doOnError(exception ->
            ExceptionLogger.log(log, exception,
                String.format("CancelReservation: %s. %s", FAILED_TO_SEND_REQUEST_TO_SERVICE,
                    ERROR_WAS_RETURNED_BY_RESERVATION_SERVICE)))
        .block();
  }

  public ReservationByBasketRefResponseDto confirmAmend(
      final ConfirmAmendRequestDto confirmAmendRequestDto) {
    return reservationsWebClient
        .put()
        .uri(uriBuilder ->
            uriBuilder.path(reservationsClientProperties.getAmendReservationEndpoint()).build())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(confirmAmendRequestDto), ConfirmAmendRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(AmendReservationException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .timeout(duration)
        .onErrorMap(TimeoutException.class, ex -> {
          log.error("ConfirmAmend: timeout exception has occurred: " + ex.getMessage());
          return new HttpTimeoutException("ReadTimeout");
        })
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }
}