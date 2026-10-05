package uk.co.whitbread.booking.infrastructure.rest.client.booking.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.BookingConfirmationException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.BookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.InternalBookingException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayConfirmationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayInvoiceRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.out.EmailStayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.BookingStatusStayDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.ConfirmationTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.FilterTypeStaysDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.SortOrderStayDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.StayRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.StayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.in.StayInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties.BookingProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class BookingClientTest {

  private static final String AUTHORIZATION = "authorization";
  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String BOOKING_REFERENCE = "bookingReference";
  private static final String INVOICE_RECORD_NUMBER = "15";
  private static final String SURNAME = "surname";
  private static final String ARRIVAL = "2023-11-25";
  private static final String EMAIL = "test@mail.com";
  private static final String SESSION_ID = "sessionId";
  private static final String BOOKING_CHANNEL = "channel";
  private static final String COUNTRY = "country";
  private static final String LANGUAGE = "language";

  @Mock
  BookingProperties properties;
  @InjectMocks
  private BookingClient bookingClient;
  @InjectMocks
  private HotelAccountClient hotelAccountClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void getCustomerBookings_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(StayResponseDto.class)).thenReturn(
        Mono.just(new StayResponseDto()));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = hotelAccountClient.getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, mockStayRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getCustomerBookings_throwsBookingException() {
    // Arrange
    var mockStayRequest = StayRequestDto.builder().build();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Assert
    assertThrows(InternalBookingException.class,
        () -> hotelAccountClient.getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, mockStayRequest));
  }

  @Test
  void getBokingInformation_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(StayInfoResponseDto.class)).thenReturn(
        Mono.just(new StayInfoResponseDto()));

    // Act
    var response = bookingClient.getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
        BOOKING_REFERENCE, mockStayInfoRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getBookingInformation_throwsBookingException() {
    var mockStayInfoRequest = StayInfoRequestDto.builder().build();

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Assert
    assertThrows(InternalBookingException.class,
        () -> bookingClient.getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
            BOOKING_REFERENCE, mockStayInfoRequest));
  }

  @Test
  void cancelBooking_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancelBookingResponseDto.class)).thenReturn(
        Mono.just(new CancelBookingResponseDto()));

    // Act
    var response = bookingClient.cancelBooking(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
        BOOKING_REFERENCE, ARRIVAL);

    // Assert
    assertNotNull(response);
  }

  @Test
  void cancelBooking_throwsBookingException() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Assert
    assertThrows(InternalBookingException.class,
        () -> bookingClient.cancelBooking(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
            BOOKING_REFERENCE, ARRIVAL));
  }

  @Test
  void sendBookingConfirmationEmail_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getSendConfirmationEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(EmailStayResponseDto.class)).thenReturn(
        Mono.just(new EmailStayResponseDto(true)));

    // Act
    var response = bookingClient.sendBookingConfirmationEmail(mockBookingConfirmationRequestDto());

    // Assert
    assertTrue(response.isSuccess());
  }

  @Test
  void sendBookingConfirmationEmail_failed() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getSendConfirmationEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(EmailStayResponseDto.class)).thenReturn(
        Mono.just(new EmailStayResponseDto(false)));

    // Act
    var response = bookingClient.sendBookingConfirmationEmail(mockBookingConfirmationRequestDto());

    // Assert
    assertFalse(response.isSuccess());
  }

  @Test
  void sendBookingConfirmationEmail_throwsBookingConfirmationException() {
    var mockBookingConfirmationRequestDto = EmailStayConfirmationRequestDto.builder().build();

    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getSendConfirmationEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Assert
    assertThrows(BookingConfirmationException.class,
        () -> bookingClient.sendBookingConfirmationEmail(mockBookingConfirmationRequestDto));

  }

  @Test
  void sendBookingInvoiceEmail_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getSendInvoiceEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(EmailStayResponseDto.class)).thenReturn(
        Mono.just(new EmailStayResponseDto(true)));

    // Act
    var response = bookingClient.resendBookingInvoiceEmail(BOOKING_CHANNEL, mockBookingInvoiceRequestDto());

    // Assert
    assertTrue(response.isSuccess());
  }

  @Test
  void sendBookingInvoiceEmail_failed() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getSendInvoiceEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(EmailStayResponseDto.class)).thenReturn(
        Mono.just(new EmailStayResponseDto(false)));

    // Act
    var response = bookingClient.resendBookingInvoiceEmail(BOOKING_CHANNEL, mockBookingInvoiceRequestDto());

    // Assert
    assertFalse(response.isSuccess());
  }

  @Test
  void sendBookingInvoiceEmail_throwsBookingInvoiceException() {
    var mockBookingInvoiceRequestDto = EmailStayInvoiceRequestDto.builder().build();

    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getSendInvoiceEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Assert
    assertThrows(BookingInvoiceException.class,
        () -> bookingClient.resendBookingInvoiceEmail(BOOKING_CHANNEL, mockBookingInvoiceRequestDto));

  }

  private StayRequestDto mockStayRequest() {

    return StayRequestDto.builder().typeOfBooking(BookingStatusStayDto.CANCELLED).filterType(
            FilterTypeStaysDto.ARRIVAL_DATE).employeeId(EMPLOYEE_ID).companyId(COMPANY_ID)
        .filterValue("filterValue").sortOrder(SortOrderStayDto.DEFAULT).build();
  }

  private StayInfoRequestDto mockStayInfoRequest() {
    return StayInfoRequestDto
        .builder()
        .surname(SURNAME)
        .arrival(ARRIVAL)
        .build();
  }

  private EmailStayConfirmationRequestDto mockBookingConfirmationRequestDto() {
    return EmailStayConfirmationRequestDto.builder()
        .destination(EMAIL)
        .sessionId(SESSION_ID)
        .type(ConfirmationTypeDto.EMAIL)
        .build();
  }

  private EmailStayInvoiceRequestDto mockBookingInvoiceRequestDto() {
    return EmailStayInvoiceRequestDto.builder()
        .emailAddress(EMAIL)
        .sessionId(SESSION_ID)
        .invoiceRecordNumber(INVOICE_RECORD_NUMBER)
        .build();
  }
}
