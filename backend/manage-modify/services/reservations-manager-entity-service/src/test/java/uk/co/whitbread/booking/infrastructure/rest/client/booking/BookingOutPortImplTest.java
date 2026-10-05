package uk.co.whitbread.booking.infrastructure.rest.client.booking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.exceptions.AuthorizationException;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.history.in.BookingStatus;
import uk.co.whitbread.booking.domain.model.history.in.FilterTypes;
import uk.co.whitbread.booking.domain.model.history.in.SortOrder;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.history.out.TypesTotals;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingPrice;
import uk.co.whitbread.booking.domain.model.information.out.BookingRoom;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.CancellationInfoResponse;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.BookingConfirmationException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.BookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper.StayRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper.StayResponseMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayConfirmationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayInvoiceRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.out.EmailStayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.BookingStatusStayDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.ConfirmationTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.FilterTypeStaysDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.SortOrderStayDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.StayRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.StayDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.StayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.TypesTotalsStaysDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.in.StayInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayGuestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayPriceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayRoomDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayUpsellBreakdown;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out.StayUpsellItemDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.BookingClient;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.HotelAccountClient;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.RateClassificationDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.RateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(MockitoExtension.class)
class BookingOutPortImplTest {

  private static final String AUTHORIZATION = "authorization";
  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String BOOKING_REFERENCE = "bookingReference";
  private static final String INVOICE_RECORD_NUMBER = "15";
  private static final String SURNAME = "surname";
  private static final String ARRIVAL = "2023-11-25";
  private static final String HOTEL_ID = "hotelId";
  private static final String EMAIL = "test@mail.com";
  private static final String SESSION_ID = "sessionId";
  private static final String BOOKING_SUBCHANNEL = "subchannel";
  private static final String BOOKING_CHANNEL = "channel";
  private static final String COUNTRY = "country";
  private static final String LANGUAGE = "language";

  @Mock
  private BookingClient bookingClient;
  @Mock
  private HotelAccountClient hotelAccountClient;
  @Mock
  private ContentClient contentClient;
  @Mock
  private StayResponseMapper stayResponseMapper;
  @Mock
  private StayRequestMapper stayRequestMapper;
  @Mock
  private TokenService tokenService;
  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @InjectMocks
  private BookingOutPortImpl bookingOutService;


