package uk.co.whitbread.digitalkey.infrastructure.rest.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.KioskCheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.KioskAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.CheckOutPortImpl;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.mapper.KioskCheckInRequestMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.mapper.OhipProfileRequestMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class CheckOutPortImplTest {

  @Mock
  private KioskCheckInRequestMapper kioskCheckInRequestMapper;

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private KioskAdapterClient kioskAdapterClient;

  @Mock
  private OhipProfileRequestMapper ohipProfileRequestMapper;

  @InjectMocks
  private CheckOutPortImpl kioskOutPort;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testDoCheckIn() {
    CheckInRequest checkInRequest = CheckInRequest.builder()
        .hotelId("H1")
        .reservationNumber("R1")
        .roomId("101")
        .build();

    KioskCheckInRequest kioskRequest = KioskCheckInRequest.builder()
        .hotelId("H1")
        .reservationNumber("R1")
        .roomId("101")
        .build();

    CheckInResponse expectedResponse = new CheckInResponse();
    when(kioskCheckInRequestMapper.toKioskCheckInRequestModel(checkInRequest)).thenReturn(kioskRequest);
    when(ohipAdapterClient.doCheckIn(kioskRequest)).thenReturn(expectedResponse);
    CheckInResponse actualResponse = kioskOutPort.doCheckIn(checkInRequest);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void testGetReservation() {
    ReservationByBasketRefResponseDto responseDto = new ReservationByBasketRefResponseDto();
    when(ohipAdapterClient.getReservationDetails("H1", "R1")).thenReturn(responseDto);

    ReservationByBasketRefResponseDto result = kioskOutPort.getReservation("H1", "R1");
    assertEquals(responseDto, result);
  }

  @Test
  void testAllocateRoom_delegatesToKioskAdapterClient() {
    RoomAllocationRequestDto requestDto = new RoomAllocationRequestDto();
    AllocationResponseDto expectedResponse = new AllocationResponseDto();

    when(kioskAdapterClient.allocateRoom(requestDto)).thenReturn(expectedResponse);

    AllocationResponseDto actualResponse = kioskOutPort.allocateRoom(requestDto);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse, actualResponse);
    Mockito.verify(kioskAdapterClient).allocateRoom(requestDto);
  }

}
