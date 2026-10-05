package uk.co.whitbread.booking.infrastructure.rest.controller.booking;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.booking.domain.ports.primary.BookingInPort;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.mapper.BookingMapper;
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
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.in.DownloadBookingInvoicesRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out.InvoiceDownloadResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.in.UpcomingBookingsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.out.UpcomingBookingsResponseDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@RequestMapping("/v1/bookings")
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController implements BookingApi {

  private final BookingInPort bookingInPort;
  private final BookingMapper bookingMapper;
  private final AuthenticatedUserService authenticatedUserService;

  @Override
  @GetMapping(value = "/history", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingResponseDto> getBookingsHistory(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @ModelAttribute BookingChannelDto bookingChannelDto,
      BookingRequestDto bookingHistoryRequest) {

    var bookingRequest = bookingMapper.toModel(bookingHistoryRequest);
    var bookingChannel = bookingMapper.toModel(bookingChannelDto);
    var bookingResponse = bookingInPort.getBookings(authorization, bookingRequest, bookingChannel);
    return ResponseEntity.ok(bookingMapper.toDto(bookingResponse));
  }

  @Override
  @PostMapping(value = "/history", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingResponseDto> retrieveBookingHistory(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @RequestBody @Valid BookingHistoryRequestDto bookingHistoryRequestDto) {

    var bookingRequest = bookingMapper.toModel(bookingHistoryRequestDto);
    var bookingChannel = bookingMapper.toBookingChannelModel(bookingHistoryRequestDto);
    var bookingResponse = bookingInPort.getBookings(authorization, bookingRequest, bookingChannel);
    return ResponseEntity.ok(bookingMapper.toDto(bookingResponse));
  }

  @Override
  @GetMapping(value = "/information", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingInfoResponseDto> getBookingInformation(
      @ModelAttribute BookingChannelDto bookingChannelDto,
      BookingInfoRequestDto bookingRequestDto) {

    var bookingInfoRequest = bookingMapper.toModel(bookingRequestDto);
    var bookingChannel = bookingMapper.toModel(bookingChannelDto);
    var bookingInfoResponse = bookingMapper.toDto(
        bookingInPort.getBookingInformation(bookingInfoRequest, bookingChannel));

    return ResponseEntity.ok(bookingInfoResponse);
  }

  @Override
  @GetMapping(value = "/upcomingBookings", produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UpcomingBookingsResponseDto> getUpcomingBookingsForInnBusiness(
        @RequestHeader(value = WB_AUTHORIZATION) String authorization,
        UpcomingBookingsRequestDto upcomingBookingsRequestDto) {

    var request = bookingMapper.toModel(upcomingBookingsRequestDto);
    var upcomingBookingsResponse = bookingInPort.getUpcomingBookings(request, authorization);
    return ResponseEntity.ok(bookingMapper.toDto(upcomingBookingsResponse));
  }

  @Override
  @PostMapping(value = "/cancel", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<CancelBookingResponseDto> cancelBooking(
      @ModelAttribute BookingChannelDto bookingChannelDto,
      @RequestBody CancelBookingRequestDto cancelBookingRequestDto) {

    log.info("Cancelbookingrequest {}", cancelBookingRequestDto);
    var cancelBookingRequest = bookingMapper.toModel(cancelBookingRequestDto);
    var cancelBookingResponse = bookingInPort.cancelBooking(cancelBookingRequest);
    var cancelBookingResponseDto = bookingMapper.toDto(cancelBookingResponse,
            cancelBookingRequest.getBookingReference());
    return ResponseEntity.ok(cancelBookingResponseDto);
  }

  @Override
  @PostMapping(value = "/confirmation")
  public ResponseEntity<Void> sendBookingConfirmationEmail(
      @RequestBody ResendConfirmationEmailRequestDto bookingConfirmationRequest) {

    var request = bookingMapper.toModel(bookingConfirmationRequest);
    bookingInPort.sendBookingConfirmationEmail(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Override
  @PostMapping(value = "/invoice")
  public ResponseEntity<Void> resendBookingInvoiceEmail(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @ModelAttribute BookingChannelDto bookingChannelDto,
      @RequestBody ResendInvoiceEmailRequestDto bookingInvoiceRequest) {

    var request = bookingMapper.toModel(bookingInvoiceRequest);
    bookingInPort.resendBookingInvoiceEmail(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Override
  @PostMapping(value = "/invoices/download", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<InvoiceDownloadResponseDto> downloadBookingInvoices(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @RequestBody @Valid DownloadBookingInvoicesRequestDto requestDto) {

    if (!authenticatedUserService.isUserAuthenticated()) {
      return ResponseEntity.status(UNAUTHORIZED).build();
    }

    var request = bookingMapper.toModel(requestDto);
    var response = bookingInPort.downloadBookingInvoices(authorization, request);
    return ResponseEntity.ok(bookingMapper.toDto(response));
  }

}
