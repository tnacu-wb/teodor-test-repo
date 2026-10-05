package uk.co.whitbread.ohip.domain.model.roomallocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.Criteria;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.ReservationIdList;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HotelRoomsDetails;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoom;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.Housekeeping;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HousekeepingRoomInfo;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HousekeepingRoomStatus;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HousekeepingRooms;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskPreference;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskPreferenceCollection;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskRoom;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomCondition;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomConditionValue;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomLinks;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;

public class RoomAllocationTestUtils {

  public static VacantRoomResponse mockVacantRoom() {
    return VacantRoomResponse.builder()
        .hotelRoomsDetails(
            HotelRoomsDetails.builder().hotelId("HOTEL_ID").room(new ArrayList<>(Arrays.asList(
                KioskRoom.builder().floor("10").roomId("567")
                    .housekeeping(Housekeeping.builder().roomCondition(
                        RoomCondition.builder().roomConditionValue(
                            RoomConditionValue.builder().code("SS").description("Set as Single")
                                .build()).build()).build()).build()))).build()).build();
  }

  public static RoomAllocationRequest mockRoomAllocationRequest() {
    var criteria = Criteria.builder().hotelId("HOTEL_ID").roomId("ROOM_ID")
        .reservationIdList(List.of(
            ReservationIdList.builder().id("id1").type("type1").build(),
            ReservationIdList.builder().id("id2").type("type2").build())).build();
    return RoomAllocationRequest.builder().criteria(criteria).build();
  }

  public static RoomAllocationResponse mockRoomAllocationResponse() {
    return RoomAllocationResponse.builder()
        .links(List.of(RoomLinks.builder().operationId("OPERATION_ID").build())).build();
  }

  public static KioskReservationPreferences mockKioskReservationPreferences() {
    return KioskReservationPreferences.builder()
        .kioskPreferenceCollection(Collections.singletonList(KioskPreferenceCollection.builder()
            .kioskPreference(
                Arrays.asList(
                    KioskPreference.builder().preferenceValue("COTR").description("Cot Requested")
                        .build(),
                    KioskPreference.builder().preferenceValue("QUAD").description("Booked as Quad")
                        .build()))
            .preferenceType("SPECIAL REQUEST")
            .preferenceTypeDescription("Special Request")
            .build()))
        .build();
  }

  public static HouseKeepingRoomStatusResponse mockFetchHouseKeepingRoomStatus() {
    return HouseKeepingRoomStatusResponse.builder().housekeepingRoomInfo(
        HousekeepingRoomInfo.builder()
            .housekeepingRooms(
                HousekeepingRooms.builder().hotelId("MANOLD").room(Collections.singletonList(
                    HouseKeepingRoom.builder().roomId("007")
                        .housekeeping(Housekeeping.builder().housekeepingRoomStatus(
                            HousekeepingRoomStatus.builder().housekeepingRoomStatusText("Clean")
                                .build()).build()).build())).build())
            .build()).build();
  }

  public static Reservation mockGetReservationWithPreferenceResponse() {
    Reservation reservation = new Reservation();
    reservation.setReservations(new HotelReservationsType());
    reservation.getReservations()
        .setReservation(Collections.singletonList(new HotelReservationType()));
    reservation.getReservations().getReservation().get(0)
        .setPreferenceCollection(Collections.singletonList(new PreferenceTypeType()));
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .setPreference(Collections.singletonList(new PreferenceType()));
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .getPreference().get(0).setPreferenceValue("SING");
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .getPreference().get(0).setDescription("Booked as Single");
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .getPreference().get(0).setGlobal(false);
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .getPreference().get(0).setSource("R");
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .getPreference().get(0).setPreferenceId("preferenceId");
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .setPreferenceType("SPECIALS");
    reservation.getReservations().getReservation().get(0).getPreferenceCollection().get(0)
        .setPreferenceTypeDescription("Specials");
    return reservation;
  }
}
