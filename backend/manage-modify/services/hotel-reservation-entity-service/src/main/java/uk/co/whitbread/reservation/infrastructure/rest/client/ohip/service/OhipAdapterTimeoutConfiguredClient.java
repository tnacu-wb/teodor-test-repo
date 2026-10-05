package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
public class OhipAdapterTimeoutConfiguredClient {

  private final WebClient ohipAdapterTimeoutConfiguredWebClient;
  private final OhipAdapterProperties ohipAdapterProperties;

  public OhipAdapterTimeoutConfiguredClient(
      @Qualifier("ohipAdapterTimeoutConfiguredWebClient") WebClient ohipAdapterTimeoutConfiguredWebClient,
      OhipAdapterProperties ohipAdapterProperties) {
    this.ohipAdapterTimeoutConfiguredWebClient = ohipAdapterTimeoutConfiguredWebClient;
    this.ohipAdapterProperties = ohipAdapterProperties;
  }

  public PreCheckInResponse sendAddFileAttachmentToReservationRequest(
      ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto) {
    log.debug("Entered sendAddFileAttachmentToReservationRequest for reservation {}",
        reservationFileAttachmentRequestDto.getReservationId());
    return ohipAdapterTimeoutConfiguredWebClient.post()
        .uri(ohipAdapterProperties.getFileAttachmentEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(reservationFileAttachmentRequestDto),
            ReservationFileAttachmentRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelReservationOhipException.class);
        })
        .bodyToMono(PreCheckInResponse.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to add attachment for hotelId=%s and reservationId %s",
            reservationFileAttachmentRequestDto.getHotelId(),
            reservationFileAttachmentRequestDto.getReservationId())))
        .timeout(Duration.ofSeconds(5))
        .block();
  }
}
