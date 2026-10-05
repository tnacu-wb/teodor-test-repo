package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockBasketResponseWithStatus;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockReservationByBasketRefResponseWithDepartureDate;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockReservationByBasketRefResponseWithDepartureDateAndPackageCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.properties.CheckInOnlineProperties;
import uk.co.whitbread.reservation.domain.properties.CheckOutOnlineProperties;

@ExtendWith(MockitoExtension.class)
class CheckOutOnlineLogicTest {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd");
  public static final String COMPLETED = "COMPLETED";
  public static final String PRE_CHECKED_IN = "PRE_CHECKED_IN";
  public static final String CIOL_FAILED = "CIOL_FAILED";
  public static final String OPEN = "OPEN";
  public static final String FAILED = "FAILED";
  public static final String AMENDED = "AMENDED";
  @Mock
  private CheckOutOnlineProperties checkOutOnlineProperties;
  @Mock
  private CheckInOnlineProperties checkInOnlineProperties;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private FeatureFlag featureFlag;

  @InjectMocks
  private CheckOutOnlineLogic checkOutOnlineLogic;

  private static Stream<Arguments> eligibleBookingCool() {
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    var ukHotel = new HotelInformationResponse("Europe/London", "GB");
    return Stream.of(
        Arguments.of(
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED),
            germanHotel, mockBasketResponseWithStatus(COMPLETED)),
        Arguments.of(
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), PRE_CHECKED_IN),
            ukHotel, mockBasketResponseWithStatus(PRE_CHECKED_IN)),
        Arguments.of(
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), CIOL_FAILED),
            ukHotel, mockBasketResponseWithStatus(CIOL_FAILED))
    );
  }

  private static Stream<Arguments> incorrectBasketStatuses() {
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    var ukHotel = new HotelInformationResponse("Europe/London", "GB");
    return Stream.of(
        Arguments.of(
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), OPEN),
            germanHotel, mockBasketResponseWithStatus(OPEN)),
        Arguments.of(
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), FAILED),
            ukHotel, mockBasketResponseWithStatus(FAILED)),
        Arguments.of(
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), AMENDED),
            ukHotel, mockBasketResponseWithStatus(AMENDED))
    );
  }

  private static Stream<Arguments> bookingWithWrongDepartureDates() {
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    var ukHotel = new HotelInformationResponse("Europe/London", "GB");

    return Stream.of(
        Arguments.of(
            //departure date tomorrow de
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now().plusDays(1),
                COMPLETED),
            germanHotel,
            mockBasketResponseWithStatus(COMPLETED)),
        Arguments.of(
            //departure date yesterday de
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now().minusDays(1),
                CIOL_FAILED),
            germanHotel,
            mockBasketResponseWithStatus(CIOL_FAILED)),
        Arguments.of(
            //departure date tomorrow uk
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now().plusDays(1),
                COMPLETED),
            ukHotel,
            mockBasketResponseWithStatus(COMPLETED)),
        Arguments.of(
            //departure date yesterday de
            mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now().minusDays(1),
                COMPLETED),
            ukHotel,
            mockBasketResponseWithStatus(PRE_CHECKED_IN))
    );
  }

  @ParameterizedTest
  @MethodSource("eligibleBookingCool")
  void isCoolAvailable_validCool(ReservationByBasketRefResponse booking,
      HotelInformationResponse hotelInfo, BasketResponse basketResponse) {
    //arrange
    mockCoolFF(true);
    when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    when(checkOutOnlineProperties.getStartingHour()).thenReturn(
        LocalTime.now(ZoneId.of(hotelInfo.getHotelTimeZone())).minusHours(1).getHour());
    when(checkOutOnlineProperties.getEndingHour()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, hotelInfo, basketResponse);

    //assert
    assertTrue(result);
  }

  @ParameterizedTest
  @MethodSource("incorrectBasketStatuses")
  void isCoolAvailable_incorrectBasketStatus(ReservationByBasketRefResponse booking,
      HotelInformationResponse hotelInfo, BasketResponse basketResponse) {
    //arrange
    mockCoolFF(true);

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, hotelInfo, basketResponse);

    //assert
    assertFalse(result);
  }

  @ParameterizedTest
  @MethodSource("bookingWithWrongDepartureDates")
  void isCoolAvailable_departureDate_startingHour_wrong(ReservationByBasketRefResponse booking,
      HotelInformationResponse hotelInfo, BasketResponse basket) {
    //arrange
    mockCoolFF(true);
    when(checkOutOnlineProperties.getEndingHour()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));
    var departureDate = booking.getReservationByIdList().get(0).getRoomStay().getDepartureDate();
    var isDepartureDateInTheFuture = LocalDate.parse(departureDate, DATE_TIME_FORMATTER)
        .isAfter(LocalDate.now());
    if (isDepartureDateInTheFuture) {
      when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    }

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, hotelInfo, basket);

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_booking_null() {
    //arrange
    mockCoolFF(true);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    //act
    var result = checkOutOnlineLogic.isCoolAvailable(null, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_hotelInfo_null() {
    //arrange
    mockCoolFF(true);
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED);

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, null,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_hotelInfo_countryCode_null() {
    //arrange
    mockCoolFF(true);
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", null);

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_booking_reservationList_null() {
    //arrange
    mockCoolFF(true);
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED);
    booking.setReservationByIdList(null);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_booking_roomstay_null() {
    //arrange
    mockCoolFF(true);
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED);
    booking.getReservationByIdList().get(0).setRoomStay(null);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_booking_basketStatus_null() {
    //arrange
    mockCoolFF(true);
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED);
    booking.setBasketStatus(null);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(null));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_Ff_Off() {
    //arrange
    mockCoolFF(false);
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(), COMPLETED);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_Lco_package() {
    //arrange
    mockCoolFF(true);

    var booking = mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(LocalDate.now(), COMPLETED,"HSCOU2");
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    when(checkOutOnlineProperties.getStartingHour()).thenReturn(
        LocalTime.now(ZoneId.of(germanHotel.getHotelTimeZone())).minusHours(1).getHour());
    when(checkOutOnlineProperties.getEndingHourLco()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertTrue(result);
  }

  @Test
  void isCoolAvailable_WrongReservationStatus_shouldReturnFalse() {
    //arrange
    mockCoolFF(true);

    var booking = mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(LocalDate.now(), COMPLETED,"HSCOU2");
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    when(checkOutOnlineProperties.getEndingHourLco()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("InHouse"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_RoomStayNull_shouldReturnFalse() {
    //arrange
    mockCoolFF(true);

    var booking = mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(LocalDate.now(), COMPLETED,"HSCOU2");
    booking.getReservationByIdList().get(0).setRoomStay(null);
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    when(checkOutOnlineProperties.getEndingHourLco()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void departureDate_unparsable_shouldReturnFalse() {
    //arrange
    mockCoolFF(true);

    var booking = mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(LocalDate.now(), COMPLETED,"MDP");
    booking.getReservationByIdList().get(0).getRoomStay().setDepartureDate("unparsable");
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void hotelTimeZone_unparsable_shouldReturnFalse() {
    //arrange
    mockCoolFF(true);
    var booking = mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(LocalDate.now(), COMPLETED,"MDP");
    var unparsableHotel = new HotelInformationResponse("Wrong", "DE");
    when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, unparsableHotel, mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @Test
  void currentTime_afterStartingHour_shouldReturnFalse() {
    //arrange
    mockCoolFF(true);

    var booking = mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(LocalDate.now(), COMPLETED,"HSCOU2");
    var germanHotel = new HotelInformationResponse("Europe/Berlin", "DE");
    when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    when(checkOutOnlineProperties.getStartingHour()).thenReturn(
        LocalTime.now(ZoneId.of(germanHotel.getHotelTimeZone())).plusHours(1).getHour());
    when(checkOutOnlineProperties.getEndingHourLco()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, germanHotel,
        mockBasketResponseWithStatus(COMPLETED));

    //assert
    assertFalse(result);
  }

  @ParameterizedTest
  @MethodSource("eligibleBookingCool")
  void isCoolAvailable_notPilotHotel_shouldReturnFalse(ReservationByBasketRefResponse booking,
      HotelInformationResponse hotelInfo, BasketResponse basketResponse) {
    //arrange
    mockCoolFF(true);
    when(checkOutOnlineProperties.getPilotHotels()).thenReturn(Set.of("FRAMTI,HEAPTI"));

    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, hotelInfo, basketResponse);

    //assert
    assertFalse(result);
  }

  @Test
  void isCoolAvailable_gbCountry_() {
    //arrange
    mockCoolFF(true);
    var ukHotel = new HotelInformationResponse("Europe/London", "GB");
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(),
        COMPLETED);
    var basket = mockBasketResponseWithStatus(COMPLETED);
    when(checkOutOnlineProperties.getPilotHotels()).thenReturn(Set.of("Exclude"));
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    when(checkOutOnlineProperties.getStartingHour()).thenReturn(
        LocalTime.now(ZoneId.of(ukHotel.getHotelTimeZone())).minusHours(1).getHour());
    when(checkOutOnlineProperties.getEndingHour()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));


    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, ukHotel, basket);

    //assert
    assertTrue(result);
  }

  @Test
  void isCoolAvailable_deHotel() {
    //arrange
    mockCoolFF(true);
    var ukHotel = new HotelInformationResponse("Europe/London", "DE");
    var booking = mockReservationByBasketRefResponseWithDepartureDate(LocalDate.now(),
        COMPLETED);
    var basket = mockBasketResponseWithStatus(COMPLETED);
    when(checkOutOnlineProperties.getPilotHotels()).thenReturn(Set.of("TESTHOTEL"));
    when(checkOutOnlineProperties.getDaysUntilDeparture()).thenReturn(0);
    when(checkOutOnlineProperties.getStartingHour()).thenReturn(
        LocalTime.now(ZoneId.of(ukHotel.getHotelTimeZone())).minusHours(1).getHour());
    when(checkOutOnlineProperties.getEndingHour()).thenReturn(23);
    when(checkOutOnlineProperties.getCoolReservationStatus()).thenReturn(Set.of("Reserved"));


    //act
    var result = checkOutOnlineLogic.isCoolAvailable(booking, ukHotel, basket);

    //assert
    assertTrue(result);
  }

  private void mockCoolFF(boolean toggle) {
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getReleasePiBbMobileCheckOut()))
        .thenReturn(toggle);
  }
}
