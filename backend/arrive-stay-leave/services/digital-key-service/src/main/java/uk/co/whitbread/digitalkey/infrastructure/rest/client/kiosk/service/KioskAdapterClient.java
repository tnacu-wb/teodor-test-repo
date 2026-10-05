package uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.digitalkey.domain.model.roomallocation.in.AllocateRequest;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.properties.KioskClientProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions.OhipAdapterException;

@Component
@Slf4j
public class KioskAdapterClient {

  private final WebClient kioskWebClient;
  private final KioskClientProperties kioskClientProperties;

  public KioskAdapterClient(@Qualifier("kioskWebClient") WebClient kioskWebClient,
                            KioskClientProperties kioskClientProperties) {
    this.kioskWebClient = kioskWebClient;
    this.kioskClientProperties = kioskClientProperties;
  }

  public AllocationResponseDto allocateRoom(RoomAllocationRequestDto allocateRequest) {
    return kioskWebClient
        .post()
        .uri(kioskClientProperties.getKioskAllocate())
        .body(Mono.just(allocateRequest), AllocateRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(OhipAdapterException.class))
        .bodyToMono(AllocationResponseDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format(
                "Error while trying to allocate room for the reservationId = %s, roomId = %s",
                allocateRequest.getHotelId(),
                allocateRequest.getRoomId())))
        .block();
  }

}