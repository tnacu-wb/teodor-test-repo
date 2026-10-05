package uk.co.whitbread.shared.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.UPCOMING_BOOKINGS_ENDPOINT;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountBookingsQueryParams;
import uk.co.whitbread.shared.cdh.model.ReservationSearchResponse;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBookingsResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;
import uk.co.whitbread.shared.cdh.properties.UriPaths;

@ExtendWith(MockitoExtension.class)
public class BookingDataServiceTest {

  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String ACCESS_CONTEXT = "InnBUsiness";
  private static final String ARRIVAL_DATE_FROM_QUERY_PARAM = "ArrivalDateFrom";
  private static final String ARRIVAL_DATE_TO_QUERY_PARAM = "ArrivalDateTo";
  private static final String BOOKING_REFERENCE_QUERY_PARAM = "BookingReference";
  private static final String ARRIVAL_DATE_FROM = "2022-08-23";
  private static final String ARRIVAL_DATE_TO = "2022-08-24";
  private static final String BOOKING_REFERENCE = "AUVR2267792";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";
  private static final String BOOKING_SUBSCRIPTION_KEY = "booking-subscription-key";

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Mock(lenient = true)
  private CdhApiProperties cdhApiProperties;

  @Mock(lenient = true)
  private CdhApiOauthProperties cdhApiOauthProperties;

  @Mock
  private CustomerDataHubClient cdhClient;

  @InjectMocks
  private BookingDataService bookingDataService;

  @BeforeEach
  void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
    when(cdhApiOauthProperties.getBookingSubscriptionKey()).thenReturn(BOOKING_SUBSCRIPTION_KEY);
  }

  @Test
  public void getCustomerAccountBookings_success() throws IOException {
    when(cdhClient.getCDH(anyString(),
        eq(buildHeaders(BOOKING_SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(ReservationSearchResponse.class))).thenReturn(
        Optional.ofNullable(buildResponseFromJson(ReservationSearchResponse.class,
              "src/test/resources/samples/get_bookings_response.json")));

    var bookingsOptional = bookingDataService.getCustomerAccountBookings(
        GetCustomerAccountBookingsQueryParams.builder().build(), ACCESSED_BY);

    assertFalse(bookingsOptional.isEmpty());
    var bookings = bookingsOptional.get();
    assertEquals(3, bookings.getTotalResults().intValue());
  }

  @Test
  public void getCustomerAccountBookings_urlIsBuiltCorrectly() {
    GetCustomerAccountBookingsQueryParams queryParams = GetCustomerAccountBookingsQueryParams.builder()
        .arrivalDateFrom(ARRIVAL_DATE_FROM)
        .arrivalDateTo(ARRIVAL_DATE_TO)
        .bookingReference(BOOKING_REFERENCE)
        .build();

    bookingDataService.getCustomerAccountBookings(queryParams, ACCESSED_BY);

    var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).getCDH(urlCaptor.capture(),
        eq(buildHeaders(BOOKING_SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(ReservationSearchResponse.class));
    var url = urlCaptor.getValue();

    var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.BOOKING_SERVICES_ENDPOINT + UriPaths.RESERVATION_SEARCH + "?"
        + BOOKING_REFERENCE_QUERY_PARAM + "=" + BOOKING_REFERENCE + "&"
        + ARRIVAL_DATE_FROM_QUERY_PARAM + "=" + ARRIVAL_DATE_FROM + "&"
        + ARRIVAL_DATE_TO_QUERY_PARAM + "=" + ARRIVAL_DATE_TO;

    assertEquals(expectedUrl, url);
  }

  @Test
  void getUpcomingBookings_WhenCalled_ThenCorrectUrlIsBuiltAndResponseIsParsedCorrectly() throws IOException {
    var expectedHeaders = buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY, ACCESS_CONTEXT);
    var expectedResponse = buildResponseFromJson(UpcomingBookingsResponse.class,
          "src/test/resources/samples/get_upcoming_bookings_response.json");
    when(cdhClient.getCDH(anyString(), eq(expectedHeaders), eq(UpcomingBookingsResponse.class)))
          .thenReturn(Optional.ofNullable(expectedResponse));

    var response = bookingDataService.getUpcomingBookings("companyId", "employeeId",
          ACCESSED_BY, ACCESS_CONTEXT);

    assertFalse(response.isEmpty());
    var upcomingBookingsResponse = response.get();
    assertEquals(3, upcomingBookingsResponse.getStays());
    assertEquals(9, upcomingBookingsResponse.getBookings());
    var upcomingBooking = upcomingBookingsResponse.getUpcomingBooking().get(0);
    assertEquals("BAN4102457", upcomingBooking.getBookingReference());
    assertEquals("HEAFIV", upcomingBooking.getHotelCode());
    assertEquals("Heathrow Airport Terminal 5", upcomingBooking.getHotelName());
    assertEquals(OffsetDateTime.of(2025, 3, 22, 0, 0, 0, 0, ZoneOffset.UTC),
          upcomingBooking.getArrivalDate());
    assertEquals(OffsetDateTime.of(2025, 3, 23, 0, 0, 0, 0, ZoneOffset.UTC),
          upcomingBooking.getDepartureDate());
    var urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).getCDH(urlArgumentCaptor.capture(), eq(expectedHeaders), eq(UpcomingBookingsResponse.class));
    var expectedUrl = cdhApiProperties.getHost() + UPCOMING_BOOKINGS_ENDPOINT
          .replace("{employeeAccountId}", "employeeId")
          .replace("{companyAccountId}", "companyId");
    assertEquals(expectedUrl, urlArgumentCaptor.getValue());
  }

  private <T> T buildResponseFromJson(Class<T> responseClass, String fileName) throws IOException {
    return objectMapper.readValue(new File(fileName), responseClass);
  }
}
