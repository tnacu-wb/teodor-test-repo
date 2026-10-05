package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.RoomAllocationOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.exception.RoomAllocationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.mapper.ReservationPreferenceMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip.OhipRoomAllocationClient;

@RequiredArgsConstructor
@Slf4j
public class RoomAllocationOutPortImpl implements RoomAllocationOutPort {

  private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
  private final OhipRoomAllocationClient ohipRoomAllocationClient;
  private final OhipReservationClient ohipReservationClient;
  private final ReservationPreferenceMapper reservationPreferenceMapper;

  @Override
  public VacantRoomResponse getVacantRoomIds(String hotelId, String roomType) {
    log.debug("Entered getVacant Rooms for hotelId={} and roomTYpe={}",
        sanitizeInput(hotelId), sanitizeInput(roomType));
    var vacantRoomResponse = ohipRoomAllocationClient.getVacantRoomIds(hotelId, roomType);
    log.debug("The Vacant Rooms are :: {}", vacantRoomResponse);
    return vacantRoomResponse;
  }

  @Override
  public RoomAllocationResponse allocateRoom(RoomAllocationRequest allocateRequest) {
    return ohipRoomAllocationClient.allocateRoom(allocateRequest);
  }

  @Override
  public HouseKeepingRoomStatusResponse fetchHouseKeepingRoomStatus(String hotelId, String roomId) {
    return ohipRoomAllocationClient.fetchHouseKeepingRoomStatus(hotelId, roomId);
  }

  @Override
  public KioskReservationPreferences fetchReservationWithPreference(String hotelId,
      String reservationId) {
    var reservationWithPreferences = ohipReservationClient.getReservationWithPreferences(hotelId,
        reservationId);
    return reservationPreferenceMapper.toModel(reservationWithPreferences);
  }

  private String sanitizeInput(String input) {
    return Optional.ofNullable(input)
        .map(in -> in.replaceAll(CONTROL_CHARACTER_REGEX, "")
            .replaceAll("[^A-Za-z0-9_-]", ""))
        .orElse("null");
  }
}
