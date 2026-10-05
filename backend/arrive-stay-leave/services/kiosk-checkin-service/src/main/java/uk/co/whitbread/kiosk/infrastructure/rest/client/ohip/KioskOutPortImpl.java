package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip;

import static uk.co.whitbread.kiosk.ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ReservationComments;
import uk.co.whitbread.kiosk.domain.model.checkin.in.UpdateCommentRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.checkin.out.ReservationAmounts;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.out.ConfirmReservationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.in.AllocateRequest;
import uk.co.whitbread.kiosk.domain.model.roomallocation.in.Criteria;
import uk.co.whitbread.kiosk.domain.model.roomallocation.in.ReservationIdList;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskPreference;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.Room;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.kiosk.domain.ports.secondary.KioskOutPort;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions.RoomAllocationException;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.KioskCheckInRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.OhipProfileRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.CommentTypeProperties;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.PreferenceProperties;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.ReservationClient;

@RequiredArgsConstructor
@Slf4j
public class KioskOutPortImpl implements KioskOutPort {

  private static final String NO_ROOMS_AVAILABLE_EXCEPTION = "There are no Rooms available for the roomType ";
  private final KioskCheckInRequestMapper kioskCheckInRequestMapper;
  private final OhipAdapterClient ohipAdapterClient;
  private final ReservationClient reservationClient;
  private final PreferenceProperties preferenceProperties;
  private final CommentTypeProperties commentTypeProperties;
  private final OhipProfileRequestMapper ohipProfileRequestMapper;

  @Override
  public void createProfile(ProfileRequest createProfileRequest, String hotelId,
      String reservationNumber) {
    ohipAdapterClient.createProfile(createProfileRequest, hotelId, reservationNumber);
  }

  @Override
  public CheckInResponse doCheckIn(CheckInRequest checkInRequest) {
    log.info("Inside Doing CheckIn");
    final var kioskCheckInRequest = kioskCheckInRequestMapper.toKioskCheckInRequestModel(
        checkInRequest);
    return ohipAdapterClient.doCheckIn(kioskCheckInRequest);
  }

