package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service;

import static uk.co.whitbread.kiosk.infrastructure.rest.client.config.KioskConstants.HOTEL_ID;
import static uk.co.whitbread.kiosk.infrastructure.rest.client.config.KioskConstants.RESERVATION_ID;
import static uk.co.whitbread.kiosk.infrastructure.rest.client.config.KioskConstants.RESERVATION_IDS;
import static uk.co.whitbread.kiosk.infrastructure.rest.client.config.KioskConstants.ROOM_ID;
import static uk.co.whitbread.kiosk.infrastructure.rest.client.config.KioskConstants.ROOM_TYPE;
import static uk.co.whitbread.kiosk.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.kiosk.domain.model.checkin.in.KioskCheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.UpdateCommentRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.checkin.out.ReservationAmounts;
import uk.co.whitbread.kiosk.domain.model.roomallocation.in.AllocateRequest;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions.OhipAdapterException;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;

@Component
@Slf4j
public class OhipAdapterClient {

  private final WebClient ohipAdapterWebClient;
  private final OhipAdapterProperties ohipAdapterProperties;

  public OhipAdapterClient(
      @Qualifier("ohipAdapterWebClient") WebClient ohipAdapterWebClient,
      OhipAdapterProperties ohipAdapterProperties) {
    this.ohipAdapterWebClient = ohipAdapterWebClient;
    this.ohipAdapterProperties = ohipAdapterProperties;
  }

  public void createProfile(ProfileRequest createProfileRequest, String hotelId,
      String reservationNumber) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(RESERVATION_ID, reservationNumber);
    ohipAdapterWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getCreateProfileEndpoint())
            .queryParams(queryParams)
            .build())
        .body(Mono.just(createProfileRequest), ProfileRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(OhipAdapterException.class))
        .toBodilessEntity()
        .doOnError(e -> ExceptionLogger.log(log, e))
        .block();
  }

  public CheckInResponse doCheckIn(KioskCheckInRequest kioskCheckInRequest) {
    return ohipAdapterWebClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getCheckInEndpoint())
            .build())
        .body(Mono.just(kioskCheckInRequest), KioskCheckInRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(OhipAdapterException.class))
        .bodyToMono(CheckInResponse.class)
        .doOnError(e -> ExceptionLogger.log(log, e))
        .block();
  }

  public AllocationResponse allocateRooms(AllocateRequest allocateRequest) {
    return ohipAdapterWebClient
        .post()
        .uri(ohipAdapterProperties.getAllocateRoomEndpoint())
        .body(Mono.just(allocateRequest), AllocateRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(OhipAdapterException.class))
        .bodyToMono(AllocationResponse.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format(
                "Error while trying to allocate room for the reservationId = %s, roomId = %s",
                allocateRequest.getCriteria().getHotelId(),
                allocateRequest.getCriteria().getRoomId())))
        .block();
  }


  public VacantRoomResponse getVacantRooms(String hotelId, String roomType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(ROOM_TYPE, roomType);
    return ohipAdapterWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getGetVacantRoomsEndpoint())
            .queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .bodyToMono(VacantRoomResponse.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e,
                String.format("Error while trying to get vacant rooms for hotelId = %s", hotelId)))
        .block();
  }

  public HouseKeepingResponse fetchHouseKeepingStatus(String hotelId, String roomId) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(ROOM_ID, roomId);
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getHousekeepingStatusEndpoint())
            .queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .bodyToMono(HouseKeepingResponse.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e,
                String.format("Error while trying to fetch HouseKeeping Status for hotelId = %s and roomId = %s",
                    hotelId, roomId)))
        .block();
  }

  public KioskReservationPreferences fetchReservationPreferences(String hotelId,
      String reservationId) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(RESERVATION_ID, reservationId);
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getReservationPreferences())
            .queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .bodyToMono(KioskReservationPreferences.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format(
                "Error while trying to fetch Preferences for hotelId = %s and reservationId = %s",
                hotelId, reservationId)))
        .block();
  }


  public Mono<Object> updateReservationComments(String hotelId, String reservationId,
      UpdateCommentRequest updateCommentRequest) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(RESERVATION_ID, reservationId);
    return ohipAdapterWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getUpdateCommentsEndpoint())
            .queryParams(queryParams)
            .build())
        .body(Mono.just(updateCommentRequest), UpdateCommentRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .bodyToMono(Object.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format(
                "Error while trying to update comment for hotelId = %s and reservationId = %s",
                hotelId, reservationId)));
  }

  public void updateProfile(ProfileRequest updateProfileRequest, String hotelId,
      String reservationNumber) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(RESERVATION_ID, reservationNumber);
    ohipAdapterWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getUpdateProfileEndpoint())
            .queryParams(queryParams)
            .build())
        .body(Mono.just(updateProfileRequest), ProfileRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(OhipAdapterException.class))
        .toBodilessEntity()
        .doOnError(e -> ExceptionLogger.log(log, e))
        .block();
  }

  public ReservationAmounts getReservationAmounts(Set<String> reservationIds, String hotelId) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(RESERVATION_IDS, String.join(",", reservationIds.stream().toList()));
    queryParams.add(HOTEL_ID, hotelId);
    return ohipAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getReservationAmountsEndpoint())
            .queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipAdapterException.class);
        })
        .bodyToMono(ReservationAmounts.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e,
                String.format("Error while trying to get reservation amounts for hotelId = %s and reservationIds = %s",
                    hotelId, reservationIds)))
        .block();
  }
}
