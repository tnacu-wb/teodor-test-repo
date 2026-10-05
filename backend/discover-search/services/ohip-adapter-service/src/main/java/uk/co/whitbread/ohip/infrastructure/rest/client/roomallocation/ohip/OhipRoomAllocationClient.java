package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip;

import static uk.co.whitbread.ohip.ErrorCode.OHIP_ALLOCATE_ROOMS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_VACANT_ROOMS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_HOUSEKEEPING_ROOM_STATUS_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ROOMS_STATUS;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ROOM_FRONT_OFFICE_STATUS;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.INCLUDE_ALL_ROOM_CONDITIONS;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LIMIT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_ID_TEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.exception.RoomAllocationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip.properties.RoomAllocationOhipProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipRoomAllocationClient {

  private final WebClient ohipWebClient;
  private final RoomAllocationOhipProperties roomAllocationOhipProperties;

  public VacantRoomResponse getVacantRoomIds(String hotelId, String roomType) {

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(ROOM_TYPE, roomType);
    queryParams.add(HOTEL_ROOMS_STATUS, "Clean");
    queryParams.add(HOTEL_ROOM_FRONT_OFFICE_STATUS, "Vacant");
    queryParams.add(INCLUDE_ALL_ROOM_CONDITIONS, "true");
    queryParams.add(LIMIT, "60");
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(roomAllocationOhipProperties.getVacantRoomsEndpoint())
            .queryParams(queryParams)
            .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new RoomAllocationException(OHIP_GET_VACANT_ROOMS_EXCEPTION,
              String.format("Error while trying to get vacant rooms for hotelId =  %s",
                  hotelId)));
        })
        .bodyToMono(VacantRoomResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RoomAllocationResponse allocateRoom(RoomAllocationRequest allocateRequest) {

    String hotelId = allocateRequest.getCriteria().getHotelId();
    String roomId = allocateRequest.getCriteria().getRoomId();
    return ohipWebClient.post()
        .uri(
            uriBuilder -> uriBuilder.path(roomAllocationOhipProperties.getRoomsAssignmentEndpoint())
                .build(hotelId,
                    allocateRequest.getCriteria().getReservationIdList().get(0).getId()))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(allocateRequest), RoomAllocationRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new RoomAllocationException(OHIP_ALLOCATE_ROOMS_EXCEPTION,
              String.format(
                  "Error while trying to allocate room for the reservationId = %s, roomId = %s",
                  hotelId, roomId)));
        })
        .bodyToMono(RoomAllocationResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public HouseKeepingRoomStatusResponse fetchHouseKeepingRoomStatus(String hotelId, String roomId) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(ROOM_ID_TEXT, roomId);
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(roomAllocationOhipProperties.getGetHousekeeping())
            .queryParams(queryParams)
            .build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new RoomAllocationException(OHIP_HOUSEKEEPING_ROOM_STATUS_EXCEPTION,
              String.format(
                  "Error while trying to get House keeping room status for hotelId = %s",
                  hotelId)));
        })
        .bodyToMono(HouseKeepingRoomStatusResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
