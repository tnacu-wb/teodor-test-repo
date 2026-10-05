package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationAlertsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class HandleCiolRevertHelperTest {

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private BasketClient basketClient;

  @Mock
  private ConcurrentTracer concurrentTracer;

  @InjectMocks
  private HandleCiolRevertHelper handleCiolRevertHelper;

  private static Stream<ReservationByBasketRefResponseDto> invalidBookings() {
    var roomsNull = new ReservationByBasketRefResponseDto();
    roomsNull.setReservationByIdList(null);
    var bookingRefNull = new ReservationByBasketRefResponseDto();
    bookingRefNull.setReservationByIdList(List.of());
    bookingRefNull.setBookingReference(null);
    return Stream.of(roomsNull, bookingRefNull, null);
  }

  private static Stream<ResponseEntity<BasketDto>> invalidBasket() {
    var completedBasket = new BasketDto();
    completedBasket.setStatus(StatusEnum.COMPLETED);

    return Stream.of(ResponseEntity.of(Optional.of(completedBasket)), null);
  }

  @ParameterizedTest
  @MethodSource("invalidBookings")
  void revertCiol_invalidBookings(ReservationByBasketRefResponseDto booking) {
    //arrange
    when(ohipAdapterClient
        .sendGetReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(booking);

    //act
    handleCiolRevertHelper.revertCiol(List.of("123"), "hotel");

    //assert
    verify(basketClient, Mockito.times(0)).sendGetBasketByReference(any());
  }

  @ParameterizedTest
  @MethodSource("invalidBasket")
  void revertCiol_wrongBasketStatus(ResponseEntity<BasketDto> basketDto) {
    var booking = new ReservationByBasketRefResponseDto();
    booking.setBookingReference("bookingRef");
    booking.setReservationByIdList(List.of(new ReservationByIdDto()));
    //arrange
    when(ohipAdapterClient
        .sendGetReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(booking);
    when(basketClient.sendGetBasketByReference(any())).thenReturn(basketDto);

    //act
    handleCiolRevertHelper.revertCiol(List.of("123"), "hotel");

    //assert
    verify(basketClient, times(0)).changeStatus(any(), any());
    verify(ohipAdapterClient, times(0)).updateReservationAlerts(any());
  }

  @Test
  void revertCiol_success() {
    var ciolAlert = mockAlerts("CIOL", "111");
    var coolAlert = mockAlerts("COOL", "123");
    var nullCodeAlert = mockAlerts(null, "123");
    var nullAlertId = mockAlerts("CIOL", null);
    var room = mockRooms("123", List.of(ciolAlert, coolAlert, nullAlertId, nullCodeAlert));
    var roomWithNoAlerts = mockRooms("321", List.of());
    var roomWithNullAlerts = mockRooms("789", null);
    var roomWithNullRoomId = mockRooms(null, List.of());
    var booking = new ReservationByBasketRefResponseDto();
    booking.setBookingReference("bookingRef");
    booking.setReservationByIdList(
        List.of(room, roomWithNoAlerts, roomWithNullAlerts, roomWithNullAlerts,
            roomWithNullRoomId));

    var basket = new BasketDto();
    basket.setStatus(StatusEnum.PRE_CHECKED_IN);
    var getBasketresponse = ResponseEntity.ok(basket);

    var changeStatusResponse = new BasketDto();
    changeStatusResponse.setStatus(StatusEnum.CIOL_RC_FAILED);

    //arrange
    when(ohipAdapterClient
        .sendGetReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(booking);
    when(basketClient.sendGetBasketByReference(any())).thenReturn(getBasketresponse);
    when(basketClient.changeStatus(any(), any())).thenReturn(changeStatusResponse);
    doNothing().when(ohipAdapterClient).updateReservationAlerts(any());

    //act
    handleCiolRevertHelper.revertCiol(List.of("123"), "hotel");

    //assert
    verify(basketClient, times(1)).changeStatus(any(), any());
    verify(ohipAdapterClient, times(1)).updateReservationAlerts(any());
  }

  @Test
  void initiateCiolRevert_success() {
    when(concurrentTracer.wrap(any(Runnable.class))).
        thenAnswer(invocation -> invocation.getArgument(0));
    assertDoesNotThrow(() -> handleCiolRevertHelper.initiateCiolRevert(List.of("123"),"FRAMTI"));
  }

  private ReservationByIdDto mockRooms(String roomId, List<ReservationAlertsDto> alerts) {
    var room = new ReservationByIdDto();
    room.setReservationId(roomId);
    room.setAlerts(alerts);
    return room;
  }

  private ReservationAlertsDto mockAlerts(String alertCode, String alertId) {
    var alerts = new ReservationAlertsDto();
    alerts.setArea("CHECKIN");
    alerts.setCode(alertCode);
    alerts.setId(alertId);
    return alerts;
  }
}