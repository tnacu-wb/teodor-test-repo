package uk.co.whitbread.kiosk.domain.ports.secondary;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ReservationComments;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.out.ConfirmReservationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;

public interface KioskOutPort {

  void createProfile(ProfileRequest createProfileRequest, String hotelId, String reservationNumber);

  CheckInResponse doCheckIn(CheckInRequest checkInRequest);

  ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest);

  AllocationResponse allocateRooms(String hotelId, String reservationId,
      VacantRoomResponse request, String roomType,
      KioskReservationPreferences reservationPreferencesResponse);

  VacantRoomResponse getVacantRooms(String hotelId, String roomType);

  HouseKeepingResponse fetchHouseKeepingStatus(String hotelId, String roomId);

  KioskReservationPreferences fetchReservationPreferences(String hotelId,
      String reservationId);

  void updateReservationComments(String hotelId,
      String reservationId, List<ReservationComments> reservationComments);

  void processProfileRequest(CheckInRequest checkInRequest);

  Optional<BigDecimal> getOutstandingBalance(String reservationId, String hotelId);
}
