package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;


public interface RoomAllocationInPort {

  VacantRoomResponse getVacantRoomIds(String hotelId, String roomType);

  RoomAllocationResponse allocateRoom(RoomAllocationRequest allocateRequest);

  HouseKeepingRoomStatusResponse fetchHouseKeepingRoomStatus(String hotelId, String roomId);

  KioskReservationPreferences fetchReservationWithPreference(String hotelId,
      String reservation);
}
