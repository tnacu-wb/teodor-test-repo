package uk.co.whitbread.booking.infrastructure.rest.client.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.BookingConfirmationException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.BookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.InternalBookingException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayConfirmationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayInvoiceRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.out.EmailStayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.in.StayInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties.BookingProperties;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingClient {

  private static final String ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG =
      "An exception was returned by Account Service";
  private static final String BOOKING_CHANNEL = "bookingChannel";
  private final WebClient bookingWebClient;
  private final BookingProperties bookingProperties;

  public StayInfoResponseDto getBookingInformation(String channelCode, String countryCode,
      String languageCode,
      String bookingReference,
      StayInfoRequestDto request
  ) {
    return bookingWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(bookingProperties.getSingleBookingEndpoint())
            .queryParam("arrival", request.getArrival())
            .queryParam("surname", request.getSurname())
            .build(bookingReference))
        .headers(httpHeaders -> {
          httpHeaders.set(BOOKING_CHANNEL, channelCode);
          httpHeaders.set("country", countryCode);
          httpHeaders.set("language", languageCode);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new InternalBookingException(ErrorCode.ACCOUNTS_BOOKING_INFO_EXCEPTION.getMessage(),
                  String.format(
                      "Error while trying to get booking by bookingReference=%s, surname =%s, arrivalDate=%s. %s",
                      bookingReference, request.getSurname(), request.getArrival(),
                      ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG),
                  ErrorCode.ACCOUNTS_BOOKING_INFO_EXCEPTION.getCode()));
        })
        .bodyToMono(StayInfoResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public CancelBookingResponseDto cancelBooking(String channelCode, String countryCode, String languageCode,
      String bookingReference, String arrival
  ) {
    return bookingWebClient
        .delete()
        .uri(uriBuilder -> uriBuilder.path(bookingProperties.getCancelBookingEndpoint())
            .queryParam("arrival", arrival)
            .build(bookingReference))
        .headers(httpHeaders -> {
          httpHeaders.set(BOOKING_CHANNEL, channelCode);
          httpHeaders.set("country", countryCode);
          httpHeaders.set("language", languageCode);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(InternalBookingException.class);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new InternalBookingException(ErrorCode.ACCOUNTS_BOOKING_CANCEL_EXCEPTION.getMessage(),
                  String.format("Could not cancel the reservation by bookingReference=%s. %s",
                      bookingReference, ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG),
                  ErrorCode.ACCOUNTS_BOOKING_CANCEL_EXCEPTION.getCode()));
        })
        .bodyToMono(CancelBookingResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public EmailStayResponseDto sendBookingConfirmationEmail(
      EmailStayConfirmationRequestDto stayConfirmationRequestDto) {

    return bookingWebClient
        .post()
        .uri(bookingProperties.getSendConfirmationEmailEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(stayConfirmationRequestDto), EmailStayConfirmationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new BookingConfirmationException(ErrorCode.SEND_BOOKING_EMAL_EXCEPTION.getMessage(),
                  String.format(
                      "Error while trying to resend confirmation email for email address=%s. %s",
                      stayConfirmationRequestDto.getDestination(),
                      ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG),
                  ErrorCode.SEND_BOOKING_EMAL_EXCEPTION.getCode()));
        })
        .bodyToMono(EmailStayResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public EmailStayResponseDto resendBookingInvoiceEmail(
      String channelCode,
      EmailStayInvoiceRequestDto bookingInvoiceEmailRequest) {

    return bookingWebClient
        .post()
        .uri(bookingProperties.getSendInvoiceEmailEndpoint())
        .headers(httpHeaders -> httpHeaders.set(BOOKING_CHANNEL, channelCode))
        .body(Mono.just(bookingInvoiceEmailRequest), EmailStayInvoiceRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new BookingInvoiceException(
                  ErrorCode.RESEND_BOOKING_INVOICE_EMAIL_EXCEPTION.getMessage(),
                  String.format(
                      "Error while trying to resend invoice email for email address = %s. %s",
                      bookingInvoiceEmailRequest.getEmailAddress(),
                      ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG),
                  ErrorCode.RESEND_BOOKING_INVOICE_EMAIL_EXCEPTION.getCode()
              ));
        })
        .bodyToMono(EmailStayResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
