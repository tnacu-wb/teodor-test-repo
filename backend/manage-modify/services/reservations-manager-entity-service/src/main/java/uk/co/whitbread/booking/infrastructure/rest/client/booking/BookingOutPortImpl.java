package uk.co.whitbread.booking.infrastructure.rest.client.booking;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.exceptions.AuthorizationException;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper.StayRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper.StayResponseMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.StayRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.BookingClient;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.HotelAccountClient;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.RateClassificationDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@RequiredArgsConstructor
public class BookingOutPortImpl implements BookingOutPort {
  private static final String GUEST = "STAYER";
  private static final String NOT_AUTHORIZED = "Not authorized";
  private final StayResponseMapper stayResponseMapper;
  private final StayRequestMapper stayRequestMapper;
  private final TokenService tokenService;
  private final BookingClient bookingClient;
  private final HotelAccountClient hotelAccountClient;
  private final ContentClient contentClient;
  private final AuthenticatedUserService authenticatedUserService;

  @Override
  public BookingResponse getBookings(String authorization, BookingRequest bookingRequest,
      BookingChannel channelCode) {
    StayRequestDto stayRequest;
    if (bookingRequest.isBusiness()) {
      EmployeeDetails employeeDetails = tokenService.retrieveEmployeeDetailsAndVerifyToken(authorization);
      stayRequest = stayRequestMapper.toDto(bookingRequest, employeeDetails);
    } else {
      stayRequest = stayRequestMapper.toDto(bookingRequest);
    }
    var stayChannel = stayRequestMapper.toBartStayCodeDto(channelCode);
    var staysResponse = hotelAccountClient.getCustomerBookings(authorization, stayChannel, stayRequest);
    return stayResponseMapper.toModel(staysResponse);
  }

  @Override
  public BookingInfoResponse getBookingInformation(BookingInfoRequest bookingInfoRequest,
      BookingChannel bookingChannel) {

    var stayInfoRequest = stayRequestMapper.toDto(bookingInfoRequest);
    var stayInfoResponse = bookingClient.getBookingInformation(
        stayRequestMapper.toBartStayCodeDto(bookingChannel), bookingInfoRequest.getCountry(),
        bookingInfoRequest.getLanguage(), bookingInfoRequest.getBookingReference(),
        stayInfoRequest);
    var rateInformation = contentClient.getHotelRateInformation(bookingInfoRequest.getCountry(),
        bookingInfoRequest.getLanguage(), bookingInfoRequest.getHotelId());
    final StayInfoResponseDto finalStayInfoResponse = stayInfoResponse;
    final StayInfoResponseDto finalStayInfoResponse1 = stayInfoResponse;
    var rateNotes = rateInformation.getRateClassifications().stream()
        .filter(rateClassification -> Objects.equals(rateClassification.getRateName(),
            finalStayInfoResponse.getReservationDetails().getRateText()))
        .filter(rateClassification -> Objects.equals(rateClassification.getRateClassification(),
            finalStayInfoResponse1.getReservationDetails().getRateClass()))
        .map(RateClassificationDto::getRateNotes)
        .findFirst()
        .orElse("");
    stayInfoResponse.getReservationDetails().setRateDescription(rateNotes);

    if (stayInfoResponse.getReservationDetails().getCancelable() != null
           &&  stayInfoResponse.getReservationDetails().getCancelable()
           &&  stayInfoResponse.getReservationDetails().getRooms().size() > 1
           && getAccessLevel().equals(GUEST)) {
      var res = stayInfoResponse.getReservationDetails();
      res.setCancelable(false);
      stayInfoResponse = StayInfoResponseDto.builder().reservationDetails(res).build();
    }
    return stayResponseMapper.toModel(stayInfoResponse);
  }

  @Override
  public CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest,
      BookingChannel bookingChannel) {
    var cancelBookingResponseDto = bookingClient.cancelBooking(
        stayRequestMapper.toBartStayCodeDto(bookingChannel), cancelBookingRequest.getCountry(),
        cancelBookingRequest.getLanguage(), cancelBookingRequest.getBookingReference(),
        cancelBookingRequest.getArrivalDate());
    return stayResponseMapper.toModel(cancelBookingResponseDto);
  }

  @Override
  public void sendBookingConfirmationEmail(ResendConfirmationEmailRequest request) {
    var confirmationRequest = stayRequestMapper.toDto(request);
    bookingClient.sendBookingConfirmationEmail(confirmationRequest);
  }

  @Override
  public void resendBookingInvoiceEmail(ResendInvoiceEmailRequest request, String authorization,
      BookingChannel bookingChannel) {
    var sessionId = tokenService.retrieveAndVerifyToken(authorization).orElseThrow();
    var invoiceRequest = stayRequestMapper.toDto(request, sessionId);
    var stayChannelCode = stayRequestMapper.toBartStayCodeDto(bookingChannel);
    bookingClient.resendBookingInvoiceEmail(stayChannelCode, invoiceRequest);
  }

  private String getAccessLevel() {
    if (!authenticatedUserService.isUserAuthenticated()) {
      var exception = new AuthorizationException(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION,
          NOT_AUTHORIZED);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return authenticatedUserService.getAuthenticatedUser().getAccount().getAccessLevel();
  }

}
