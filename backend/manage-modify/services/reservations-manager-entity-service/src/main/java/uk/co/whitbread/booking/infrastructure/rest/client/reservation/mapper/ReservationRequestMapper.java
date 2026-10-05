package uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.BaseEmailRequest;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.BaseOperaEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationBookingInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationCancelRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationChannelDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationRateInformationRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationRequestMapper {

  @Mapping(
      target = "userDateTime",
      expression = "java(toUserDateTimeModel())"
  )
  @Mapping(target = "country", source = "bookingInfoRequest.country")
  @Mapping(target = "language", source = "bookingInfoRequest.language")
  @Mapping(target = "basketReference", source = "basketReference")
  @Mapping(target = "hotelId", source = "bookingInfoRequest.hotelId")
  @Mapping(target = "channel", source = "reservationChannel.channel")
  @Mapping(target = "subchannel", source = "reservationChannel.subchannel")
  @Mapping(target = "token", source = "bookingInfoRequest.token")
  ReservationCancelRequestDto toDto(
      BookingInfoRequest bookingInfoRequest,
      ReservationChannelDto reservationChannel,
      String basketReference);

  @Mapping(target = "resNo", source = "bookingInfoRequest.bookingReference")
  @Mapping(target = "arrivalDate", source = "bookingInfoRequest.arrival")
  @Mapping(target = "lastName", source = "bookingInfoRequest.surname")
  @Mapping(target = "country", source = "bookingInfoRequest.country")
  @Mapping(target = "language", source = "bookingInfoRequest.language")
  @Mapping(target = "channel", source = "bookingChannel.channel")
  @Mapping(target = "subchannel", source = "bookingChannel.subchannel")
  @Mapping(target = "isOldBooking", source = "isPastBooking")
  ReservationBookingInfoRequestDto toDto(BookingInfoRequest bookingInfoRequest, BookingChannel bookingChannel,
                                         boolean isPastBooking);

  @Mapping(source = "basketReference", target = "basketReference")
  CancelReservationRequestDto toDto(CancelBookingRequest cancelBookingRequest);

  BaseOperaEmailRequestDto toDto(BaseEmailRequest bookingConfirmationRequest);

  ReservationChannelDto toDto(BookingChannel bookingChannel);

  @Mapping(target = "hotelId", source = "request.hotelId")
  ReservationRateInformationRequestDto toModel(
      BookingInfoRequest request, String channel
  );

  default String toUserDateTimeModel() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
  }

}