  @Test
  void findPiCustomerBookingsHistory__noBookingsFound() {
    // Arrange
    given(hotelAccountClient.getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL,
        stayPiRequest())).willReturn(
        stayResponse());
    given(stayRequestMapper.toDto(bookingPiRequest())).willReturn(stayPiRequest());
    given(stayResponseMapper.toModel(stayResponse())).willReturn(emptyBookingResponse());
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);

    // Act
    var result = bookingOutService.getBookings(AUTHORIZATION, bookingPiRequest(), bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getBookings().isEmpty(), equalTo(true));
    verify(hotelAccountClient).getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, stayPiRequest());
    verify(stayResponseMapper).toModel((StayResponseDto) any());
    verify(stayRequestMapper).toDto((BookingRequest) any());
  }

  @Test
  void findPiCustomerBookingsHistory__success() {
    // Arrange
    given(hotelAccountClient.getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, stayPiRequest()))
        .willReturn(stayResponse());
    given(stayRequestMapper.toDto((BookingRequest) any())).willReturn(stayPiRequest());
    given(stayResponseMapper.toModel(stayResponse())).willReturn(bookingResponse());
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);

    // Act
    var result = bookingOutService.getBookings(AUTHORIZATION, bookingPiRequest(), bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getBookings().isEmpty(), equalTo(false));
    verify(hotelAccountClient).getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, stayPiRequest());
    verify(stayResponseMapper).toModel((StayResponseDto) any());
    verify(stayRequestMapper).toDto((BookingRequest) any());
  }

  @Test
  void findBbCustomerBookingsHistory__success() {
    var employeeDetails = new EmployeeDetails(COMPANY_ID, EMPLOYEE_ID);
    // Arrange
    given(hotelAccountClient.getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, stayBbRequest()))
        .willReturn(stayResponse());
    given(tokenService.retrieveEmployeeDetailsAndVerifyToken(AUTHORIZATION)).willReturn(
        employeeDetails);
    given(stayRequestMapper.toDto(bookingBbRequest(), employeeDetails)).willReturn(stayBbRequest());
    given(stayResponseMapper.toModel(stayResponse())).willReturn(bookingResponse());
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);

    // Act
    var result = bookingOutService.getBookings(AUTHORIZATION, bookingBbRequest(), bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertFalse(result.getBookings().isEmpty());
    verify(hotelAccountClient).getCustomerBookings(AUTHORIZATION, BOOKING_CHANNEL, stayBbRequest());
    verify(stayResponseMapper).toModel((StayResponseDto) any());
    verify(stayRequestMapper).toDto(bookingBbRequest(), employeeDetails);
  }

  @Test
  void getBookingInformation__noBookingsFound() {
    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(
        new CustomJwtAuthenticationToken(getJwt(),
            getAccount()));
    given(bookingClient.getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE, BOOKING_REFERENCE,
        stayInfoRequest()))
        .willReturn(stayInfoResponse());
    given(stayRequestMapper.toDto(bookingInfoRequest())).willReturn(stayInfoRequest());
    given(stayResponseMapper.toModel((StayInfoResponseDto) any())).willReturn(
        BookingInfoResponse.builder().build());
    given(contentClient.getHotelRateInformation(COUNTRY, LANGUAGE, HOTEL_ID)).willReturn(
        rateInformationResponse());
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);

    // Act
    var result = bookingOutService.getBookingInformation(bookingInfoRequest(), bookingChannel());

    // Assert
    assertThat(result.getReservationDetails(), nullValue());
    verify(bookingClient).getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
        BOOKING_REFERENCE, stayInfoRequest());
    verify(stayResponseMapper).toModel((StayInfoResponseDto) any());
    verify(stayRequestMapper).toBartStayCodeDto(bookingChannel());
    verify(stayRequestMapper).toDto((BookingInfoRequest) any());
  }

  @Test
  void getBookingInformation__success() {

    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(
        new CustomJwtAuthenticationToken(getJwt(),
            getAccount()));
    given(bookingClient.getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE, BOOKING_REFERENCE,
        stayInfoRequest()))
        .willReturn(stayInfoResponse());

    given(bookingClient.getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE, BOOKING_REFERENCE,
        stayInfoRequest()))
        .willReturn(stayInfoResponse());
    given(stayRequestMapper.toDto(bookingInfoRequest())).willReturn(stayInfoRequest());
    given(stayResponseMapper.toModel((StayInfoResponseDto) any())).willReturn(
        bookingInfoResponse());
    given(contentClient.getHotelRateInformation(COUNTRY, LANGUAGE, HOTEL_ID)).willReturn(
        rateInformationResponse());
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);

    // Act
    var result = bookingOutService.getBookingInformation(bookingInfoRequest(), bookingChannel());

    // Assert
    assertThat(result.getReservationDetails(), notNullValue());
    verify(bookingClient).getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
        BOOKING_REFERENCE, stayInfoRequest());
    verify(stayResponseMapper).toModel((StayInfoResponseDto) any());
    verify(stayRequestMapper).toBartStayCodeDto(bookingChannel());
    verify(stayRequestMapper).toDto((BookingInfoRequest) any());
  }

  @Test
  void getBookingInformation__bookingInformationFoundDisallowCancel() {

    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(
        new CustomJwtAuthenticationToken(getJwt(),
            getAccountGuest()));
    given(bookingClient.getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE, BOOKING_REFERENCE,
        stayInfoRequest()))
        .willReturn(stayInfoResponse());

    given(contentClient.getHotelRateInformation(COUNTRY, LANGUAGE, HOTEL_ID)).willReturn(
        rateInformationResponse());

    given(stayRequestMapper.toDto(bookingInfoRequest())).willReturn(stayInfoRequest());
    given(stayResponseMapper.toModel((StayInfoResponseDto) any())).willReturn(
        bookingInfoResponse());
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);

    // Act
    var result = bookingOutService.getBookingInformation(bookingInfoRequest(), bookingChannel());

    // Assert
    assertThat(result.getReservationDetails(), notNullValue());
    verify(bookingClient).getBookingInformation(BOOKING_CHANNEL, COUNTRY, LANGUAGE,
        BOOKING_REFERENCE, stayInfoRequest());
  }

  @Test
  void sendBookingConfirmationEmail_success() {
    // Arrange
    var bookingConfirmationRequest = bookingConfirmationRequest();
    var bookingConfirmationRequestDto = bookingConfirmationRequestDto();
    given(bookingClient.sendBookingConfirmationEmail(bookingConfirmationRequestDto))
        .willReturn(emailStayResponseDto(true));
    given(stayRequestMapper.toDto(bookingConfirmationRequest)).willReturn(
        bookingConfirmationRequestDto);

    // Act
    // Assert
    assertDoesNotThrow(
        () -> bookingOutService.sendBookingConfirmationEmail(bookingConfirmationRequest));
  }


  @Test
  void sendBookingConfirmationEmail_failedConfirmation() {
    // Arrange
    var bookingConfirmationRequest = bookingConfirmationRequest();
    var bookingConfirmationRequestDto = bookingConfirmationRequestDto();
    given(bookingClient.sendBookingConfirmationEmail(bookingConfirmationRequestDto))
        .willThrow(
            new BookingConfirmationException(
                ErrorCode.DIGITAL_GET_OPERA_BOOKING_EXCEPTION.getMessage(),
                "message", ErrorCode.DIGITAL_GET_OPERA_BOOKING_EXCEPTION.getCode()));
    given(stayRequestMapper.toDto((bookingConfirmationRequest))).willReturn(
        bookingConfirmationRequestDto);

    // Act
    // Assert
    assertThrows(BookingConfirmationException.class,
        () -> bookingOutService.sendBookingConfirmationEmail(bookingConfirmationRequest));
  }

  @Test
  void sendBookingInvoiceEmail_success() {
    // Arrange
    var bookingInvoiceRequest = bookingInvoiceRequest();
    var bookingInvoiceRequestDto = bookingInvoiceRequestDto();
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);
    given(bookingClient.resendBookingInvoiceEmail(BOOKING_CHANNEL, bookingInvoiceRequestDto))
        .willReturn(emailStayResponseDto(true));
    given(tokenService.retrieveAndVerifyToken(AUTHORIZATION))
        .willReturn(Optional.of(SESSION_ID));
    given(stayRequestMapper.toDto(bookingInvoiceRequest, SESSION_ID)).willReturn(
        bookingInvoiceRequestDto);

    // Act
    // Assert
    assertDoesNotThrow(
        () -> bookingOutService.resendBookingInvoiceEmail(bookingInvoiceRequest, AUTHORIZATION,
            bookingChannel()));
  }

  @Test
  void sendBookingInvoiceEmail_failedResendInvoice() {
    var bookingInvoiceRequest = bookingInvoiceRequest();
    var bookingInvoiceRequestDto = bookingInvoiceRequestDto();

    // Arrange
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);
    given(bookingClient.resendBookingInvoiceEmail(BOOKING_CHANNEL, bookingInvoiceRequestDto))
        .willThrow(
            new BookingInvoiceException(ErrorCode.DIGITAL_GET_OPERA_BOOKING_EXCEPTION.getMessage(),
                "message", ErrorCode.DIGITAL_GET_OPERA_BOOKING_EXCEPTION.getCode()));
    given(stayRequestMapper.toDto(bookingInvoiceRequest, SESSION_ID)).willReturn(
        bookingInvoiceRequestDto);
    given(tokenService.retrieveAndVerifyToken(AUTHORIZATION))
        .willReturn(Optional.of(SESSION_ID));

    // Act
    // Assert
    var bookingChannel = bookingChannel();
    assertThrows(BookingInvoiceException.class,
        () -> bookingOutService.resendBookingInvoiceEmail(bookingInvoiceRequest, AUTHORIZATION,
            bookingChannel));
  }

  @Test
  void cancelBooking_success() {
    var cancelBookingResponse = CancelBookingResponse.builder().build();
    var cancelBookingResponseDto = CancelBookingResponseDto.builder().build();

    // Arrange
    given(stayRequestMapper.toBartStayCodeDto(bookingChannel())).willReturn(BOOKING_CHANNEL);
    given(
        bookingClient.cancelBooking(BOOKING_CHANNEL, COUNTRY, LANGUAGE, BOOKING_REFERENCE, ARRIVAL))
        .willReturn(cancelBookingResponseDto);
    given(stayResponseMapper.toModel(cancelBookingResponseDto)).willReturn(cancelBookingResponse);

    // Act
    var response = bookingOutService.cancelBooking(cancelBookingRequest(), bookingChannel());

    // Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getAccessLevelTest(boolean authenticated) {
    // Arrange
    given(authenticatedUserService.isUserAuthenticated()).willReturn(authenticated);
    if (authenticated) {
      given(authenticatedUserService.getAuthenticatedUser()).willReturn(
          new CustomJwtAuthenticationToken(getJwt(), getAccount()));
    }
    // Act
    if (!authenticated) {
      var exception = assertThrows(AuthorizationException.class,
          () -> {
            ReflectionTestUtils.invokeMethod(bookingOutService, "getAccessLevel");
          });
      assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getCode(),
          exception.getErrorCode());
    } else {
      String result = ReflectionTestUtils.invokeMethod(bookingOutService, "getAccessLevel");
      //Assert
      assertNotNull(result);
    }
  }

  private BookingChannel bookingChannel() {
    return BookingChannel.builder()
        .channel(BOOKING_CHANNEL)
        .subchannel(BOOKING_SUBCHANNEL)
        .language(LANGUAGE)
        .build();
  }

  private ResendConfirmationEmailRequest bookingConfirmationRequest() {
    return ResendConfirmationEmailRequest.builder()
        .email(EMAIL)
        .hotelId(HOTEL_ID)
        .sessionId(SESSION_ID)
        .build();
  }

  private ResendInvoiceEmailRequest bookingInvoiceRequest() {
    return ResendInvoiceEmailRequest.builder()
        .language(LANGUAGE)
        .email(EMAIL)
        .hotelId(HOTEL_ID)
        .invoiceRecordNumber(INVOICE_RECORD_NUMBER)
        .build();
  }

  private EmailStayConfirmationRequestDto bookingConfirmationRequestDto() {
    return EmailStayConfirmationRequestDto.builder()
        .destination(EMAIL)
        .sessionId(SESSION_ID)
        .type(ConfirmationTypeDto.EMAIL)
        .build();
  }

  private EmailStayInvoiceRequestDto bookingInvoiceRequestDto() {
    return EmailStayInvoiceRequestDto.builder()
        .emailAddress(EMAIL)
        .sessionId(SESSION_ID)
        .invoiceRecordNumber(INVOICE_RECORD_NUMBER)
        .build();
  }

  private EmailStayResponseDto emailStayResponseDto(boolean success) {
    return EmailStayResponseDto.builder()
        .success(success)
        .build();
  }

  private StayRequestDto stayPiRequest() {

    return StayRequestDto
        .builder()
        .typeOfBooking(BookingStatusStayDto.CANCELLED)
        .filterType(FilterTypeStaysDto.ARRIVAL_DATE).employeeId(EMPLOYEE_ID)
        .companyId(COMPANY_ID)
        .filterValue("filterValue")
        .sortOrder(SortOrderStayDto.DEFAULT)
        .build();
  }

  private StayRequestDto stayBbRequest() {

    return StayRequestDto
        .builder()
        .business(true)
        .typeOfBooking(BookingStatusStayDto.CANCELLED)
        .filterType(FilterTypeStaysDto.ARRIVAL_DATE)
        .employeeId(EMPLOYEE_ID)
        .companyId(COMPANY_ID)
        .filterValue("filterValue")
        .sortOrder(SortOrderStayDto.DEFAULT)
        .build();
  }

  private BookingRequest bookingPiRequest() {

    return BookingRequest.builder()
        .typeOfBooking(BookingStatus.PAST).filterType(FilterTypes.NAME)
        .sortOrder(SortOrder.DEFAULT)
        .build();
  }

  private BookingRequest bookingBbRequest() {

    return BookingRequest
        .builder()
        .business(true)
        .typeOfBooking(BookingStatus.PAST).filterType(FilterTypes.NAME)
        .sortOrder(SortOrder.DEFAULT)
        .build();
  }

  private BookingResponse bookingResponse() {
    return BookingResponse.builder()
        .bookings(List.of(new Booking()))
        .totals(TypesTotals
            .builder()
            .build())
        .build();
  }

  private BookingResponse emptyBookingResponse() {
    return BookingResponse.builder()
        .bookings(List.of())
        .totals(TypesTotals
            .builder()
            .build())
        .build();
  }

  private StayResponseDto stayResponse() {

    return StayResponseDto.builder()
        .pageIndex(1)
        .pageSize(10)
        .continuationToken("continuationToken")
        .totalSize(10)
        .totals(TypesTotalsStaysDto.builder().build())
        .stays(List.of(StayDto
            .builder()
            .bookingStatus(BookingStatusStayDto.FUTURE)
            .build()))
        .build();
  }

  private StayInfoRequestDto stayInfoRequest() {
    return StayInfoRequestDto
        .builder()
        .surname(SURNAME)
        .arrival(ARRIVAL)
        .build();
  }

  private BookingDetails bookingDetails() {

    return BookingDetails
        .builder()
        .rooms(List.of(BookingRoom
            .builder()
            .adultsMeal(Set.of(BookingPackagesDetails
                .builder()
                .build()))
            .kidsMeal(Set.of(BookingPackagesDetails
                .builder()
                .build()))
            .build()))
        .outstandingAmount(BookingPrice
            .builder()
            .build())
        .donationsPackage(BookingPackagesDetails
            .builder()
            .build())
        .prepaidAmount(BookingPrice
            .builder()
            .build())
        .cancellationInfoResponse(CancellationInfoResponse
            .builder()
            .build())
        .build();
  }

  private BookingInfoResponse bookingInfoResponse() {

    return BookingInfoResponse
        .builder()
        .reservationDetails(bookingDetails())
        .checkInTime(LocalTime.now())
        .checkOutTime(LocalTime.now())
        .build();
  }

  private StayInfoResponseDto stayInfoResponse() {

    return StayInfoResponseDto
        .builder()
        .checkOutTime(LocalTime.now())
        .checkOutTime(LocalTime.NOON)
        .reservationDetails(StayDetailsDto
            .builder()
            .cancelable(true)
            .cityTax(StayPriceDto
                .builder()
                .build())
            .roomCost(StayPriceDto
                .builder()
                .build())
            .prepaidAmount(StayPriceDto
                .builder()
                .build())
            .upsellBreakdown(StayUpsellBreakdown
                .builder()
                .upsellItems(List.of(StayUpsellItemDto
                    .builder()
                    .build()))
                .build())
            .rooms(List.of(StayRoomDto
                .builder()
                .roomCost(StayPriceDto
                    .builder()
                    .build())
                .guest(StayGuestDto
                    .builder()
                    .build())
                .build(),
                StayRoomDto
                .builder()
                .roomCost(StayPriceDto
                    .builder()
                    .build())
                .guest(StayGuestDto
                    .builder()
                    .build())
                .build()))
            .build())
        .build();
  }

  private RateInformationResponseDto rateInformationResponse() {

    return RateInformationResponseDto.builder()
        .rateClassifications(
          List.of(RateClassificationDto.builder()
              .rateDescription("rateDescription")
              .rateClassification("rateClassification")
              .rateName("rateName")
              .rateNotes("rateNotes").build()))
        .build();
  }

  private BookingInfoRequest bookingInfoRequest() {

    return BookingInfoRequest
        .builder()
        .country(COUNTRY)
        .language(LANGUAGE)
        .bookingReference(BOOKING_REFERENCE)
        .arrival(ARRIVAL)
        .hotelId(HOTEL_ID)
        .surname(SURNAME)
        .build();
  }

  private CancelBookingRequest cancelBookingRequest() {
    return CancelBookingRequest
        .builder()
        .country(COUNTRY)
        .language(LANGUAGE)
        .bookingReference(BOOKING_REFERENCE)
        .arrivalDate(ARRIVAL)
        .build();
  }

  private Jwt getJwt(){
    return new Jwt("authorization", Instant.now(), Instant.now().plusSeconds(60),
            Map.of("header1", "header2"), Map.of("Claim1", "Claim2"));
  }
  private Account getAccount() {
    return Account.builder()
            .bartId("187")
            .operaCompanyId("7765828")
            .companyId("5445")
            .accessLevel("SELF")
            .email("finalEmail")
            .customerId("5445")
            .employeeId("1")
            .build();
  }

  private Account getAccountGuest() {
    return Account.builder()
            .bartId("187")
            .operaCompanyId("7765828")
            .companyId("5445")
            .accessLevel("STAYER")
            .email("finalEmail")
            .customerId("5445")
            .employeeId("1")
            .build();
  }

}
