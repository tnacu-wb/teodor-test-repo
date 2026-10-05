package uk.co.whitbread.digitalkey.infrastructure.rest.controller.checkin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.primary.CheckInPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.CheckInController;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.CheckInRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.CheckInResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByIdDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.RoomStayByIdDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.DK_ISSUED;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.SUCCESS;

@ExtendWith(MockitoExtension.class)
class CheckInControllerTest {

  @Mock
  private CheckInPort checkInPort;

  @InjectMocks
  private CheckInController checkInController;

  @Mock
  private CharacterUdfInPort characterUdfInPort;

    @Test
    void checkInController_inHouse_returnsSuccessAndSkipsAllocateAndCheckIn() {
        // Arrange
        String hotelId = "LON01";
        String reservationId = "RES-001";
        String roomType = "DBL";
        String roomNumber = "101";

        CheckInRequestDto request = CheckInRequestDto.builder()
                .hotelId(hotelId)
                .reservationId(reservationId)
                .build();

        ReservationByIdDto reservation = mock(ReservationByIdDto.class);
        RoomStayByIdDto roomStay = mock(RoomStayByIdDto.class);

        when(checkInPort.getReservation(hotelId, reservationId)).thenReturn(reservation);
        when(reservation.getReservationStatus()).thenReturn("inhouse");
        when(reservation.getRoomStay()).thenReturn(roomStay);
        when(roomStay.getRoomType()).thenReturn(roomType);
        when(roomStay.getRoomNumber()).thenReturn(roomNumber);

        // Act
        CheckInResponseDto response = checkInController.checkInEndPoint(request);

        // Assert
        assertNotNull(response);
        assertEquals(roomNumber, response.getRoomNumber());
        assertEquals(SUCCESS, response.getCheckInStatus());

        verify(checkInPort).getReservation(hotelId, reservationId);
        verify(characterUdfInPort).updateUdfc20(reservationId, hotelId, DK_ISSUED);
        // Ensure no further downstream calls were made on INHOUSE path
        verifyNoMoreInteractions(checkInPort, characterUdfInPort);
    }

    @Test
    void checkInController_notInHouse_allocatesRoom_thenChecksIn_andReturnsResult() {
        // Arrange
        String hotelId = "LON01";
        String reservationId = "RES-002";
        String roomType = "DBL";
        String currentRoomInReservation = "000";
        String allocatedRoom = "105";

        CheckInRequestDto request = CheckInRequestDto.builder()
                .hotelId(hotelId)
                .reservationId(reservationId)
                .build();

        ReservationByIdDto reservation = mock(ReservationByIdDto.class);
        RoomStayByIdDto roomStay = mock(RoomStayByIdDto.class);

        when(checkInPort.getReservation(hotelId, reservationId)).thenReturn(reservation);
        when(reservation.getReservationStatus()).thenReturn("BOOKED");
        when(reservation.getRoomStay()).thenReturn(roomStay);
        when(roomStay.getRoomType()).thenReturn(roomType);
        when(roomStay.getRoomNumber()).thenReturn(currentRoomInReservation);

        when(checkInPort.allocateRoom(hotelId, reservationId, roomType, currentRoomInReservation))
                .thenReturn(allocatedRoom);

        CheckInResponseDto expected = CheckInResponseDto.builder()
                .roomNumber(allocatedRoom)
                .checkInStatus(SUCCESS)
                .build();

        when(checkInPort.checkIn(reservationId, hotelId, allocatedRoom)).thenReturn(expected);

        // Act
        CheckInResponseDto actual = checkInController.checkInEndPoint(request);

        // Assert
        assertSame(expected, actual);
        verify(checkInPort).getReservation(hotelId, reservationId);
        verify(checkInPort).allocateRoom(hotelId, reservationId, roomType, currentRoomInReservation);
        verify(checkInPort).checkIn(reservationId, hotelId, allocatedRoom);
        verifyNoMoreInteractions(checkInPort);
    }

}