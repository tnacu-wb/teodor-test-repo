package uk.co.whitbread.kiosk.domain.ports.primary;

import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;

public interface KioskInPort {

  CheckInResponse getCheckInResponse(CheckInRequest checkInRequest);

  ProfileRequest createProfileRequest(CheckInRequest checkInRequest);

  AllocationResponse allocateRooms(String hotelId, String reservationId,
      VacantRoomResponse vacantRoomResponse, String roomType,
      KioskReservationPreferences reservationPreferencesResponse);

  VacantRoomResponse getVacantRooms(String hotelId, String roomType);

  HouseKeepingResponse fetchHouseKeepingStatus(String hotelId, String roomId);

  KioskReservationPreferences fetchReservationPreferences(String hotelId, String roomId);
}
