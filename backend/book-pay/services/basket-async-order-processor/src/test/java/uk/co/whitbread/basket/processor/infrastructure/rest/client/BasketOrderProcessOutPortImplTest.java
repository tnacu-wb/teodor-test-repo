package uk.co.whitbread.basket.processor.infrastructure.rest.client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.processor.domain.model.out.BookingChannel;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmAmendRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.PaymentCard;
import uk.co.whitbread.basket.processor.domain.model.out.PaymentOption;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.AmendReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.HotelReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.mapper.ReservationRequestMapper;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.mapper.ReservationResponseMapper;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.ReservationsClient;
import uk.co.whitbread.hotel.reservation.generated.models.BookingChannelDto;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmAmendRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationByBasketRefResponseDto;

@ExtendWith(MockitoExtension.class)
class BasketOrderProcessOutPortImplTest {

  @InjectMocks
  private BasketOrderProcessOutPortImpl basketOrderProcessOutPort;

  @Mock
  private ReservationsClient reservationsClient;
  @Mock
  private ReservationRequestMapper reservationRequestMapper;
  @Mock
  private ReservationResponseMapper reservationResponseMapper;

  @Test
  void confirmReservation__success() {
    //Arrange
    var reservation = new ConfirmReservationRequest("ABCDE12345", "TEST",
        PaymentOption.RESERVE_WITHOUT_CARD,
        "DVA", "VA", PaymentCard.builder().build(), "3CPReference", null, "1");
    when(reservationRequestMapper.toDto(any(ConfirmReservationRequest.class))).thenReturn(
        mockConfirmReservationRequest());
    when(reservationsClient.confirmReservation(any())).thenReturn(new ConfirmReservationResponseDto());

    //Act
    basketOrderProcessOutPort.confirmReservation(reservation);

    //Assert
    verify(reservationsClient, times(1)).confirmReservation(any());
  }

  @Test
  void cancelReservation__success() {
    //Arrange
    var reservation = new CancelReservationRequest("HOTEL_01", "TEST", List.of("RESERVATION123"),
        PaymentOption.RESERVE_WITHOUT_CARD);
    when(reservationRequestMapper.toDto(any(CancelReservationRequest.class))).thenReturn(
        mockCancelReservationRequest());
    when(reservationsClient.cancelReservation(any())).thenReturn(new CancelReservationResponseDto());

    //Act
    basketOrderProcessOutPort
        .cancelReservation(reservation);

    //Assert
    verify(reservationsClient, times(1)).cancelReservation(any());
  }

  @Test
  void confirmAmend__success() {
    //Arrange
    when(reservationRequestMapper.toDto(any(ConfirmAmendRequest.class))).thenReturn(
            mockConfirmAmendRequestDto());
    when(reservationsClient.confirmAmend(any())).thenReturn(new ReservationByBasketRefResponseDto());

    //Act
    basketOrderProcessOutPort
            .confirmAmend(mockConfirmAmendRequest());

    //Assert
    verify(reservationsClient, times(1)).confirmAmend(mockConfirmAmendRequestDto());
  }

  @Test
  void confirmReservation_fail() {
    //Arrange
    var reservation = new ConfirmReservationRequest("ABCDE12345", "TEST",
        PaymentOption.RESERVE_WITHOUT_CARD,
        "DVA", "VA", PaymentCard.builder().build(), "3CPReference", null, "1");
    when(reservationRequestMapper.toDto(any(ConfirmReservationRequest.class))).thenReturn(
        mockConfirmReservationRequest());
    when(reservationsClient.confirmReservation(any())).thenThrow(
        new HotelReservationException("Exception on purpose for unit test", "", new Exception(),
            100));
    //Assert
    assertThrows(HotelReservationException.class, () -> basketOrderProcessOutPort
        .confirmReservation(reservation));
  }

  @Test
  void cancelReservation_fail() {
    //Arrange
    var reservation = new CancelReservationRequest("HOTEL_01", "TEST", List.of("RESERVATION123"),
        PaymentOption.RESERVE_WITHOUT_CARD);
    when(reservationRequestMapper.toDto(any(CancelReservationRequest.class))).thenReturn(
        mockCancelReservationRequest());
    when(reservationsClient.cancelReservation(any())).thenThrow(
        new HotelReservationException("Exception on purpose for unit test", "", new Exception(),
            100));
    //Assert
    assertThrows(HotelReservationException.class, () -> basketOrderProcessOutPort
            .cancelReservation(reservation));
  }

  @Test
  void confirmAmend_fail() {
    //Arrange
    when(reservationRequestMapper.toDto(any(ConfirmAmendRequest.class))).thenReturn(
        mockConfirmAmendRequestDto());
    when(reservationsClient.confirmAmend(any())).thenThrow(
        new AmendReservationException("Exception on purpose for unit test", "", new Exception(),
            100));
    ConfirmAmendRequest confirmAmendRequest = mockConfirmAmendRequest();

    //Assert
    assertThrows(AmendReservationException.class, () -> basketOrderProcessOutPort
        .confirmAmend(confirmAmendRequest));
  }

  private CancelReservationRequestDto mockCancelReservationRequest() {
    var cancelReservationRequestDto = new CancelReservationRequestDto();
    cancelReservationRequestDto.setReservationIds(List.of("RESERVATION123"));
    return cancelReservationRequestDto;
  }

  private ConfirmReservationRequestDto mockConfirmReservationRequest() {
    var confirmReservationRequestDto = new ConfirmReservationRequestDto();
    confirmReservationRequestDto.setReservationId("ABCDE12345");
    return confirmReservationRequestDto;
  }

  private ConfirmAmendRequestDto mockConfirmAmendRequestDto() {
    var bookingChannel = new BookingChannelDto();
    bookingChannel.setChannel("PI");
    bookingChannel.setSubchannel("WEB");
    bookingChannel.setLanguage("EN");

    var confirmAmendRequestDto = new ConfirmAmendRequestDto();
    confirmAmendRequestDto.setOriginalBookingRef("original-basketref");
    confirmAmendRequestDto.setTempBookingRef("temp-basketref");
    confirmAmendRequestDto.setToken("123456789");
    confirmAmendRequestDto.setBookingChannel(bookingChannel);

    return confirmAmendRequestDto;
  }


  private ConfirmAmendRequest mockConfirmAmendRequest() {
    var bookingChannel = BookingChannel.builder()
    .channel("PI")
    .subchannel("WEB")
    .language("EN")
            .build();

      return ConfirmAmendRequest.builder()
    .originalBookingRef("original-basketref")
    .tempBookingRef("temp-basketref")
    .token("123456789")
    .bookingChannel(bookingChannel)
            .build();
  }

}