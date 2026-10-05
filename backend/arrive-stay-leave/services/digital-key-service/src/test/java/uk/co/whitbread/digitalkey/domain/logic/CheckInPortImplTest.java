package uk.co.whitbread.digitalkey.domain.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.*;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.digitalkey.ErrorCode;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CurrentRoomInfo;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.Reservation;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.RoomStay;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.primary.CheckInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CheckOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions.CheckInRequestException;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.CheckInResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByIdDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.RoomStayByIdDto;

/**
 * Unit tests for KioskInPortImpl.
 */
@ExtendWith(MockitoExtension.class)
class CheckInPortImplTest {

    @Mock
    private CheckOutPort checkOutPort;

    @Mock
    private CharacterUdfInPort characterUdfInPort;

    private CheckInPort impl;

    @BeforeEach
    void setUp() {
        impl = new CheckInPortImpl(checkOutPort, characterUdfInPort);
    }

    @Test
    void getReservation_whenBasketResponseNull_throwsCheckInRequestException() {
        String hotelId = "H1";
        String reservationId = "R1";

        when(checkOutPort.getReservation(hotelId, reservationId)).thenReturn(null);

        CheckInRequestException ex = assertThrows(
                CheckInRequestException.class,
                () -> impl.getReservation(hotelId, reservationId)
        );
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND.getCode(), ex.getErrorCode());
        verify(checkOutPort).getReservation(hotelId, reservationId);
        verifyNoMoreInteractions(checkOutPort, characterUdfInPort);
    }

    @Test
    void getReservation_whenOutstandingBalanceNotZero_throwsCheckInRequestException() {
        String hotelId = "H1";
        String reservationId = "R1";

        ReservationByBasketRefResponseDto basket = mock(ReservationByBasketRefResponseDto.class);
        when(checkOutPort.getReservation(hotelId, reservationId)).thenReturn(basket);
        // Anything other than OUTSTANDING_BALANCE
        when(basket.getBalanceOutstanding()).thenReturn(new BigDecimal("10.50"));

        CheckInRequestException ex = assertThrows(
                CheckInRequestException.class,
                () -> impl.getReservation(hotelId, reservationId)
        );
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND.getCode(), ex.getErrorCode());
        verify(checkOutPort).getReservation(hotelId, reservationId);
        verify(basket).getBalanceOutstanding();
        verifyNoMoreInteractions(checkOutPort, characterUdfInPort);
    }

    @Test
    void getReservation_happyPath_returnsMatchingReservation() {
        String hotelId = "H1";
        String reservationId = "R1";

        ReservationByBasketRefResponseDto basket = mock(ReservationByBasketRefResponseDto.class);
        when(checkOutPort.getReservation(hotelId, reservationId)).thenReturn(basket);
        when(basket.getBalanceOutstanding()).thenReturn(BigDecimal.ZERO);

        ReservationByIdDto match = mock(ReservationByIdDto.class);
        RoomStayByIdDto roomStay = mock(RoomStayByIdDto.class);

        when(match.getReservationId()).thenReturn(reservationId.toUpperCase()); // case-insensitive check
        when(match.getRoomStay()).thenReturn(roomStay);

        List<ReservationByIdDto> list = List.of(match);
        when(basket.getReservationByIdList()).thenReturn(list);

        ReservationByIdDto result = impl.getReservation(hotelId, reservationId);

        assertSame(match, result);
        verify(checkOutPort).getReservation(hotelId, reservationId);
        verify(basket).getBalanceOutstanding();
        verify(basket).getReservationByIdList();
        verifyNoMoreInteractions(checkOutPort, characterUdfInPort);
    }

    @Test
    void allocateRoom_whenAllocationResponseNullOrInvalid_throws() {
        String hotelId = "H1";
        String reservationId = "R1";

        // Case 1: null response
        when(checkOutPort.allocateRoom(any(RoomAllocationRequestDto.class))).thenReturn(null);

        CheckInRequestException ex1 = assertThrows(
                CheckInRequestException.class,
                () -> impl.allocateRoom(hotelId, reservationId, "DBL", "100")
        );
        assertEquals(ErrorCode.NO_ROOMS_AVAILABLE.getCode(), ex1.getErrorCode());

        // Case 2: blank roomId
        AllocationResponseDto invalid1 = mock(AllocationResponseDto.class);
        when(invalid1.getRoomId()).thenReturn("   ");
        when(checkOutPort.allocateRoom(any(RoomAllocationRequestDto.class))).thenReturn(invalid1);

        CheckInRequestException ex2 = assertThrows(
                CheckInRequestException.class,
                () -> impl.allocateRoom(hotelId, reservationId, "DBL", "100")
        );
        assertEquals(ErrorCode.NO_ROOMS_AVAILABLE.getCode(), ex2.getErrorCode());

        // Case 3: blank status
        AllocationResponseDto invalid2 = mock(AllocationResponseDto.class);
        when(invalid2.getRoomId()).thenReturn("105");
        when(invalid2.getStatus()).thenReturn("   ");
        when(checkOutPort.allocateRoom(any(RoomAllocationRequestDto.class))).thenReturn(invalid2);

        CheckInRequestException ex3 = assertThrows(
                CheckInRequestException.class,
                () -> impl.allocateRoom(hotelId, reservationId, "DBL", "100")
        );
        assertEquals(ErrorCode.NO_ROOMS_AVAILABLE.getCode(), ex3.getErrorCode());

        verify(checkOutPort, times(3)).allocateRoom(any(RoomAllocationRequestDto.class));
        verifyNoMoreInteractions(characterUdfInPort);
    }

    @Test
    void allocateRoom_whenNotClean_throws() {
        String hotelId = "H1";
        String reservationId = "R1";

        AllocationResponseDto resp = mock(AllocationResponseDto.class);
        when(resp.getRoomId()).thenReturn("201");
        when(resp.getStatus()).thenReturn("DIRTY"); // not CLEAN
        when(checkOutPort.allocateRoom(any(RoomAllocationRequestDto.class))).thenReturn(resp);

        CheckInRequestException ex = assertThrows(
                CheckInRequestException.class,
                () -> impl.allocateRoom(hotelId, reservationId, "DBL", "100")
        );
        assertEquals(ErrorCode.NO_ROOMS_AVAILABLE.getCode(), ex.getErrorCode());

        verify(checkOutPort).allocateRoom(any(RoomAllocationRequestDto.class));
        verifyNoMoreInteractions(characterUdfInPort);
    }

    @Test
    void allocateRoom_happyPath_updatesUdfAndReturnsRoomId() {
        String hotelId = "H1";
        String reservationId = "R1";

        AllocationResponseDto resp = mock(AllocationResponseDto.class);
        when(resp.getRoomId()).thenReturn("205");
        when(resp.getStatus()).thenReturn(CLEAN);

        when(checkOutPort.allocateRoom(any(RoomAllocationRequestDto.class))).thenReturn(resp);

        String out = impl.allocateRoom(hotelId, reservationId, "DBL", "100");

        assertEquals("205", out);

        // Verify UDF status update order (optional)
        InOrder inOrder = inOrder(checkOutPort, characterUdfInPort);
        inOrder.verify(checkOutPort).allocateRoom(any(RoomAllocationRequestDto.class));
        inOrder.verify(characterUdfInPort).updateUdfc20(reservationId, hotelId, DK_ISSUED);
        verifyNoMoreInteractions(checkOutPort, characterUdfInPort);
    }

    @Test
    void isNullOrBlank_behavesAsExpected() {
        assertTrue(CheckInPortImpl.isNullOrBlank(""));
        assertTrue(CheckInPortImpl.isNullOrBlank("   "));
        assertFalse(CheckInPortImpl.isNullOrBlank("A"));
        assertFalse(CheckInPortImpl.isNullOrBlank("  B "));
    }


    @Test
    void testCheckIn_ReturnsSuccessResponse() {
        // Arrange
        String reservationId = "RES123";
        String hotelId = "H100";
        String inputRoomId = "201";
        String finalRoomId = "777";   // expected room returned by nested response

        // Create nested structure:
        CurrentRoomInfo currentRoomInfo = new CurrentRoomInfo();
        currentRoomInfo.setRoomId(finalRoomId);

        RoomStay roomStay = new RoomStay();
        roomStay.setCurrentRoomInfo(currentRoomInfo);

        Reservation reservation = new Reservation();
        reservation.setRoomStay(roomStay);

        CheckInResponse checkInResponse = new CheckInResponse();
        checkInResponse.setReservation(List.of(reservation));

        // Mock doCheckIn call
        when(checkOutPort.doCheckIn(any(CheckInRequest.class)))
                .thenReturn(checkInResponse);

        // Act
        CheckInResponseDto result =
                impl.checkIn(reservationId, hotelId, inputRoomId);

        // Assert
        assertNotNull(result);
        assertEquals(finalRoomId, result.getRoomNumber());
        assertEquals(SUCCESS, result.getCheckInStatus());

        // Verify doCheckIn was called with any built CheckInRequest
        verify(checkOutPort).doCheckIn(any(CheckInRequest.class));

        verifyNoMoreInteractions(checkOutPort, characterUdfInPort);
    }

}

