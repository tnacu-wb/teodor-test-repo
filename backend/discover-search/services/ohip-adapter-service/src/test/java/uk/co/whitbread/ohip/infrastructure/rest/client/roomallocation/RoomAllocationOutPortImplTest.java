package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.*;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.roomallocation.RoomAllocationTestUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.exception.RoomAllocationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.mapper.ReservationPreferenceMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip.OhipRoomAllocationClient;

@ExtendWith(MockitoExtension.class)
class RoomAllocationOutPortImplTest {

  @InjectMocks
  private RoomAllocationOutPortImpl roomAllocationOutPort;

  @Mock
  private OhipRoomAllocationClient ohipRoomAllocationClient;
  @Mock
  private OhipReservationClient ohipReservationClient;
  @Mock
  private ReservationPreferenceMapper reservationPreferenceMapper;

  @Test
  void getVacantRoomIds__ShouldReturnOK() {
    //Arrange
    when(ohipRoomAllocationClient.getVacantRoomIds(anyString(), anyString())).thenReturn(
        RoomAllocationTestUtils.mockVacantRoom());

    //Act
    var vacantRoomResponse = roomAllocationOutPort.getVacantRoomIds("TestHotelId", "roomType");

    //Assert
    assertThat(vacantRoomResponse, notNullValue());
    assertThat(vacantRoomResponse.getHotelRoomsDetails().getRoom(), hasSize(1));
  }

  @Test
  void getVacantRoomIds__shouldThrowException() throws IOException {
    // Arrange
    String error = "Error while trying to get vacant rooms";
    Mockito.when(ohipRoomAllocationClient.getVacantRoomIds(anyString(), anyString()))
        .thenThrow(new RoomAllocationException(OHIP_GET_VACANT_ROOMS_EXCEPTION, error));
    // Act
    RoomAllocationException exception = Assertions
        .assertThrows(RoomAllocationException.class, () -> {
          roomAllocationOutPort.getVacantRoomIds("TestHotelId", "roomType");
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void allocateRoom__ShouldReturnOK() {
    //Arrange
    var request = RoomAllocationTestUtils.mockRoomAllocationRequest();
    when(ohipRoomAllocationClient.allocateRoom(any())).thenReturn(
        RoomAllocationTestUtils.mockRoomAllocationResponse());

    //Act
    var roomAllocationResponse = roomAllocationOutPort.allocateRoom(request);

    //Assert
    assertThat(roomAllocationResponse, notNullValue());
    assertThat(roomAllocationResponse.getLinks(), hasSize(1));
  }

  @Test
  void fetchHouseKeepingRoomStatus__ShouldReturnOK() {
    //Arrange
    when(ohipRoomAllocationClient.fetchHouseKeepingRoomStatus(anyString(), anyString())).thenReturn(
        RoomAllocationTestUtils.mockFetchHouseKeepingRoomStatus());

    //Act
    var houseKeepingRoomStatusResponse = roomAllocationOutPort.fetchHouseKeepingRoomStatus("MANOLD",
        "007");

    //Assert
    assertThat(houseKeepingRoomStatusResponse, notNullValue());
    assertThat(
        houseKeepingRoomStatusResponse.getHousekeepingRoomInfo().getHousekeepingRooms().getRoom(),
        hasSize(1));
  }

  @Test
  void fetchReservationWithPreference__ShouldReturnOK() {
    //Arrange
    when(ohipReservationClient.getReservationWithPreferences(anyString(), anyString())).thenReturn(
        RoomAllocationTestUtils.mockGetReservationWithPreferenceResponse());
    when(reservationPreferenceMapper.toModel(any())).thenReturn(
        RoomAllocationTestUtils.mockKioskReservationPreferences());

    //Act
    var kioskReservationPreferences = roomAllocationOutPort.fetchReservationWithPreference("MANOLD",
        "1234");

    //Assert
    assertThat(kioskReservationPreferences, notNullValue());
    assertThat(kioskReservationPreferences.getKioskPreferenceCollection(), hasSize(1));
  }

  @Test
  void fetchReservationWithPreference__shouldThrowException() {
    // Arrange
    String error = "Error while trying to get reservation with preference for " +
            "hotelId=MANOLD and reservationId=1234.";
    Mockito.when(ohipReservationClient.getReservationWithPreferences(anyString(), anyString()))
        .thenThrow(new RoomAllocationException(OHIP_RETRIVE_RESERVATION_PREFERENCES_EXCEPTION, error));
    // Act
    RoomAllocationException exception = Assertions
        .assertThrows(RoomAllocationException.class, () -> {
          roomAllocationOutPort.fetchReservationWithPreference("MANOLD", "1234");
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void allocateRoom__shouldThrowException() throws IOException {
    // Arrange
    String error = "Error while trying to allocate rooms.";
    Mockito.when(ohipRoomAllocationClient.allocateRoom(any()))
        .thenThrow(new RoomAllocationException(OHIP_ALLOCATE_ROOMS_EXCEPTION, error));
    // Act
    RoomAllocationException exception = Assertions
        .assertThrows(RoomAllocationException.class, () -> {
          roomAllocationOutPort.allocateRoom(RoomAllocationTestUtils.mockRoomAllocationRequest());
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void fetchHouseKeepingRoomStatus__shouldThrowException() throws IOException {
    // Arrange
    String error = "Error while trying to get House keeping room status.";
    Mockito.when(ohipRoomAllocationClient.fetchHouseKeepingRoomStatus(anyString(), anyString()
        ))
        .thenThrow(new RoomAllocationException(OHIP_HOUSEKEEPING_ROOM_STATUS_EXCEPTION, error));
    // Act
    RoomAllocationException exception = Assertions
        .assertThrows(RoomAllocationException.class, () -> {
          roomAllocationOutPort.fetchHouseKeepingRoomStatus("MANOLD", "007");
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }
}
