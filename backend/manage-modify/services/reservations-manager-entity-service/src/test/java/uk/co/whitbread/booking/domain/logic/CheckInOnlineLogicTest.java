package uk.co.whitbread.booking.domain.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.logic.utils.AEMLabelKeyConstants;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;
import uk.co.whitbread.booking.domain.model.history.out.Booker;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.history.out.BookingStatus;
import uk.co.whitbread.booking.domain.model.history.out.CiolEligibleBookingsAndHotels;
import uk.co.whitbread.booking.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.PaymentInfoOutPort;
import uk.co.whitbread.booking.domain.properties.CheckInOnlineProperties;

@ExtendWith(MockitoExtension.class)
class CheckInOnlineLogicTest {

  private static final String MOBILE = "MOBILE";
  @Mock
  private CheckInOnlineProperties checkInOnlineProperties;

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private HotelInfoOutPort hotelInfoOutPort;

  @Mock
  private PaymentInfoOutPort paymentInfoOutPort;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private FeatureFlag mockedFeatureFlag;

  @Mock
  private BookingChannel channel;
  @InjectMocks
  private CheckInOnlineLogic checkInOnlineLogic;

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void getEligibleBookingsForCiol_checkCiolRules(boolean deRegCardFf) {
    //Arrange
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(2);
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getCiolHotels()).thenReturn(Set.of("FRAMTI"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("WRONGRATE"));
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));
    when(checkInOnlineProperties.getCiolEmails()).thenReturn(
        Set.of("test@test.com", "test1@test.com", "test2@test.com"));
    if (deRegCardFf) {
      when(checkInOnlineProperties.getDeRegCardRooms()).thenReturn(1);
    }
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(deRegCardFf);
    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(mockUpcomingBookingResponse());

    //Assert
    if (deRegCardFf) {
      assertNotNull(response);
      assertEquals(5, response.reservationIds().size());
      assertTrue(response.reservationIds().contains("booking1"));
      assertTrue(response.reservationIds().contains("booking2"));
      assertTrue(response.reservationIds().contains("booking3"));
      assertTrue(response.reservationIds().contains("booking4"));
    } else {
      assertNotNull(response);
      assertEquals(3, response.reservationIds().size());
      assertTrue(response.reservationIds().contains("booking3"));
      assertTrue(response.reservationIds().contains("booking4"));
    }
    assertTrue(response.reservationIds().contains("bookingWithPiba"));
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void getEligibleBookingsForCiol_noCountryRateHotelIdRestrictions(boolean deRegCardFf) {
    //Arrange
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getCiolHotels()).thenReturn(Set.of());
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of());
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));
    if (deRegCardFf) {
      when(checkInOnlineProperties.getDeRegCardRooms()).thenReturn(1);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      channel = BookingChannel.builder().subchannel(MOBILE).build();
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPiba()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPibaCnp()))
          .thenReturn(false);
    }
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(deRegCardFf);

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(mockUpcomingBookingResponse());

    //Assert
    if (deRegCardFf) {
      assertNotNull(response);
      assertEquals(6, response.reservationIds().size());
      assertTrue(response.reservationIds().contains("booking1"));
      assertTrue(response.reservationIds().contains("booking2"));
      assertTrue(response.reservationIds().contains("booking3"));
      assertTrue(response.reservationIds().contains("WRONGRATE"));
      assertTrue(response.reservationIds().contains("TESTHOTEL"));
    } else {
      assertNotNull(response);
      assertEquals(3, response.reservationIds().size());
      assertTrue(response.reservationIds().contains("booking3"));
      assertTrue(response.reservationIds().contains("TESTHOTEL"));
    }
    assertTrue(response.reservationIds().contains("bookingWithPiba"));
  }

  @Test
  void setCiolDataEligibleBookings() {
    //Arrange
    var bookings = mockUpcomingBookingResponse();
    when(basketOutPort.getBasketStatusesForBookingRefs(any()))
        .thenReturn(Map.of("booking1", BasketStatus.COMPLETED,
            "booking2", BasketStatus.PRE_CHECKED_IN,
            "booking3", BasketStatus.COMPLETED,
            "booking4", BasketStatus.CIOL_FAILED,
            "booking5", BasketStatus.CIOL_RC_FAILED));

    //Act
    checkInOnlineLogic.setCiolDataEligibleBookings(bookings,
        new CiolEligibleBookingsAndHotels(Set.of("booking1", "booking2",
            "booking3", "booking4"), Map.of()));

    //Assert
    var response = bookings.getBookings().stream()
        .filter(Booking::isCheckInOnlineAvailable)
        .collect(
            Collectors.toSet());
    assertNotNull(response);
    assertEquals(3, response.size());

  }

  @Test
  void getEligibleBookingsDeRegCard_notPilotHotel() {
    //Assert
    var germanHotel = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE",
        "FRAMTI",
        "booking1");
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setBookings(List.of(germanHotel));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(true);
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getDeRegCardHotels()).thenReturn(Set.of("TestHotel"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of());

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(bookingResponse);

    //Assert
    assertNotNull(response);
    assertTrue(response.reservationIds().isEmpty());
  }

  @Test
  void getEligibleBookingsDeRegCard_withArrivalDateComplaint() {
    //Assert
    var germanHotel = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now().plusDays(5), "FLEXRATE",
        "FRAMTI",
        "booking1");
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setBookings(List.of(germanHotel));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(bookingResponse);

    //Assert
    assertNotNull(response);
    assertTrue(response.reservationIds().isEmpty());
    assertEquals(AEMLabelKeyConstants.DIGITAL_CIOL_ARRIVAL_DATE_COMPLAINT,germanHotel.getCiolErrorLabelKey());
  }

  @Test
  void getEligibleBookingsDeRegCard_withAInvalidRates() {
    //Assert
    var germanHotel = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "INVALIDRATE",
        "FRAMTI",
        "booking1");
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setBookings(List.of(germanHotel));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("INVALIDRATE"));

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(bookingResponse);

    //Assert
    assertNotNull(response);
    assertTrue(response.reservationIds().isEmpty());
    assertEquals(
        String.format(AEMLabelKeyConstants.DIGITAL_CIOL_ROOM_STAY_RATES_INVALID, germanHotel.getRateName()),
        germanHotel.getCiolErrorLabelKey()
    );

  }

  @Test
  void getEligibleBookingsDeRegCard_pilotHotel_FfOff() {
    //Assert
    var germanHotel = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE",
        "TestHotel",
        "booking1");
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setBookings(List.of(germanHotel));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(
        Map.of("TestHotel", "DE", "LONEUS", "GB"));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(false);
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of());

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(bookingResponse);

    //Assert
    assertNotNull(response);
    assertTrue(response.reservationIds().isEmpty());
  }

  @Test
  void getEligibleBookingsUkHotel_DeRegCardOff() {
    //Assert
    var germanHotel = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE",
        "TestHotel",
        "booking1");
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setBookings(List.of(germanHotel));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(
        Map.of("FRAMTI", "DE", "TestHotel", "GB"));
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of());
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
    when(checkInOnlineProperties.getCiolHotels()).thenReturn(Set.of("TestHotel"));
    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(bookingResponse);

    //Assert
    assertNotNull(response);
    assertTrue(response.reservationIds().contains("booking1"));
  }


  @ParameterizedTest
  @CsvSource({"true, true", "false, true", "true, false", "false, false"})
  void getEligibleBookingsForCiol_checkPibaCardType(boolean isMobileCiolPiba, boolean isMobileCiolPibaCnp) {
    //Arrange
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(2);
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getCiolHotels()).thenReturn(Set.of("FRAMTI"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("WRONGRATE"));
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));
    when(checkInOnlineProperties.getCiolEmails()).thenReturn(
        Set.of("test@test.com", "test1@test.com", "test2@test.com"));

    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPiba()))
        .thenReturn(isMobileCiolPiba);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPibaCnp()))
        .thenReturn(isMobileCiolPibaCnp);
    channel = BookingChannel.builder().subchannel(MOBILE).build();
    if (isMobileCiolPiba && isMobileCiolPibaCnp) {
      when(checkInOnlineProperties.getDeRegCardRooms()).thenReturn(1);
    }

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(mockUpcomingBookingResponse());

    //Assert
    assertNotNull(response);
    if (isMobileCiolPiba && isMobileCiolPibaCnp) {
      assertEquals(5, response.reservationIds().size());
      assertTrue(response.reservationIds().contains("booking1"));
      assertTrue(response.reservationIds().contains("booking2"));
    } else {
      assertEquals(3, response.reservationIds().size());
    }
    assertTrue(response.reservationIds().contains("bookingWithPiba"));
    assertTrue(response.reservationIds().contains("booking3"));
    assertTrue(response.reservationIds().contains("booking4"));
  }

  @ParameterizedTest
  @NullSource
  @CsvSource({"web", "MOBILE"})
  void getEligibleBookingsForCiol_subchannel(String subchannel) {
    //Arrange
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(2);
    when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(2);
    when(checkInOnlineProperties.getCiolHotels()).thenReturn(Set.of("FRAMTI"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("TEST", "TM"));
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(hotelInfoOutPort.getHotelInfo(any())).thenReturn(Map.of("FRAMTI", "DE", "LONEUS", "GB"));
    when(checkInOnlineProperties.getCiolEmails()).thenReturn(
        Set.of("test@test.com", "test1@test.com", "test2@test.com"));

    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPiba()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPibaCnp()))
        .thenReturn(true);
    channel = BookingChannel.builder().subchannel(subchannel).build();

    //Act
    var response = checkInOnlineLogic.getEligibleBookingsForCiol(mockUpcomingBookingResponse());

    //Assert
    assertNotNull(response);
    assertEquals(3, response.reservationIds().size());
    if (!MOBILE.equals(subchannel)) {
      assertTrue(response.reservationIds().contains("bookingWithPiba"));
    }
    assertTrue(response.reservationIds().contains("booking3"));
    assertTrue(response.reservationIds().contains("booking4"));
  }

  private BookingResponse mockUpcomingBookingResponse() {

    var bookingWithNullBooker = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(),
        "FLEXRATE", "FRAMTI",
        "nullBooker");
    bookingWithNullBooker.setBooker(null);
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setBookings(List.of(
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE", "FRAMTI",
            "booking1"),
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now().plusDays(1), "ADVANCE",
            "FRAMTI", "booking2"),
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now().plusDays(2), "EMPLOYEE",
            "LONEUS", "booking3"),
        mockUpcomingBooking(BookingStatus.FUTURE, 2, LocalDate.now().plusDays(2), "FLEXRATE",
            "LONEUS", "booking4"),
        //not eligible null booking
        mockUpcomingBooking(null, 1, LocalDate.now().plusDays(2), "EMPLOYEE",
            "LONEUS", "pastBooking"),
        //not eligible past booking
        mockUpcomingBooking(BookingStatus.PAST, 1, LocalDate.now().minusDays(5), "EMPLOYEE",
            "LONEUS", "pastBooking"),
        //not eligble cancelledBooking
        mockUpcomingBooking(BookingStatus.CANCELLED, 1, LocalDate.now().plusDays(2), "EMPLOYEE",
            "LONEUS", "cancelledBooking"),
        //not eligible room number not compliant
        mockUpcomingBooking(BookingStatus.FUTURE, 2, LocalDate.now(), "FLEXRATE", "FRAMTI",
            "roomNotCompliant"),
        //not eligible date not within 2 days
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now().plusDays(3), "FLEXRATE",
            "FRAMTI",
            "arrivalDateNotCompliant"),
        //not eligible rates not compliant
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "WRONGRATE", "FRAMTI",
            "WRONGRATE"),
        //not eligible rates not compliant
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE", "TESTHOTEL",
            "TESTHOTEL"),
        //not eligible TM rates not compliant
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "TM123", "TESTHOTEL",
            "TESTHOTEL"),
        mockUpcomingBooking(BookingStatus.FUTURE, 2, LocalDate.now().plusDays(1), "FLEXRATE",
            "FRAMTI", "deRegRoomsNotCompliant"),
        mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now().plusDays(1), "FLEXRATE",
            "LONEUS", "bookingWithPiba"),
        bookingWithNullBooker
    ));
    return bookingResponse;
  }

  @Test
  void setCiolDataEligibleBookings_NullCiolEligibleBookingsAndHotels() {
    //Arrange
    var bookings = mockUpcomingBookingResponse();
    when(basketOutPort.getBasketStatusesForBookingRefs(any()))
        .thenReturn(Map.of("booking1", BasketStatus.COMPLETED,
            "booking2", BasketStatus.PRE_CHECKED_IN,
            "booking3", BasketStatus.COMPLETED,
            "booking4", BasketStatus.CIOL_FAILED));

    //Act
    checkInOnlineLogic.setCiolDataEligibleBookings(bookings, null);

    //Assert
    var response = bookings.getBookings().stream()
        .filter(Booking::isCheckInOnlineAvailable)
        .collect(
            Collectors.toSet());
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidCiolEligibleBookingsAndHotels")
  void setCiolDataEligibleBookings_invalidCiolEligibleBookingsAndHotels(CiolEligibleBookingsAndHotels ciolBookings) {
    //Arrange
    var bookings = mockUpcomingBookingResponse();
    when(basketOutPort.getBasketStatusesForBookingRefs(any()))
        .thenReturn(Map.of("booking1", BasketStatus.COMPLETED,
            "booking2", BasketStatus.PRE_CHECKED_IN,
            "booking3", BasketStatus.COMPLETED,
            "booking4", BasketStatus.CIOL_FAILED));

    //Act
    checkInOnlineLogic.setCiolDataEligibleBookings(bookings, null);

    //Assert
    var response = bookings.getBookings().stream()
        .filter(Booking::isCheckInOnlineAvailable)
        .collect(
            Collectors.toSet());
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  private Booking mockUpcomingBooking(BookingStatus bookingStatus,
      int noOfRooms,
      LocalDate arrivalDate,
      String rateName,
      String hotelCode,
      String bookingRef) {

    var booking = new Booking();
    booking.setBookingStatus(bookingStatus);
    booking.setNoOfRooms(noOfRooms);
    booking.setArrivalDate(arrivalDate);
    booking.setRateName(rateName);
    booking.setHotelCode(hotelCode);
    booking.setBookingReference(bookingRef);

    Booker booker = new Booker();
    booker.setFirstName("testFirstName");
    booker.setLastName("testLastName");
    booker.setEmailAddress("test@test.com");
    booking.setBooker(booker);

    return booking;
  }

  private static Stream<Arguments> invalidCiolEligibleBookingsAndHotels() {
    return Stream.of(
        Arguments.of(new CiolEligibleBookingsAndHotels(null, new HashMap<>())),
        Arguments.of(new CiolEligibleBookingsAndHotels(new HashSet<>(), null))
    );

  }

  @ParameterizedTest
  @CsvSource({
      "MOBILE,true,true,true",
      "MOBILE,true,true,false",
      "MOBILE,true,false,true",
      "MOBILE,true,false,false",
      "WEB,false,false,true",
      "WEB,false,false,false",
      "WEB,false,true,true",
      "WEB,false,true,false",
      ",false,true,true",
      ",false,true,false",
      "nullChannel,false,false,true",})
  void filterByPibaCard_shouldSetCheckInOnlineUnavailableAndErrorLabelForPibaBookings(
      String subchannel, boolean isMobile, boolean isFF, boolean isFFCnp) {
    // Arrange
    var booking1 = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE",
        "HOTEL1", "ref1");
    booking1.setCheckInOnlineAvailable(true);
    var booking2 = mockUpcomingBooking(BookingStatus.FUTURE, 1, LocalDate.now(), "FLEXRATE",
        "HOTEL2", "ref2");
    booking2.setCheckInOnlineAvailable(true);
    var checkingOnlineBookings = List.of(booking1, booking2);
    var bookingList = List.of(booking1, booking2);
    var bookingChannel = BookingChannel.builder().subchannel(subchannel).build();
    if("nullChannel".equals(subchannel)) {
      bookingChannel = null;
    }
    if (isMobile) {
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPiba())).thenReturn(isFF);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPibaCnp())).thenReturn(isFFCnp);
      if (isFF && isFFCnp) {
        when(paymentInfoOutPort.getPibaCpReservations(checkingOnlineBookings, isFF, isFFCnp)).thenReturn(
            List.of("ref2"));
      }
    }

    // Act
    var result = checkInOnlineLogic.filterByPibaCard(checkingOnlineBookings, bookingList,
        bookingChannel);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.get(0).isCheckInOnlineAvailable());
    if (isMobile && isFF && isFFCnp) {
      assertFalse(result.get(1).isCheckInOnlineAvailable());
      assertEquals(AEMLabelKeyConstants.DIGITAL_CIOL_PIBA_CARD_NOT_ALLOWED,
          result.get(1).getCiolErrorLabelKey());
    } else {
      assertTrue(result.get(1).isCheckInOnlineAvailable());
      assertNull(result.get(1).getCiolErrorLabelKey());
    }
  }
}

