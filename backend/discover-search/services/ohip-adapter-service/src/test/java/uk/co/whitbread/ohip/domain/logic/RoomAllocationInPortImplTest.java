package uk.co.whitbread.ohip.domain.logic;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.roomallocation.RoomAllocationTestUtils;
import uk.co.whitbread.ohip.domain.ports.secondary.RoomAllocationOutPort;

@ExtendWith(MockitoExtension.class)
class RoomAllocationInPortImplTest {

  @InjectMocks
  private RoomAllocationInPortImpl roomAllocationInPort;

  @Mock
  private RoomAllocationOutPort roomAllocationOutPort;

  @Test
  void getVacantRoomIds__ShouldReturnOK() {
    //Arrange
    when(roomAllocationOutPort.getVacantRoomIds(anyString(), anyString())).thenReturn(
        RoomAllocationTestUtils.mockVacantRoom());

    //Act
    var response = roomAllocationInPort.getVacantRoomIds("HOTEL_ID", "DOUBLE");

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getHotelRoomsDetails());
    assertEquals("567", response.getHotelRoomsDetails().getRoom().get(0).getRoomId());

    verifyNoMoreInteractions(roomAllocationOutPort);

  }

  @Test
  void fetchReservationWithPreference__ShouldReturnOK() {
    //Arrange
    when(roomAllocationOutPort.fetchReservationWithPreference(anyString(), anyString())).thenReturn(
        RoomAllocationTestUtils.mockKioskReservationPreferences());

    //Act
    var response = roomAllocationInPort.fetchReservationWithPreference("HOTEL_ID", "1234");

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getKioskPreferenceCollection());
    assertEquals("COTR", response.getKioskPreferenceCollection().get(0).getKioskPreference().get(0)
        .getPreferenceValue());

    verifyNoMoreInteractions(roomAllocationOutPort);

  }

  @Test
  void allocateRoom__ShouldReturnOK() {
    //Arrange
    var request = RoomAllocationTestUtils.mockRoomAllocationRequest();
    when(roomAllocationOutPort.allocateRoom(any())).thenReturn(
        RoomAllocationTestUtils.mockRoomAllocationResponse());

    //Act
    var response = roomAllocationInPort.allocateRoom(request);

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getLinks());
    assertEquals("OPERATION_ID", response.getLinks().get(0).getOperationId());

    verifyNoMoreInteractions(roomAllocationOutPort);

  }

  @Test
  void fetchHouseKeepingRoomStatus__ShouldReturnOK() {
    //Arrange
    when(roomAllocationOutPort.fetchHouseKeepingRoomStatus(anyString(), anyString())).thenReturn(
        RoomAllocationTestUtils.mockFetchHouseKeepingRoomStatus());

    //Act
    var houseKeepingRoomStatusResponse = roomAllocationInPort.fetchHouseKeepingRoomStatus("MANOLD",
        "007");

    //Assert
    MatcherAssert.assertThat(houseKeepingRoomStatusResponse, Matchers.notNullValue());
    MatcherAssert.assertThat(
        houseKeepingRoomStatusResponse.getHousekeepingRoomInfo().getHousekeepingRooms().getRoom(),
        hasSize(1));
  }
}
