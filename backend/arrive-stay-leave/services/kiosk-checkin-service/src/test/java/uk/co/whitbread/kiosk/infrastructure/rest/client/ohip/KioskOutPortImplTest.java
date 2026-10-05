package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.kiosk.ErrorCode;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.out.ReservationAmounts;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HotelRoomsDetails;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.Housekeeping;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskPreference;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskPreferenceCollection;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.Room;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.RoomCondition;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.RoomConditionValue;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions.RoomAllocationException;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.KioskCheckInRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.OhipProfileRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.CommentTypeProperties;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.PreferenceProperties;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.ReservationClient;

@ExtendWith(MockitoExtension.class)
class KioskOutPortImplTest {

  @Mock
  private KioskCheckInRequestMapper kioskCheckInRequestMapperMock;
  @Mock
  private OhipAdapterClient ohipAdapterClientMock;
  @Mock
  private ReservationClient reservationClientMock;
  @Mock
  private PreferenceProperties preferencePropertiesMock;
  @Mock
  private CommentTypeProperties commentTypeProperties;
  @Mock
  private OhipProfileRequestMapper ohipProfileRequestMapper;

  private static final String HOTEL_NAME = "TEST";
  private static final String RESERVATION_NUMBER = "ABCD122";
  private static final String ROOM_TYPE = "DOUBLE";

  @InjectMocks
  private KioskOutPortImpl kioskOutPort;

  @Test
  void createProfile_WhenOhipCreateProfileFails_ThenProfileExceptionIsThrown() {
    var profileRequestMock = mock(ProfileRequest.class);
    doThrow(new RuntimeException()).when(ohipAdapterClientMock).createProfile(eq(profileRequestMock), eq(HOTEL_NAME), eq(
        RESERVATION_NUMBER));

    Assertions.assertThrows(RuntimeException.class,
        () -> kioskOutPort.createProfile(profileRequestMock, HOTEL_NAME, RESERVATION_NUMBER));
  }

  @Test
  void doCheckin_WhenOhipCheckinFails_ThenProfileExceptionIsThrown() {
    var checkInRequestMock = mock(CheckInRequest.class);
    doThrow(new RuntimeException()).when(ohipAdapterClientMock).doCheckIn(any());

    Assertions.assertThrows(RuntimeException.class, () -> kioskOutPort.doCheckIn(checkInRequestMock));
  }

  @Test
  void confirmReservation_WhenOhipMakeDepositFails_ThenProfileExceptionIsThrown() {
    var confirmReservationRequestMock = mock(ConfirmReservationRequest.class);
    doThrow(new RuntimeException()).when(reservationClientMock).makeDeposit(any());

    Assertions.assertThrows(RuntimeException.class,
        () -> kioskOutPort.confirmReservation(confirmReservationRequestMock));
  }

  @Test
  void allocateRooms_WhenNoAvailableRooms_ThenRoomAllocationExceptionIsThrown() {
    var vacantRoomResponse = VacantRoomResponse.builder()
        .hotelRoomsDetails(
            HotelRoomsDetails.builder()
                .room(List.of(Room.builder()
                        .roomId("room1")
                        .housekeeping(Housekeeping.builder()
                            .roomCondition(RoomCondition.builder()
                                .roomConditionValue(RoomConditionValue.builder()
                                    .code("DIRTY")
                                    .build())
                                .build())
                            .build())
                    .build()))
                .build()
        ).build();
    var kioskReservationPreferences = KioskReservationPreferences
            .builder()
            .kioskPreferenceCollection(List.of(KioskPreferenceCollection.builder()
                    .kioskPreference(List.of(KioskPreference.builder()
                        .preferenceValue("DB")
                        .build()))
                .build()))
            .build();
    var myConditionsMap = new HashMap<String, String>();
    myConditionsMap.put("DB", "CLEAN");
    when(this.preferencePropertiesMock.getCondition()).thenReturn(myConditionsMap);

    var exception = Assertions.assertThrows(RoomAllocationException.class,
        () -> kioskOutPort.allocateRooms(HOTEL_NAME, RESERVATION_NUMBER, vacantRoomResponse,
            ROOM_TYPE, kioskReservationPreferences));

    assertEquals(ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION.getMessage(), exception.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void allocateRooms_WhenNoRoomConditioningInformationIsAvailable_ThenRoomAllocationExceptionIsThrown() {
    var vacantRoomResponse = VacantRoomResponse.builder()
        .hotelRoomsDetails(
            HotelRoomsDetails.builder()
                .room(List.of(Room.builder()
                    .roomId("room1")
                    .housekeeping(Housekeeping.builder()
                        .roomCondition(RoomCondition.builder()
                            .roomConditionValue(RoomConditionValue.builder()
                                .code("DIRTY")
                                .build())
                            .build())
                        .build())
                    .build()))
                .build()
        ).build();

    var exception = Assertions.assertThrows(RoomAllocationException.class,
        () -> kioskOutPort.allocateRooms(HOTEL_NAME, RESERVATION_NUMBER, vacantRoomResponse,
            ROOM_TYPE, null));

    assertEquals(ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION.getMessage(), exception.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void allocateRooms_WhenHotelRoomDetailsHasNoRooms_ThenRoomAllocationExceptionIsThrown() {
    var vacantRoomResponse = VacantRoomResponse.builder()
        .hotelRoomsDetails(
            HotelRoomsDetails.builder()
                .room(List.of())
                .build()
        ).build();

    var exception = Assertions.assertThrows(RoomAllocationException.class,
        () -> kioskOutPort.allocateRooms(HOTEL_NAME, RESERVATION_NUMBER, vacantRoomResponse,
            ROOM_TYPE, null));

    assertEquals(ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION.getMessage(), exception.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getOutstandingBalance_ReturnsValue() {

    when(ohipAdapterClientMock.getReservationAmounts(any(Set.class), anyString())).thenReturn(
        ReservationAmounts.builder().outStandingCostOfStay(
            BigDecimal.valueOf(50)).build());

    Optional<BigDecimal> outstandingBalance = kioskOutPort.getOutstandingBalance("312321", "FRAMTI");

    assertNotNull(outstandingBalance);
    assertTrue(outstandingBalance.isPresent());
    assertEquals(50, outstandingBalance.get().intValue());
  }

  @Test
  void getOutstandingBalance_ReturnsEmptyOptional() {

    when(ohipAdapterClientMock.getReservationAmounts(any(Set.class), anyString())).thenReturn(null);

    Optional<BigDecimal> outstandingBalance = kioskOutPort.getOutstandingBalance("312321", "FRAMTI");

    assertNotNull(outstandingBalance);
    assertFalse(outstandingBalance.isPresent());
  }
}