  @Override
  public ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest) {
    return reservationClient.makeDeposit(confirmReservationRequest);
  }

  @Override
  public AllocationResponse allocateRooms(String hotelId, String reservationId,
      VacantRoomResponse vacantRoomResponse, String roomType,
      KioskReservationPreferences reservationPreferencesResponse) {
    if (vacantRoomResponse.getHotelRoomsDetails().getRoom() != null
        && !vacantRoomResponse.getHotelRoomsDetails().getRoom().isEmpty()) {
      String roomId;
      List<Room> roomList = vacantRoomResponse.getHotelRoomsDetails().getRoom();
      if (null != reservationPreferencesResponse
          && !reservationPreferencesResponse.getKioskPreferenceCollection().isEmpty()) {
        List<KioskPreference> kioskPreference = reservationPreferencesResponse.getKioskPreferenceCollection()
            .get(0).getKioskPreference();

        List<KioskPreference> cotsRequired = kioskPreference.stream()
            .filter(kioskPreference1 -> kioskPreference1.getPreferenceValue().contains("COTR"))
            .toList();

        List<String> stringList;

        if (cotsRequired.isEmpty()) {
          stringList = kioskPreference.stream()
              .map(kioskPreference1 -> preferenceProperties.getCondition()
                  .get(kioskPreference1.getPreferenceValue())).toList();
        } else {
          log.info("The Cots are requested.....");
          kioskPreference.removeIf(
              kioskPreference1 -> kioskPreference1.getPreferenceValue().contains("COTR"));

          stringList = kioskPreference.stream()
              .map(kioskPreference1 -> preferenceProperties.getCondition().get(String.join("",
                  kioskPreference1.getPreferenceValue(), "COTR"))).toList();
        }

        log.info("The String List is :: {}", stringList);
        List<String> roomIdList = roomList.stream()
            .filter(room -> null != room.getHousekeeping().getRoomCondition())
            .filter(room -> stringList.contains(
                room.getHousekeeping().getRoomCondition().getRoomConditionValue().getCode()))
            .map(Room::getRoomId)
            .toList();

        if (roomIdList.isEmpty()) {
          RoomAllocationException exception =
              new RoomAllocationException(KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION,
                  NO_ROOMS_AVAILABLE_EXCEPTION + roomType + " With Preferences");
          ExceptionLogger.log(log, exception);
          throw exception;
        } else {
          log.info("The RoomId list is :: {}", roomIdList);
          roomId = roomIdList.get(0);
        }
      } else {
        List<String> nonConditionalRooms = roomList.stream()
            .filter(room -> null == room.getHousekeeping().getRoomCondition())
            .map(Room::getRoomId)
            .toList();
        if (nonConditionalRooms.isEmpty()) {
          RoomAllocationException exception = new RoomAllocationException(KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION,
              NO_ROOMS_AVAILABLE_EXCEPTION + roomType);
          ExceptionLogger.log(log, exception);
          throw exception;
        } else {
          roomId = nonConditionalRooms.get(0);
        }
      }
      final var allocateRoomRequest = buildAllocateRequest(hotelId, reservationId, roomId);
      AllocationResponse allocationResponse = ohipAdapterClient.allocateRooms(allocateRoomRequest);
      allocationResponse.setRoomId(roomId);
      return allocationResponse;
    } else {
      RoomAllocationException exception = new RoomAllocationException(KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION,
          NO_ROOMS_AVAILABLE_EXCEPTION + roomType);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Override
  public VacantRoomResponse getVacantRooms(String hotelId, String roomType) {
    return ohipAdapterClient.getVacantRooms(hotelId, roomType);
  }

  @Override
  public HouseKeepingResponse fetchHouseKeepingStatus(String hotelId, String roomId) {
    return ohipAdapterClient.fetchHouseKeepingStatus(hotelId, roomId);
  }

  @Override
  public KioskReservationPreferences fetchReservationPreferences(String hotelId,
      String reservationId) {
    return ohipAdapterClient.fetchReservationPreferences(hotelId, reservationId);
  }

  @Override
  public void updateReservationComments(String hotelId,
      String reservationId, List<ReservationComments> reservationComments) {
    for (ReservationComments reservationComment : reservationComments) {
      ohipAdapterClient.updateReservationComments(hotelId, reservationId,
          prepareUpdateCommentRequest(reservationComment)).block();
    }
  }

  @Override
  public void processProfileRequest(CheckInRequest checkInRequest) {
    var stayingGuestDetailsList = checkInRequest.getStayingGuestDetails();
    if (stayingGuestDetailsList != null) {
      if (!stayingGuestDetailsList.isEmpty()) {
        var updateProfileRequest = ohipProfileRequestMapper.toKioskUpdateProfileRequestModel("",
            Collections.singletonList(stayingGuestDetailsList.get(0)));
        ohipAdapterClient.updateProfile(updateProfileRequest, checkInRequest.getHotelId(),
            checkInRequest.getReservationNumber());
        stayingGuestDetailsList.remove(0);
        if (!stayingGuestDetailsList.isEmpty()) {
          final var createProfileRequest = ohipProfileRequestMapper.toKioskUpdateProfileRequestModel(
              "",
              checkInRequest.getStayingGuestDetails());
          ohipAdapterClient.createProfile(createProfileRequest, checkInRequest.getHotelId(),
              checkInRequest.getReservationNumber());
        }
      }
    }
  }

  @Override
  public Optional<BigDecimal> getOutstandingBalance(String reservationId, String hotelId) {
    ReservationAmounts reservationAmounts = ohipAdapterClient.getReservationAmounts(Set.of(reservationId), hotelId);
    if (reservationAmounts != null) {
      return Optional.ofNullable(reservationAmounts.getOutStandingCostOfStay());
    }
    return Optional.empty();
  }

  private UpdateCommentRequest prepareUpdateCommentRequest(
      ReservationComments reservationComments) {
    return UpdateCommentRequest.builder()
        .textValue(reservationComments.getComment())
        .commentTitle(reservationComments.getCommentType())
        .type(commentTypeProperties.getType()
            .get(reservationComments.getCommentType().replaceAll("\\s", "")))
        .build();
  }

  private AllocateRequest buildAllocateRequest(String hotelId, String reservationId,
      String roomId) {
    List<ReservationIdList> reservationIdLists = new ArrayList<>();
    reservationIdLists.add(
        ReservationIdList.builder().id(reservationId).type("Reservations").build());
    return AllocateRequest.builder().criteria(
        Criteria.builder().hotelId(hotelId).roomNumberLocked(true)
            .reservationIdList(reservationIdLists).roomId(roomId).build()).build();
  }

}
