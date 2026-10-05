package uk.co.whitbread.booking.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.exceptions.AuthorizationException;
import uk.co.whitbread.booking.domain.model.exceptions.BookingValidationException;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
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
import uk.co.whitbread.booking.domain.model.information.out.GalleryImage;
import uk.co.whitbread.booking.domain.model.information.out.HotelInformationResponse;
import uk.co.whitbread.booking.domain.model.information.out.Links;
import uk.co.whitbread.booking.domain.model.information.out.ThumbnailImage;
import uk.co.whitbread.booking.domain.model.information.out.TopSectionImage;
import uk.co.whitbread.booking.domain.model.information.out.UpcomingBookings;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.migration.out.PmsSource;
import uk.co.whitbread.booking.domain.model.upcoming.in.UpcomingBookingRequest;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsCdhResponse;
import uk.co.whitbread.booking.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.BookingOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.ReservationOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties.ReservationsManagerProperties;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBooking;

@ExtendWith(MockitoExtension.class)
class BookingInPortImplTest {

  private static final String AUTHORIZATION = "authorization";
  private static final String BOOKING_REFERENCE = "bookingReference";
  private static final String SURNAME = "surname";
  private static final String ARRIVAL = "2023-11-25";
  private static final String HOTEL_ID = "hotelId";
  private static final String EMAIL = "test@mail.com";
  private static final String SESSION_ID = "sessionId";
  private static final String BOOKING_SUBCHANNEL = "subchannel";
  private static final String BOOKING_CHANNEL = "channel";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String COMPANY_ID = "companyId";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Mock
  private BookingOutPortImpl bookingOutPort;
  @Mock
  private ReservationOutPortImpl operaBookingOutPort;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private ReservationsManagerProperties reservationsManagerProperties;
  @Mock
  private CheckInOnlineLogic checkInOnlineLogic;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private CdhOutPort cdhOutPort;
  @Mock
  private DigitalKeyFeature digitalKeyFeature;

  @InjectMocks
  private BookingInPortImpl inPort;

  @Test
  void findCustomerBookingsHistory__noBookingsFound() {
    // Arrange
    given(bookingOutPort.getBookings(AUTHORIZATION, bookingRequest(), bookingChannel())).willReturn(
        emptyBookingResponse());
    // Act
    var result = inPort.getBookings(AUTHORIZATION, bookingRequest(), bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getBookings().isEmpty(), equalTo(true));
  }

