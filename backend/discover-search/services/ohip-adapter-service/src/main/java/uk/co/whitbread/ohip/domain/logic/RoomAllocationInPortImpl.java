package uk.co.whitbread.ohip.domain.logic;

import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.ohip.domain.ports.primary.RoomAllocationInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RoomAllocationOutPort;

public class RoomAllocationInPortImpl implements RoomAllocationInPort {

  private final RoomAllocationOutPort roomAllocationOutPort;

  public RoomAllocationInPortImpl(RoomAllocationOutPort roomAllocationOutPort) {
    this.roomAllocationOutPort = roomAllocationOutPort;
  }

  @Override
  public VacantRoomResponse getVacantRoomIds(String hotelId, String roomType) {
    return roomAllocationOutPort.getVacantRoomIds(hotelId, roomType);
  }

  @Override
  public RoomAllocationResponse allocateRoom(RoomAllocationRequest allocateRequest) {
    return roomAllocationOutPort.allocateRoom(allocateRequest);
  }

  @Override
  public HouseKeepingRoomStatusResponse fetchHouseKeepingRoomStatus(String hotelId, String roomId) {
    return roomAllocationOutPort.fetchHouseKeepingRoomStatus(hotelId, roomId);
  }

  @Override
  public KioskReservationPreferences fetchReservationWithPreference(String hotelId,
      String reservation) {
    return roomAllocationOutPort.fetchReservationWithPreference(hotelId, reservation);
  }
}
