package uk.co.whitbread.booking.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.invoice.Language;
import uk.co.whitbread.booking.domain.model.upcoming.in.UpcomingBookingRequest;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsResponse;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.channel.BookingChannelDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.email.in.ResendConfirmationEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.email.in.ResendInvoiceEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in.BookingHistoryRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in.BookingRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out.BookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in.BookingInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in.CancelBookingRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out.BookingInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.LanguageDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.in.DownloadBookingInvoicesRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out.InvoiceDownloadResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.in.UpcomingBookingsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.out.UpcomingBookingsResponseDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {

  BookingInfoRequest toModel(BookingInfoRequestDto bookingRequestDto);

  BookingRequest toModel(BookingRequestDto bookingRequestDto);

  BookingRequest toModel(BookingHistoryRequestDto bookingHistoryRequestDto);

  CancelBookingRequest toModel(CancelBookingRequestDto cancelBookingRequestDto);

  ResendConfirmationEmailRequest toModel(
      ResendConfirmationEmailRequestDto bookingConfirmationRequestDto);

  ResendInvoiceEmailRequest toModel(
      ResendInvoiceEmailRequestDto bookingInvoiceRequest);

  BookingChannel toModel(BookingChannelDto bookingChannelDto);

  UpcomingBookingRequest toModel(UpcomingBookingsRequestDto upcomingBookingsRequestDto);

  @Mapping(target = "channel", expression = "java(request.channel().name())")
  DownloadBookingInvoicesRequest toModel(DownloadBookingInvoicesRequestDto request);

  Language toModel(LanguageDto language);

  BookingChannel toBookingChannelModel(BookingHistoryRequestDto bookingHistoryRequestDto);

  BookingResponseDto toDto(BookingResponse bookingResponse);

  BookingInfoResponseDto toDto(BookingInfoResponse bookingResponse);

  UpcomingBookingsResponseDto toDto(UpcomingBookingsResponse upcomingBookingsResponse);

  @Mapping(target = "bookingReference", source = "bookingReference")
  CancelBookingResponseDto toDto(CancelBookingResponse cancelBookingResponse, String bookingReference);

  InvoiceDownloadResponseDto toDto(InvoiceDownloadResponse response);

  LanguageDto toDto(Language language);
}