  @Test
  void findCustomerBookingsHistory__success() {
    // Arrange
    given(bookingOutPort.getBookings(AUTHORIZATION, bookingRequest(), bookingChannel())).willReturn(
        bookingResponse());
    when(checkInOnlineLogic.filterByPibaCard(any(), any(), any())).thenReturn(
        filteredBookingResponse().getBookings());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPiBbMobileCheckInOnline()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileDigitalKey()))
        .thenReturn(true);
    // Act
    var result = inPort.getBookings(AUTHORIZATION, bookingRequest(), bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getBookings().isEmpty(), equalTo(false));
    assertThat(result.getBookings().size(), equalTo(1));
    assertThat(result.getBookings().get(0).getSourceSystem(), equalTo(PmsSource.OPERA.toString()));
    assertThat(result.getBookings().get(0),
        equalTo(filteredBookingResponse().getBookings().get(0)));
  }

  @Test
  void getOperaBookingInformation__success() {
    // Arrange
    var bookingInfoRequest = bookingInfoRequest();
    LocalDate arrivalDate = LocalDate.parse(bookingInfoRequest.getArrival(), DATE_FORMATTER);

    given(operaBookingOutPort.getOperaBookingInformation(bookingInfoRequest, bookingChannel())).willReturn(
        bookingInfoResponse());
    ReflectionTestUtils.setField(inPort, "oldBookingThreshold", LocalDate.now()
        .minusDays(arrivalDate.toEpochDay())
        .plusMonths(2)
        .getMonthValue());
    
    // Act
    var result = inPort.getBookingInformation(bookingInfoRequest, bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getReservationDetails(), notNullValue());
    verify(operaBookingOutPort).getOperaBookingInformation(bookingInfoRequest, bookingChannel());
  }

  @Test
  void getOperaBookingInformation__noBookingInformationFound() {
    // Arrange
    given(operaBookingOutPort.getOperaBookingInformation(bookingInfoRequest(), bookingChannel()))
        .willReturn(BookingInfoResponse.builder().reservationDetails(BookingDetails
            .builder().build()).build());
    ReflectionTestUtils.setField(inPort, "oldBookingThreshold", 2);

    // Act
    var result = inPort.getBookingInformation(bookingInfoRequest(), bookingChannel());

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getReservationDetails().getRooms(), nullValue());
  }

  @Test
  void cancelBookingFromOpera() {
    var bookingReference = "bookingRef";
    given(operaBookingOutPort.cancelBooking(any())).willReturn(
        CancelBookingResponse.builder().bookingReference(
            bookingReference).build());

    var cancelBookingResponse = inPort.cancelBooking(CancelBookingRequest.builder().build());
    assertThat(cancelBookingResponse.getBookingReference(), is(bookingReference));
  }

  @Test
  void sendOperaBookingConfirmationEmail_success() {
    // Arrange
    var bookingConfirmationRequest = bookingConfirmation();

    // Act
    inPort.sendBookingConfirmationEmail(bookingConfirmationRequest);

    // Assert
    verify(operaBookingOutPort).sendBookingConfirmationOrInvoiceEmail(bookingConfirmationRequest);
  }

  @Test
  void sendOperaBookingInvoiceEmail_success() {
    // Arrange
    var bookingInvoiceRequest = bookingInvoice();

    // Act
    inPort.resendBookingInvoiceEmail(bookingInvoiceRequest);

    // Assert
    verify(operaBookingOutPort).sendBookingConfirmationOrInvoiceEmail(bookingInvoiceRequest);
  }

  @Test
  void getUpcomingBookings_WhenInvalidAuthorizationEmployeeId_ThenExceptionThrown() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    mockAuthentication("", COMPANY_ID, EMAIL);

    var result = assertThrows(AuthorizationException.class,
          () -> inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION));

    assertNotNull(result.getDebugMessage());
    assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getMessage(), result.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getCode(), result.getErrorCode());
    verify(authenticatedUserService).getAuthenticatedUser();
  }

  @Test
  void getUpcomingBookings_WhenInvalidAuthorizationCompanyId_ThenExceptionThrown() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    mockAuthentication(EMPLOYEE_ID, "", EMAIL);

    var result = assertThrows(AuthorizationException.class,
          () -> inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION));

    assertNotNull(result.getDebugMessage());
    assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getMessage(), result.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getCode(), result.getErrorCode());
    verify(authenticatedUserService).getAuthenticatedUser();
  }

  @Test
  void getUpcomingBookings_WhenInvalidAuthorizationEmail_ThenExceptionThrown() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, "");

    var result = assertThrows(AuthorizationException.class,
          () -> inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION));

    assertNotNull(result.getDebugMessage());
    assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getMessage(), result.getGlobalErrTextTemplate());
    assertEquals(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION.getCode(), result.getErrorCode());
    verify(authenticatedUserService).getAuthenticatedUser();
  }

  @Test
  void getUpcomingBookings_WhenNoUpcomingBookingInCdh_ThenNoExceptionThrown() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
          .bookings(2)
          .stays(3)
          .build();
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsCdhResponse);

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(upcomingBookingsCdhResponse.getBookings(), result.getBookings());
    assertEquals(upcomingBookingsCdhResponse.getStays(), result.getStays());
    assertNull(result.getHotelName());
    assertNull(result.getArrivalDate());
    assertNull(result.getArrivalTime());
    assertNull(result.getDepartureDate());
    assertNull(result.getDepartureTime());
    assertNull(result.getBookingReference());
    assertNull(result.getBrand());
    assertNull(result.getLinks());
    assertNull(result.getGalleryImages());
    assertNull(result.getThumbnailImages());
    assertNull(result.getTopSectionImages());
  }

  @Test
  void getUpcomingBookings_WhenUpcomingBookingsArePresent_ThenNextBookingIsTheOneWithTheNearestArrivalDate() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    var upcomingBooking1 = UpcomingBooking.builder()
          .bookingReference("BOOK1")
          .hotelName("Some interesting hotel 1")
          .hotelCode("HOTEL1")
          .arrivalDate(OffsetDateTime.now().plusMonths(3))
          .departureDate(OffsetDateTime.now().plusMonths(3).plusDays(3))
          .build();
    var expectedUpcomingBooking = UpcomingBooking.builder()
          .bookingReference("BOOK2")
          .hotelName("Some interesting hotel 2")
          .hotelCode("HOTEL2")
          .arrivalDate(OffsetDateTime.now().plusMonths(1))
          .departureDate(OffsetDateTime.now().plusMonths(1).plusDays(2))
          .build();
    var upcomingBooking3 = UpcomingBooking.builder()
          .bookingReference("BOOK3")
          .hotelName("Some interesting hotel 3")
          .hotelCode("HOTEL3")
          .arrivalDate(OffsetDateTime.now().plusMonths(2))
          .departureDate(OffsetDateTime.now().plusMonths(2).plusDays(1))
          .build();
    var upcomingBookingsFromCdhList = List.of(upcomingBooking1, expectedUpcomingBooking, upcomingBooking3);
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
          .bookings(2)
          .stays(3)
          .upcomingBooking(upcomingBookingsFromCdhList)
          .build();
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsCdhResponse);

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(upcomingBookingsCdhResponse.getBookings(), result.getBookings());
    assertEquals(upcomingBookingsCdhResponse.getStays(), result.getStays());
    assertEquals(expectedUpcomingBooking.getHotelName(), result.getHotelName());
    assertEquals(expectedUpcomingBooking.getArrivalDate().toLocalDate(), result.getArrivalDate());
    assertNull(result.getArrivalTime());
    assertEquals(expectedUpcomingBooking.getDepartureDate().toLocalDate(), result.getDepartureDate());
    assertNull(result.getDepartureTime());
    assertEquals(expectedUpcomingBooking.getBookingReference(), result.getBookingReference());
    assertNull(result.getBrand());
    assertNull(result.getLinks());
    assertNull(result.getGalleryImages());
    assertNull(result.getThumbnailImages());
    assertNull(result.getTopSectionImages());
  }

  @Test
  void getUpcomingBookings_WhenNoExtraPackagesAdded_ThenArrivalAndDepartureTimeIsSetCorrectly() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    var expectedUpcomingBooking = UpcomingBooking.builder()
          .bookingReference("BOOK2")
          .hotelName("Some interesting hotel 2")
          .hotelCode("HOTEL2")
          .arrivalDate(OffsetDateTime.now().plusMonths(1))
          .departureDate(OffsetDateTime.now().plusMonths(1).plusDays(2))
          .build();
    var upcomingBookingsFromCdhList = List.of(expectedUpcomingBooking);
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
          .bookings(2)
          .stays(3)
          .upcomingBooking(upcomingBookingsFromCdhList)
          .build();
    var bookingRoom1 = new UpcomingBookings(
        LocalTime.of(10, 30).toString(),
        LocalTime.of(14, 30).toString(),
        Set.of(
            BookingPackagesDetails.builder().description("wrong early package").build()
        ),
        "not_me@email.com"
    );

    var expectedRoom = new UpcomingBookings(
        LocalTime.of(12, 0).toString(),
        LocalTime.of(14, 0).toString(),
        null,
        EMAIL
    );
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsCdhResponse);
    when(operaBookingOutPort.getStayDates(AUTHORIZATION, expectedUpcomingBooking.getBookingReference()))
          .thenReturn(List.of(bookingRoom1, expectedRoom));

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(upcomingBookingsCdhResponse.getBookings(), result.getBookings());
    assertEquals(upcomingBookingsCdhResponse.getStays(), result.getStays());
    assertEquals(expectedUpcomingBooking.getHotelName(), result.getHotelName());
    assertEquals(expectedUpcomingBooking.getArrivalDate().toLocalDate(), result.getArrivalDate());
    assertEquals(LocalTime.parse(expectedRoom.checkInTime()), result.getArrivalTime());
    assertEquals(expectedUpcomingBooking.getDepartureDate().toLocalDate(), result.getDepartureDate());
    assertEquals(LocalTime.parse(expectedRoom.checkOutTime()), result.getDepartureTime());
    assertEquals(expectedUpcomingBooking.getBookingReference(), result.getBookingReference());
  }

  @Test
  void getUpcomingBookings_WhenNoArrivalAndDepartureTimeExistsOnBooking_ThenTheyAreSetCorrectlyToDefaultValues() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
        .country("gb")
        .language("en")
        .channel("BB")
        .subchannel("WEB")
        .build();
    var expectedUpcomingBooking = UpcomingBooking.builder()
        .bookingReference("BOOK2")
        .hotelName("Some interesting hotel 2")
        .hotelCode("HOTEL2")
        .arrivalDate(OffsetDateTime.now().plusMonths(1))
        .departureDate(OffsetDateTime.now().plusMonths(1).plusDays(2))
        .build();
    var upcomingBookingsFromCdhList = List.of(expectedUpcomingBooking);
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
        .bookings(2)
        .stays(3)
        .upcomingBooking(upcomingBookingsFromCdhList)
        .build();
    var bookingRoom1 = new UpcomingBookings(
        null,
        null,
        Set.of(
            BookingPackagesDetails.builder().description("wrong early package").build()
        ),
        "not_me@email.com"
    );

    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
        .thenReturn(upcomingBookingsCdhResponse);
    when(operaBookingOutPort.getStayDates(AUTHORIZATION, expectedUpcomingBooking.getBookingReference()))
        .thenReturn(List.of(bookingRoom1));

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(LocalTime.of(15, 0), result.getArrivalTime());
    assertEquals(LocalTime.of(12, 0), result.getDepartureTime());
  }

  @Test
  void getUpcomingBookings_WhenExtraPackagesAdded_ThenArrivalAndDepartureTimeIsSetCorrectly() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    var expectedUpcomingBooking = UpcomingBooking.builder()
          .bookingReference("BOOK2")
          .hotelName("Some interesting hotel 2")
          .hotelCode("HOTEL2")
          .arrivalDate(OffsetDateTime.now().plusMonths(1))
          .departureDate(OffsetDateTime.now().plusMonths(1).plusDays(2))
          .build();
    var upcomingBookingsFromCdhList = List.of(expectedUpcomingBooking);
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
          .bookings(2)
          .stays(3)
          .upcomingBooking(upcomingBookingsFromCdhList)
          .build();
    var bookingRoom1 = new UpcomingBookings(
        LocalTime.of(10, 30).toString(),
        LocalTime.of(14, 30).toString(),
        Set.of(
            BookingPackagesDetails.builder().description("wrong early package").build()
        ),
        "not_me@email.com"
    );
    var expectedRoom = new UpcomingBookings(
        LocalTime.of(12, 0).toString(),
        LocalTime.of(17, 0).toString(),
        Set.of(
            BookingPackagesDetails
                .builder()
                .description("Early Check In")
                .packageCode("HSCKIN")
                .build(),
            BookingPackagesDetails
                .builder()
                .description("Late Check Out 2PM")
                .packageCode("HSCOU2")
                .build()
        ),
        EMAIL
    );
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsCdhResponse);
    when(operaBookingOutPort.getStayDates(AUTHORIZATION, expectedUpcomingBooking.getBookingReference()))
          .thenReturn(List.of(bookingRoom1, expectedRoom));

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(upcomingBookingsCdhResponse.getBookings(), result.getBookings());
    assertEquals(upcomingBookingsCdhResponse.getStays(), result.getStays());
    assertEquals(expectedUpcomingBooking.getHotelName(), result.getHotelName());
    assertEquals(expectedUpcomingBooking.getArrivalDate().toLocalDate(), result.getArrivalDate());
    assertEquals(LocalTime.of(11, 0), result.getArrivalTime());
    assertEquals(expectedUpcomingBooking.getDepartureDate().toLocalDate(), result.getDepartureDate());
    assertEquals(LocalTime.of(14, 0), result.getDepartureTime());
    assertEquals(expectedUpcomingBooking.getBookingReference(), result.getBookingReference());
  }

  @Test
  void getUpcomingBookings_WhenExtraPackagesAddedLateCheckOut4Pm_ThenArrivalAndDepartureTimeIsSetCorrectly() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    var expectedUpcomingBooking = UpcomingBooking.builder()
          .bookingReference("BOOK2")
          .hotelName("Some interesting hotel 2")
          .hotelCode("HOTEL2")
          .arrivalDate(OffsetDateTime.now().plusMonths(1))
          .departureDate(OffsetDateTime.now().plusMonths(1).plusDays(2))
          .build();
    var upcomingBookingsFromCdhList = List.of(expectedUpcomingBooking);
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
          .bookings(2)
          .stays(3)
          .upcomingBooking(upcomingBookingsFromCdhList)
          .build();
    var bookingRoom1 = new UpcomingBookings(
        LocalTime.of(10, 30).toString(),
        LocalTime.of(14, 30).toString(),
        Set.of(
            BookingPackagesDetails.builder().description("wrong early package").build()
        ),
        "not_me@email.com"
    );

    var expectedRoom = new UpcomingBookings(
        LocalTime.of(12, 0).toString(),
        LocalTime.of(17, 0).toString(),
        Set.of(
            BookingPackagesDetails
                .builder()
                .description("Early Check In")
                .packageCode("HSCKIN")
                .build(),
            BookingPackagesDetails
                .builder()
                .description("Late Check Out 4PM")
                .packageCode("HSCOU4")
                .build()
        ),
        EMAIL
    );
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsCdhResponse);
    when(operaBookingOutPort.getStayDates(AUTHORIZATION, expectedUpcomingBooking.getBookingReference()))
          .thenReturn(List.of(bookingRoom1, expectedRoom));

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(upcomingBookingsCdhResponse.getBookings(), result.getBookings());
    assertEquals(upcomingBookingsCdhResponse.getStays(), result.getStays());
    assertEquals(expectedUpcomingBooking.getHotelName(), result.getHotelName());
    assertEquals(expectedUpcomingBooking.getArrivalDate().toLocalDate(), result.getArrivalDate());
    assertEquals(LocalTime.of(11, 0), result.getArrivalTime());
    assertEquals(expectedUpcomingBooking.getDepartureDate().toLocalDate(), result.getDepartureDate());
    assertEquals(LocalTime.of(16, 0), result.getDepartureTime());
    assertEquals(expectedUpcomingBooking.getBookingReference(), result.getBookingReference());
  }

  @Test
  void getUpcomingBookings_WhenDataFetchedFromAEM_ThenResponseIsBuiltCorrectly() {
    var upcomingBookingsRequest = UpcomingBookingRequest.builder()
          .country("gb")
          .language("en")
          .channel("BB")
          .subchannel("WEB")
          .build();
    var expectedUpcomingBooking = UpcomingBooking.builder()
          .bookingReference("BOOK2")
          .hotelName("Some interesting hotel 2")
          .hotelCode("HOTEL2")
          .arrivalDate(OffsetDateTime.now().plusMonths(1))
          .departureDate(OffsetDateTime.now().plusMonths(1).plusDays(2))
          .build();
    var upcomingBookingsFromCdhList = List.of(expectedUpcomingBooking);
    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder()
          .bookings(2)
          .stays(3)
          .upcomingBooking(upcomingBookingsFromCdhList)
          .build();
    var bookingRoom1 = new UpcomingBookings(LocalTime.of(10, 30).toString(),
        LocalTime.of(14, 30).toString(),
        Set.of(BookingPackagesDetails.builder()
            .description("wrong early package")
            .build()), "not_me@email.com");
    var expectedRoom = new UpcomingBookings(
        LocalTime.of(12, 0).toString(),
        LocalTime.of(17, 0).toString(),
        Set.of(
            BookingPackagesDetails
                .builder()
                .description("Early Check In")
                .packageCode("HSCKIN")
                .build(),
            BookingPackagesDetails
                .builder()
                .description("Late Check Out 4PM")
                .packageCode("HSCOU4")
                .build()
        ),
        "test@mail.com"
    );
    var hotelInformationResponse = HotelInformationResponse.builder()
          .galleryImages(List.of(GalleryImage.builder().imageSrc("/src/image1.png").build()))
          .thumbnailImages(List.of(ThumbnailImage.builder().imageSrc("src/thumbnail.png").build()))
          .topSectionImages(List.of(TopSectionImage.builder().imageSrc("src/top.png").build()))
          .brand("BRAND")
          .links(Links.builder().detailsPage("/src/details.html").build())
          .build();
    mockAuthentication(EMPLOYEE_ID, COMPANY_ID, EMAIL);
    when(cdhOutPort.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsCdhResponse);
    when(operaBookingOutPort.getStayDates(AUTHORIZATION, expectedUpcomingBooking.getBookingReference()))
          .thenReturn(List.of(bookingRoom1, expectedRoom));
    when(operaBookingOutPort.getHotelInformation(any()))
          .thenReturn(hotelInformationResponse);

    var result = inPort.getUpcomingBookings(upcomingBookingsRequest, AUTHORIZATION);

    verify(cdhOutPort).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    assertEquals(upcomingBookingsCdhResponse.getBookings(), result.getBookings());
    assertEquals(upcomingBookingsCdhResponse.getStays(), result.getStays());
    assertEquals(expectedUpcomingBooking.getHotelName(), result.getHotelName());
    assertEquals(expectedUpcomingBooking.getArrivalDate().toLocalDate(), result.getArrivalDate());
    assertEquals(LocalTime.of(11, 0), result.getArrivalTime());
    assertEquals(expectedUpcomingBooking.getDepartureDate().toLocalDate(), result.getDepartureDate());
    assertEquals(LocalTime.of(16, 0), result.getDepartureTime());
    assertEquals(expectedUpcomingBooking.getBookingReference(), result.getBookingReference());
    assertEquals(hotelInformationResponse.getBrand(), result.getBrand());
    assertEquals(hotelInformationResponse.getLinks(), result.getLinks());
    assertEquals(hotelInformationResponse.getGalleryImages(), result.getGalleryImages());
    assertEquals(hotelInformationResponse.getThumbnailImages(), result.getThumbnailImages());
    assertEquals(hotelInformationResponse.getTopSectionImages(), result.getTopSectionImages());
  }

  @Test
  void downloadBookingInvoices_withPiChannel_success() {
    // Arrange
    String authorization = "Bearer token";
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(uk.co.whitbread.booking.domain.model.invoice.Language.EN)
        .channel("PI")
        .subChannel("Online")
        .hotelBrand("PI")
        .build();

    Booking booking = new Booking();
    booking.setBookingReference("GAN9859956");
    BookingResponse bookingResponse = BookingResponse.builder()
        .bookings(List.of(booking))
        .build();

    InvoiceDownloadResponse expectedResponse = InvoiceDownloadResponse.builder()
        .invoices(List.of())
        .build();

    when(bookingOutPort.getBookings(any(), any(), any())).thenReturn(bookingResponse);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPiBbMobileCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileDigitalKey())).thenReturn(false);
    when(operaBookingOutPort.downloadBookingInvoices(request)).thenReturn(expectedResponse);

    // Act
    var result = inPort.downloadBookingInvoices(authorization, request);

    // Assert
    assertNotNull(result);
    assertEquals(expectedResponse, result);
    verify(operaBookingOutPort).downloadBookingInvoices(request);
  }

  @Test
  void downloadBookingInvoices_withBbChannel_success() {
    // Arrange
    String authorization = "Bearer token";
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(uk.co.whitbread.booking.domain.model.invoice.Language.EN)
        .channel("BB")
        .subChannel("Online")
        .hotelBrand("HUB")
        .build();

    Booking booking = new Booking();
    booking.setBookingReference("GAN9859956");
    BookingResponse bookingResponse = BookingResponse.builder()
        .bookings(List.of(booking))
        .build();

    InvoiceDownloadResponse expectedResponse = InvoiceDownloadResponse.builder()
        .invoices(List.of())
        .build();

    when(bookingOutPort.getBookings(any(), any(), any())).thenReturn(bookingResponse);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPiBbMobileCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileDigitalKey())).thenReturn(false);
    when(operaBookingOutPort.downloadBookingInvoices(request)).thenReturn(expectedResponse);

    // Act
    var result = inPort.downloadBookingInvoices(authorization, request);

    // Assert
    assertNotNull(result);
    assertEquals(expectedResponse, result);
    verify(operaBookingOutPort).downloadBookingInvoices(request);
  }

  @Test
  void downloadBookingInvoices_withUnsupportedChannel_throwsException() {
    // Arrange
    String authorization = "Bearer token";
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(uk.co.whitbread.booking.domain.model.invoice.Language.EN)
        .channel("INVALID")
        .subChannel("Online")
        .hotelBrand("ZIP")
        .build();

    // Act & Assert
    BookingValidationException exception = assertThrows(BookingValidationException.class,
        () -> inPort.downloadBookingInvoices(authorization, request));

    assertEquals(ErrorCode.BOOKING_INVOICE_UNSUPPORTED_CHANNEL.getCode(), exception.getErrorCode());
  }

  @Test
  void downloadBookingInvoices_whenNoBookingFound_throwsException() {
    // Arrange
    String authorization = "Bearer token";
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(uk.co.whitbread.booking.domain.model.invoice.Language.EN)
        .channel("PI")
        .subChannel("Online")
        .hotelBrand("PI")
        .build();

    BookingResponse emptyBookingResponse = BookingResponse.builder()
        .bookings(List.of())
        .build();

    when(bookingOutPort.getBookings(any(), any(), any())).thenReturn(emptyBookingResponse);

    // Act & Assert
    BookingValidationException exception = assertThrows(BookingValidationException.class,
        () -> inPort.downloadBookingInvoices(authorization, request));

    assertEquals(ErrorCode.BOOKING_INVOICE_NOT_FOUND.getCode(), exception.getErrorCode());
  }

  @Test
  void downloadBookingInvoices_withCaseInsensitiveChannel_success() {
    // Arrange
    String authorization = "Bearer token";
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(uk.co.whitbread.booking.domain.model.invoice.Language.EN)
        .channel("pi")  // lowercase
        .subChannel("Online")
        .hotelBrand("PI")
        .build();

    Booking booking = new Booking();
    booking.setBookingReference("GAN9859956");
    BookingResponse bookingResponse = BookingResponse.builder()
        .bookings(List.of(booking))
        .build();

    InvoiceDownloadResponse expectedResponse = InvoiceDownloadResponse.builder()
        .invoices(List.of())
        .build();

    when(bookingOutPort.getBookings(any(), any(), any())).thenReturn(bookingResponse);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPiBbMobileCheckInOnline())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileDigitalKey())).thenReturn(false);
    when(operaBookingOutPort.downloadBookingInvoices(request)).thenReturn(expectedResponse);

    // Act
    var result = inPort.downloadBookingInvoices(authorization, request);

    // Assert
    assertNotNull(result);
    assertEquals(expectedResponse, result);
  }

  private void mockAuthentication(String employeeId, String companyId, String email) {
    var authenticationMock = mock(Authentication.class);
    var customJwtAuthenticationTokenMock = mock(CustomJwtAuthenticationToken.class);
    var accountMock = mock(Account.class);
    when(accountMock.getEmail()).thenReturn(email);
    when(accountMock.getEmployeeId()).thenReturn(employeeId);
    when(accountMock.getCompanyId()).thenReturn(companyId);
    when(customJwtAuthenticationTokenMock.getAccount()).thenReturn(accountMock);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(customJwtAuthenticationTokenMock);
    SecurityContextHolder.getContext().setAuthentication(authenticationMock);
  }


  private BookingChannel bookingChannel() {
    return BookingChannel.builder()
        .channel(BOOKING_CHANNEL)
        .subchannel(BOOKING_SUBCHANNEL)
        .build();
  }

  private ResendConfirmationEmailRequest bookingConfirmation() {
    return ResendConfirmationEmailRequest.builder()
        .email(EMAIL)
        .hotelId(HOTEL_ID)
        .sessionId(SESSION_ID)
        .build();
  }

  private ResendInvoiceEmailRequest bookingInvoice() {
    return ResendInvoiceEmailRequest.builder()
        .email(EMAIL)
        .hotelId(HOTEL_ID)
        .build();
  }

  private BookingRequest bookingRequest() {

    return BookingRequest.builder()
        .typeOfBooking(BookingStatus.PAST).filterType(FilterTypes.NAME)
        .sortOrder(SortOrder.DEFAULT)
        .build();
  }

  private BookingResponse bookingResponse() {
    Booking booking = new Booking();
    booking.setSourceSystem(PmsSource.OPERA.toString());
    booking.setHotelCode(HOTEL_ID);
    return BookingResponse.builder()
        .bookings(List.of(booking))
        .totals(TypesTotals
            .builder()
            .build())
        .build();
  }
  private BookingResponse filteredBookingResponse() {
    Booking booking = new Booking();
    booking.setSourceSystem(PmsSource.OPERA.toString());
    booking.setHotelCode(HOTEL_ID);
    booking.setCheckInOnline(false);
    return BookingResponse.builder()
        .bookings(List.of(booking))
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

  private BookingInfoRequest bookingInfoRequest() {

    return BookingInfoRequest
        .builder()
        .bookingReference(BOOKING_REFERENCE)
        .arrival(ARRIVAL)
        .hotelId(HOTEL_ID)
        .surname(SURNAME)
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
}
