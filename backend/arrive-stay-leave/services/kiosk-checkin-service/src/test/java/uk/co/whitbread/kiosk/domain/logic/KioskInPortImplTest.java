package uk.co.whitbread.kiosk.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.kiosk.ErrorCode;
import uk.co.whitbread.kiosk.domain.logic.config.PaymentTypeConfig;
import uk.co.whitbread.kiosk.domain.logic.exception.BalanceNotPaidException;
import uk.co.whitbread.kiosk.domain.logic.exception.ConfirmReservationException;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CardRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.PaymentDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ReservationComments;
import uk.co.whitbread.kiosk.domain.model.checkin.in.StayingGuestDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.out.ConfirmReservationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HotelRoomsDetails;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.Room;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.kiosk.domain.ports.secondary.KioskOutPort;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.OhipProfileRequestMapper;

@ExtendWith(MockitoExtension.class)
class KioskInPortImplTest {

  @Mock
  private KioskOutPort kioskOutPort;
  @Mock
  private PaymentTypeConfig paymentTypeConfig;
  @Mock
  private OhipProfileRequestMapper ohipProfileRequestMapper;
  @InjectMocks
  private KioskInPortImpl kioskInPortImpl;

  @Test
  void getCheckInResponse_Success() {
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());
    doNothing().when(kioskOutPort)
        .processProfileRequest(any());
    when(kioskOutPort.confirmReservation(any(ConfirmReservationRequest.class))).thenReturn(
        ConfirmReservationResponse.builder().reservationStatus("reserved").build());
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.empty());

    var response = kioskInPortImpl.getCheckInResponse(mockCheckInRequest());

    assertNotNull(response);
  }

  @Test
  void getCheckInResponse_Success_with_zero_amount() {
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());

    var response = kioskInPortImpl.getCheckInResponse(mockCheckInRequestWithZeroAmount());
    assertNotNull(response);

    var response1 = kioskInPortImpl.getCheckInResponse(mockCheckInRequestWithZeroAmount1());
    assertNotNull(response1);
  }

  @Test
  void getCheckInResponse_Success_with_null_for_amount() {
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());

    var response = kioskInPortImpl.getCheckInResponse(mockCheckInRequestWithNullAmount());
    assertNotNull(response);
  }

  @Test
  void getCheckInResponse_Success_with_null_for_payment() {
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());

    var response = kioskInPortImpl.getCheckInResponse(mockCheckInRequestWithNullForPayment());
    assertNotNull(response);
  }

  @Test
  void getCheckInResponse_WhenReservationIsPartialPaid_ThenConfirmReservationExceptionIsThrown() {
    doNothing().when(kioskOutPort)
        .processProfileRequest(any());
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.empty());
    when(kioskOutPort.confirmReservation(any(ConfirmReservationRequest.class))).thenReturn(
        ConfirmReservationResponse
            .builder()
            .reservationStatus("reserved")
            .isPartialPaid(true)
            .build());

    var exception = Assertions.assertThrows(ConfirmReservationException.class,
        () -> kioskInPortImpl.getCheckInResponse(mockCheckInRequest()));

    assertEquals(ErrorCode.KIOSK_PARTIAL_PAID_CHECK_IN_EXCEPTION.getMessage(), exception.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.KIOSK_PARTIAL_PAID_CHECK_IN_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getCheckInResponse_WhenCardIsNull_ThenConfirmReservationExceptionIsThrown() {
    doNothing().when(kioskOutPort)
        .processProfileRequest(any());
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.empty());
    CheckInRequest checkInRequest = CheckInRequest.builder().hotelId("MANOLD").roomId("100")
        .roomType("PPLDBL")
        .reservationNumber("123456").stayingGuestDetails(new ArrayList<>(Arrays.asList(
            StayingGuestDetails.builder().firstName("Test1").lastName("Name1").build(),
            StayingGuestDetails.builder().firstName("Test2").lastName("Name2").build())))
        .paymentDetails(PaymentDetails.builder().amount(new BigDecimal("70.55")).build()).build();

    var exception = Assertions.assertThrows(ConfirmReservationException.class,
        () -> kioskInPortImpl.getCheckInResponse(checkInRequest));

    assertEquals(ErrorCode.KIOSK_CARD_EXCEPTION.getMessage(), exception.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.KIOSK_CARD_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void createProfileRequest_Success() {
    var response = kioskInPortImpl.createProfileRequest(mockCheckInRequest());

    assertEquals("Test1", response.getGuestDetails().get(0).getGivenName());
  }

  private CheckInRequest mockCheckInRequest() {
    return CheckInRequest.builder().hotelId("MANOLD").roomId("100").roomType("PPLDBL")
        .reservationNumber("123456").stayingGuestDetails(new ArrayList<>(Arrays.asList(
            StayingGuestDetails.builder().firstName("Test1").lastName("Name1").build(),
            StayingGuestDetails.builder().firstName("Test2").lastName("Name2").build())))
        .paymentDetails(PaymentDetails.builder()
            .amount(new BigDecimal("50.97"))
            .card(
                CardRequest.builder().expiryYear("2025").expiryMonth("02")
                    .token("873465276439572469857").cardholderName("Sooraj").cardType("ZZ")
                    .cardSchemeId("PI").build())
            .build()).build();
  }

  @Test
  void allocateRooms_Success() {
    when(kioskOutPort.allocateRooms(anyString(), anyString(),
        any(VacantRoomResponse.class), anyString(), any())).thenReturn(new AllocationResponse());

    var response = kioskInPortImpl.allocateRooms("MANOLD", "123456", mockVacantRoomResponse(),
        "DOUBLE", new KioskReservationPreferences());

    assertNotNull(response);
  }


  @Test
  void getVacantRooms_Success() {
    when(kioskOutPort.getVacantRooms(anyString(), anyString())).thenReturn(
        mockVacantRoomResponse());

    var response = kioskInPortImpl.getVacantRooms("MANOLD", "PPLDBL");

    assertNotNull(response);
  }

  private VacantRoomResponse mockVacantRoomResponse() {
    List<String> roomIds = Arrays.asList("100", "101", "102", "103");
    VacantRoomResponse vacantRoomResponse = new VacantRoomResponse();
    HotelRoomsDetails hotelRoomsDetails = new HotelRoomsDetails();
    List<Room> room = new ArrayList<>();
    roomIds.forEach(roomId -> room.add(Room.builder().roomId(roomId).build()));
    hotelRoomsDetails.setRoom(room);
    vacantRoomResponse.setHotelRoomsDetails(hotelRoomsDetails);
    return vacantRoomResponse;
  }

  @Test
  void prepareConfirmReservationRequest_ForBUCardType() {
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());
    doNothing().when(kioskOutPort).processProfileRequest(any());
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.empty());
    when(kioskOutPort.confirmReservation(any(ConfirmReservationRequest.class)))
        .thenReturn(ConfirmReservationResponse.builder().reservationStatus("reserved").build());

    var response = kioskInPortImpl.getCheckInResponse(mockCheckInRequest());

    assertNotNull(response);

    ArgumentCaptor<ConfirmReservationRequest> captor = forClass(ConfirmReservationRequest.class);
    verify(kioskOutPort).confirmReservation(captor.capture());
    ConfirmReservationRequest capturedRequest = captor.getValue();
    final String cardType = capturedRequest.getPaymentCard().getCardType();
    assertEquals("BU", cardType);
  }

  @Test
  void prepareConfirmReservationRequest_ForOtherCardType() {
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());
    doNothing().when(kioskOutPort).processProfileRequest(any());
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.empty());
    when(kioskOutPort.confirmReservation(any(ConfirmReservationRequest.class)))
        .thenReturn(ConfirmReservationResponse.builder().reservationStatus("reserved").build());
    final CheckInRequest mockRequest = mockCheckInRequest();
    mockRequest.getPaymentDetails().getCard().setCardType("VISA");
    var response = kioskInPortImpl.getCheckInResponse(mockRequest);

    assertNotNull(response);

    ArgumentCaptor<ConfirmReservationRequest> captor = forClass(ConfirmReservationRequest.class);
    verify(kioskOutPort).confirmReservation(captor.capture());
    ConfirmReservationRequest capturedRequest = captor.getValue();
    final String cardType = capturedRequest.getPaymentCard().getCardType();
    assertEquals("VISA", cardType);
  }

  @Test
  void getCheckInResponse_throwsBalanceNotPaidException() {
    doNothing().when(kioskOutPort)
        .processProfileRequest(any());
    doNothing().when(kioskOutPort).updateReservationComments(anyString(),anyString(),any(List.class));
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.of(BigDecimal.valueOf(100)));

    CheckInRequest checkInRequest = mockCheckInRequest();
    checkInRequest.getPaymentDetails().setAmount(BigDecimal.valueOf(50));
    checkInRequest.setReservationComments(List.of(new ReservationComments()));

    assertThrows(BalanceNotPaidException.class, () -> kioskInPortImpl.getCheckInResponse(checkInRequest));
  }

  @Test
  void getCheckInResponse_whenOutstandingBalanceIsPaid() {
    doNothing().when(kioskOutPort)
        .processProfileRequest(any());
    when(kioskOutPort.getOutstandingBalance(anyString(),anyString())).thenReturn(Optional.of(BigDecimal.valueOf(100)));
    when(kioskOutPort.confirmReservation(any(ConfirmReservationRequest.class))).thenReturn(
        ConfirmReservationResponse.builder().reservationStatus("reserved").build());
    when(kioskOutPort.doCheckIn(any(CheckInRequest.class))).thenReturn(new CheckInResponse());

    CheckInRequest checkInRequest = mockCheckInRequest();
    checkInRequest.getPaymentDetails().setAmount(BigDecimal.valueOf(100));
    checkInRequest.setReservationComments(new ArrayList<>());

    CheckInResponse response = kioskInPortImpl.getCheckInResponse(checkInRequest);

    assertNotNull(response);
  }

  private CheckInRequest mockCheckInRequestWithZeroAmount() {
    return CheckInRequest.builder().hotelId("MANOLD").roomId("100").roomType("PPLDBL")
        .reservationNumber("123456").stayingGuestDetails(new ArrayList<>(Arrays.asList(
            StayingGuestDetails.builder().firstName("Test1").lastName("Name1").build(),
            StayingGuestDetails.builder().firstName("Test2").lastName("Name2").build())))
        .paymentDetails(PaymentDetails.builder()
            .amount(new BigDecimal("0"))
            .card(
                CardRequest.builder().expiryYear("2025").expiryMonth("02")
                    .token("873465276439572469857").cardholderName("Sooraj").cardType("ZZ")
                    .cardSchemeId("PI").build())
            .build()).build();
  }

  private CheckInRequest mockCheckInRequestWithZeroAmount1() {
    return CheckInRequest.builder().hotelId("MANOLD").roomId("100").roomType("PPLDBL")
        .reservationNumber("123456").stayingGuestDetails(new ArrayList<>(Arrays.asList(
            StayingGuestDetails.builder().firstName("Test1").lastName("Name1").build(),
            StayingGuestDetails.builder().firstName("Test2").lastName("Name2").build())))
        .paymentDetails(PaymentDetails.builder()
            .amount(new BigDecimal("0.00"))
            .card(
                CardRequest.builder().expiryYear("2025").expiryMonth("02")
                    .token("873465276439572469857").cardholderName("Sooraj").cardType("ZZ")
                    .cardSchemeId("PI").build())
            .build()).build();
  }

  private CheckInRequest mockCheckInRequestWithNullAmount() {
    return CheckInRequest.builder().hotelId("MANOLD").roomId("100").roomType("PPLDBL")
        .reservationNumber("123456").stayingGuestDetails(new ArrayList<>(Arrays.asList(
            StayingGuestDetails.builder().firstName("Test1").lastName("Name1").build(),
            StayingGuestDetails.builder().firstName("Test2").lastName("Name2").build())))
        .paymentDetails(PaymentDetails.builder()
            .amount(null)
            .card(
                CardRequest.builder().expiryYear("2025").expiryMonth("02")
                    .token("873465276439572469857").cardholderName("Sooraj").cardType("ZZ")
                    .cardSchemeId("PI").build())
            .build()).build();
  }

  private CheckInRequest mockCheckInRequestWithNullForPayment() {
    return CheckInRequest.builder().hotelId("MANOLD").roomId("100").roomType("PPLDBL")
        .reservationNumber("123456").stayingGuestDetails(new ArrayList<>(Arrays.asList(
            StayingGuestDetails.builder().firstName("Test1").lastName("Name1").build(),
            StayingGuestDetails.builder().firstName("Test2").lastName("Name2").build())))
        .paymentDetails(null)
        .build();
  }

}
