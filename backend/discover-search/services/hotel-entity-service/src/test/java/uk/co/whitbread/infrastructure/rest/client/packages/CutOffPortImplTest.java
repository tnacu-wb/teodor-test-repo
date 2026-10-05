package uk.co.whitbread.infrastructure.rest.client.packages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
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
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.packages.in.CutOffMinute;
import uk.co.whitbread.domain.model.packages.in.ExtrasCutoff;
import uk.co.whitbread.domain.model.packages.in.HotelInformationExtended;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.CutOffExtras;
import uk.co.whitbread.domain.ports.secondary.AvailableCleanRoomOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.infrastructure.config.PackagesProperties;


@ExtendWith(MockitoExtension.class)
class CutOffPortImplTest {

  private static final String HOTEL_ID = "TKINPT";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private static final String HSCKIN = "HSCKIN";
  private static final String HSCOU2 = "HSCOU2";
  @InjectMocks
  private CutOffPortImpl cutOffPort;

  @Mock
  private HotelInfoOutPort hotelInfoOutPort;

  @Mock
  private AvailableCleanRoomOutPort cleanRoomPort;

  @Mock
  private PackagesProperties packagesProperties;

  private static LocalDate currentDate;
  private static LocalDateTime curentTime;

  @BeforeAll
  static void init() {
    curentTime = LocalDateTime.now();
    currentDate = curentTime.toLocalDate();
  }

  @ParameterizedTest
  @CsvSource({", true", "2, false"})
  void isCutOffByHotel_DefaultCutOff_withValidTimeZone_shouldCalculateCorrectly(Integer cutOff,
      Boolean res) {
    var hotelInfo = mockHotelInfo("America/New_York", "1/1/70, 3:00 PM");
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);
    when(packagesProperties.getCutOffTime()).thenReturn(cutOff);

    var futureDate = currentDate.plusDays(2);
    var request = mockPackagesRequest(futureDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(null, null, null, null);

    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    assertThat(result, notNullValue());
    assertEquals(result.getIsEciCutOffCiol(), res);
    assertEquals(result.getIsEciCutOffBooking(), res);
    assertEquals(result.getIsLcoCutOffCiol(), res);
    assertEquals(result.getIsLcoCutOffBooking(), res);
  }

  @ParameterizedTest(name = "timezone=''{0}'' should fallback to default")
  @MethodSource("provideInvalidOrMissingTimezones")
  void isCutOffByHotel_DefaultCuttOff_withInvalidOrMissingTimeZone_shouldUseSystemDefault(
      String timeZone) {
    var hotelInfo = mockHotelInfo(timeZone, "1/1/70, 3:00 PM");
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);
    when(packagesProperties.getCutOffTime()).thenReturn(2);

    var futureDate = currentDate.plusDays(2);
    var request = mockPackagesRequest(futureDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(null, null, null, null);

    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    assertThat(result, notNullValue());
    assertFalse(result.getIsEciCutOffCiol());
    assertFalse(result.getIsEciCutOffBooking());
    assertFalse(result.getIsLcoCutOffCiol());
    assertFalse(result.getIsLcoCutOffBooking());
  }

  private static Stream<Arguments> provideInvalidOrMissingTimezones() {
    return Stream.of(
        Arguments.of("Invalid/TimeZone"),
        Arguments.of(""),
        Arguments.of((String) null)
    );
  }

  @Test
  void isOutsideCutOffTime_whenManageBookingPageTrue_shouldAlwaysReturnTrue() {
    var cutOff = CutOffExtras.builder()
        .isEciCutOffCiol(false)
        .isEciCutOffBooking(false)
        .isLcoCutOffCiol(false)
        .isLcoCutOffBooking(false)
        .build();

    var request = mockPackagesRequest(currentDate, false, true);

    assertTrue(cutOffPort.isOutsideCutOffTime(HSCKIN, request, cutOff));
  }


  @Test
  void isOutsideCutOffTime_withUnknownCode_shouldReturnTrue() {
    var cutOff = CutOffExtras.builder()
        .isEciCutOffCiol(false)
        .isEciCutOffBooking(false)
        .isLcoCutOffCiol(false)
        .isLcoCutOffBooking(false)
        .build();

    var request = mockPackagesRequest(currentDate, false, false);

    assertTrue(cutOffPort.isOutsideCutOffTime("UNKNOWN", request, cutOff));
  }

  @ParameterizedTest
  @CsvSource({
      "HSCKIN, true, true, true, true, true, true",
      "HSCKIN, true, false, true, true, true, false",
      "HSCOU2, true, true, true, true, true, true",
      "HSCOU2, true, true, false, true, true, false",
      "HSCKIN, false, true, true, true, true, true",
      "HSCKIN, false, true, true, false, true, false",
      "HSCOU2, false, true, true, true, true, true",
      "HSCOU2, false, true, true, true, false, false"
  })
  void isOutsideCutOffTime_withVariousFlagCombinations_shouldReturnCorrectly(
      String code,
      Boolean isCiol,
      Boolean eciCuttOffCiol,
      Boolean lcoCutOffCiol,
      Boolean eciCutOffBooking,
      Boolean lcoCutOffBooking,
      Boolean expected) {

    var cutOff = CutOffExtras.builder()
        .isEciCutOffCiol(eciCuttOffCiol)
        .isEciCutOffBooking(eciCutOffBooking)
        .isLcoCutOffCiol(lcoCutOffCiol)
        .isLcoCutOffBooking(lcoCutOffBooking)
        .build();

    var request = mockPackagesRequest(currentDate, isCiol, false);

    var result = cutOffPort.isOutsideCutOffTime(code, request, cutOff);
    assertEquals(expected, result);
  }


  @Test
  void isOutsideCutOffTime_whenManageBookingPageNull_shouldCheckCutOffBookingFlags() {
    var cutOff = CutOffExtras.builder()
        .isEciCutOffCiol(false)
        .isEciCutOffBooking(true)
        .isLcoCutOffCiol(false)
        .isLcoCutOffBooking(true)
        .build();

    var request = mockPackagesRequest(currentDate, false, null);
    assertTrue(cutOffPort.isOutsideCutOffTime(HSCKIN, request, cutOff));
    assertTrue(cutOffPort.isOutsideCutOffTime(HSCOU2, request, cutOff));
  }

  @ParameterizedTest
  @CsvSource({"1, 1, false", "1, 2, true", "2, 2, false", "2, 3, true"})
  void isCutOffByHotel_DefaultCuttOff_withFutureArrivalDate_shouldIndicateCutOffAvailable(
      Integer config, Integer days, Boolean expected) {
    var hotelInfo = mockHotelInfo("Europe/London", "1/1/70, 3:00 PM");
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);
    when(packagesProperties.getCutOffTime()).thenReturn(config);

    var futureDate = currentDate.plusDays(days);
    var request = mockPackagesRequest(futureDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(null, null, null, null);

    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    assertThat(result, notNullValue());
    assertEquals(expected, result.getIsEciCutOffCiol());
    assertEquals(expected, result.getIsEciCutOffBooking());
    assertEquals(expected, result.getIsLcoCutOffCiol());
    assertEquals(expected, result.getIsLcoCutOffBooking());
  }


  @Test
  void isCutOffByHotel_DefaultCuttOff_withNull_shouldUseDefaultCutOff() {
    var hotelInfo = mockHotelInfo("Europe/London", "1/1/70, 3:00 PM");
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);
    when(packagesProperties.getCutOffTime()).thenReturn(null);
    var request = mockPackagesRequest(currentDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(null, null, null, null);

    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    assertThat(result, notNullValue());
  }

  @ParameterizedTest(name = "cutOff={0}, checkIn=''{1}'', isCiol={2}")
  @MethodSource("provideDefaultCutOffCheckInScenarios")
  void isCutOffByHotel_DefaultCuttOff_withVariousCheckInScenarios_shouldCalculateCorrectly(
      Integer cutOffTime, String checkInTime, Boolean isCiol) {
    var hotelInfo = mockHotelInfo("Europe/London", checkInTime);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);
    when(packagesProperties.getCutOffTime()).thenReturn(cutOffTime);

    var request = mockPackagesRequest(currentDate, isCiol, false);
    var cutOffMinutes = mockCutOffMinutes(null, null, null, null);

    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    assertThat(result, notNullValue());
    assertNull(result.getCiolCurrentDateAvailableRooms());
  }

  private static Stream<Arguments> provideDefaultCutOffCheckInScenarios() {
    return Stream.of(
        Arguments.of(0, "1/1/70, 3:00 PM", false),
        Arguments.of(24, "1/1/70, 6:00 AM", true),
        Arguments.of(24, "1/1/70, 11:00 PM", true)
    );
  }

  @Test
  void isOutsideCutOffTime_withCiolNullValue_shouldTreatAsBookingFlow() {
    var cutOff = CutOffExtras.builder()
        .isEciCutOffCiol(false)
        .isEciCutOffBooking(true)
        .isLcoCutOffCiol(false)
        .isLcoCutOffBooking(false)
        .build();

    var request = mockPackagesRequest(currentDate, null, false);

    assertTrue(cutOffPort.isOutsideCutOffTime("HSCKIN", request, cutOff));
  }


  private HotelInfo mockHotelInfo(String timeZone, String checkInTime) {
    return HotelInfo.builder()
        .threeLetterId("TKI")
        .hotelCountryCode("GB")
        .hotelTimeZone(timeZone)
        .currencyCode("GBP")
        .languageCode("en")
        .checkInTime(checkInTime)
        .checkOutTime("1/1/70, 11:00 AM")
        .build();
  }

  private PackagesRequest mockPackagesRequest(LocalDate arrivalDate, Boolean isCiol,
      Boolean isManageBooking) {
    String startDate = arrivalDate.toString();
    String endDate = arrivalDate.plusDays(1).toString();
    return PackagesRequest.builder()
        .hotelId(HOTEL_ID)
        .startDate(startDate)
        .endDate(endDate)
        .adultsNumber(2)
        .childrenNumber(0)
        .nightsNumber(3)
        .country(COUNTRY)
        .language(LANGUAGE)
        .isCiol(isCiol)
        .isManageBookingPage(isManageBooking)
        .build();
  }

  private CutOffMinute mockCutOffMinutes(Integer eciCiol, Integer eciBooking,
      Integer lcoCiol, Integer lcoBooking) {
    return new CutOffMinute(eciCiol, eciBooking, lcoCiol, lcoBooking);
  }

  @ParameterizedTest(name = "ECI_CIOL={0}, ECI_BOOKING={1}, LCO_CIOL={2}, LCO_BOOKING={3}, "
      + "daysUntilArrival={4}, shouldPass={5}")
  @CsvSource({
      "null, null, null, null, 2",
      "1440, 1440, 1440, 1440, 2",
      "1440, 1440, 1440, 1440, 1",
      "0, 0, 0, 0, 2",
      "1440, null, null, null, 1",
      "null, 1440, null, null, 1",
      "null, null, 1440, null, 1",
      "null, null, null, 1440, 1",
      "2880, 1440, 720, 360, 4",
      "1, 1, 1, 1, 2",
      "1, 1, 1, 1, 1"
  })
  void isCutOffByHotel_withVariousCutOffMinuteCombinations_shouldReturnTrue(
      String eciCiolStr, String eciBookingStr, String lcoCiolStr, String lcoBookingStr,
      Integer daysUntilArrival) {

    // Arrange
    var now = ZonedDateTime.now(ZoneId.systemDefault());
    DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    var timeZone = ZoneId.systemDefault().getId();
    var time = now.plusMinutes(1).toLocalTime().format(timeFormatter);

    var hotelInfo = mockHotelInfo(timeZone, "1/1/70, " + time);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    Integer eciCiol = "null".equals(eciCiolStr) ? null : Integer.parseInt(eciCiolStr);
    Integer eciBooking = "null".equals(eciBookingStr) ? null : Integer.parseInt(eciBookingStr);
    Integer lcoCiol = "null".equals(lcoCiolStr) ? null : Integer.parseInt(lcoCiolStr);
    Integer lcoBooking = "null".equals(lcoBookingStr) ? null : Integer.parseInt(lcoBookingStr);

    var futureDate = currentDate.plusDays(daysUntilArrival);
    var request = mockPackagesRequest(futureDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(eciCiol, eciBooking, lcoCiol, lcoBooking);

    // Act
    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    // Assert
    assertThat(result, notNullValue());
    assertEquals(true, result.getIsEciCutOffCiol());
    assertEquals(true, result.getIsEciCutOffBooking());
    assertEquals(true, result.getIsLcoCutOffCiol());
    assertEquals(true, result.getIsLcoCutOffBooking());
  }

  @ParameterizedTest(name = "ECI_CIOL={0}, ECI_BOOKING={1}, LCO_CIOL={2}, LCO_BOOKING={3}, "
      + "daysUntilArrival={4}, shouldPass={5}")
  @CsvSource({
      "1440, 1440, 1440, 1440, 1",
      "2880, null, null, null, 2",
      "null, 1440, null, null, 1",
      "null, null, 1440, null, 1",
      "null, null, null, 1440, 1",
      "10, 10, 10, 10, 0"
  })
  void isCutOffByHotel_withVariousCutOffMinuteCombinations_shouldReturnFalse(
      String eciCiolStr, String eciBookingStr, String lcoCiolStr, String lcoBookingStr,
      Integer daysUntilArrival) {

    // Arrange
    var timeZone = "Europe/London";
    ZoneId zoneId = ZoneId.of(timeZone);
    var currentDateTime = ZonedDateTime.now(zoneId);

    DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    var time = currentDateTime.minusMinutes(10).toLocalTime().format(timeFormatter);

    var hotelInfo = mockHotelInfo(timeZone, "1/1/70, " + time);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    Integer eciCiol = "null".equals(eciCiolStr) ? null : Integer.parseInt(eciCiolStr);
    Integer eciBooking = "null".equals(eciBookingStr) ? null : Integer.parseInt(eciBookingStr);
    Integer lcoCiol = "null".equals(lcoCiolStr) ? null : Integer.parseInt(lcoCiolStr);
    Integer lcoBooking = "null".equals(lcoBookingStr) ? null : Integer.parseInt(lcoBookingStr);
    if ("null".equals(eciCiolStr) || "null".equals(eciBookingStr) || "null".equals(lcoCiolStr)
        || "null".equals(lcoBookingStr)) {
      when(packagesProperties.getCutOffTime()).thenReturn(daysUntilArrival);
    }
    var futureDate = currentDateTime.plusDays(daysUntilArrival).toLocalDate();
    var request = mockPackagesRequest(futureDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(eciCiol, eciBooking, lcoCiol, lcoBooking);

    // Act
    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    // Assert
    assertThat(result, notNullValue());
    assertEquals(false, result.getIsEciCutOffCiol());
    assertEquals(false, result.getIsEciCutOffBooking());
    assertEquals(false, result.getIsLcoCutOffCiol());
    assertEquals(false, result.getIsLcoCutOffBooking());
  }

  @ParameterizedTest(name = "CIOL={0}, code={1}, eciCiol={2}, lcoCiol={3}, eciBooking={4}, "
      + "lcoBooking={5}, expected={6}")
  @CsvSource({
      // CIOL flow with HSCKIN
      "true, HSCKIN, true, false, true, false, true",
      "true, HSCKIN, false, true, true, false, false",

      // CIOL flow with HSCOU2
      "true, HSCOU2, true, true, false, false, true",
      "true, HSCOU2, true, false, false, false, false",

      // Booking flow with HSCKIN
      "false, HSCKIN, true, false, true, false, true",
      "false, HSCKIN, true, false, false, false, false",

      // Booking flow with HSCOU2
      "false, HSCOU2, true, false, true, true, true",
      "false, HSCOU2, true, false, true, false, false",

      // Unknown code (should default to true)
      "true, UNKNOWN, true, true, true, true, true",
      "false, UNKNOWN, false, false, false, false, true",

      // With all flags false
      "true, HSCKIN, false, false, false, false, false",
      "false, HSCOU2, false, false, false, false, false",

      // With all flags true
      "true, HSCKIN, true, true, true, true, true",
      "false, HSCOU2, true, true, true, true, true",
  })
  void isOutsideCutOffTime_withVariousCutOffMinuteCombinations_shouldReturnCorrectly(
      Boolean isCiol, String code, Boolean eciCiolFlag, Boolean lcoCiolFlag,
      Boolean eciBookingFlag, Boolean lcoBookingFlag, Boolean expected) {

    // Arrange
    var cutOff = CutOffExtras.builder()
        .isEciCutOffCiol(eciCiolFlag)
        .isEciCutOffBooking(eciBookingFlag)
        .isLcoCutOffCiol(lcoCiolFlag)
        .isLcoCutOffBooking(lcoBookingFlag)
        .build();

    var request = mockPackagesRequest(currentDate, isCiol, false);

    // Act
    var result = cutOffPort.isOutsideCutOffTime(code, request, cutOff);

    // Assert
    assertEquals(expected, result);
  }


  @ParameterizedTest(name = "eciCiol={0}, eciBooking={1}, lcoCiol={2}, lcoBooking={3}, "
      + "timezone={4}, checkInTime={5}, shouldAllPass={6}")
  @MethodSource("provideCheckInTimeAndTimezoneScenarios")
  void isCutOffByHotel_withVariousCheckInTimesAndTimezones_shouldHandleCorrectly(
      Integer eciCiol, Integer eciBooking, Integer lcoCiol, Integer lcoBooking,
      String timezone, String checkInTime, Boolean shouldAllPass) {

    // Arrange
    var hotelInfo = mockHotelInfo(timezone, checkInTime);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    var futureDate = currentDate.plusDays(3);
    var request = mockPackagesRequest(futureDate, false, false);
    var cutOffMinutes = mockCutOffMinutes(eciCiol, eciBooking, lcoCiol, lcoBooking);

    // Act
    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    // Assert
    assertThat(result, notNullValue());
    if (shouldAllPass) {
      assertTrue(result.getIsEciCutOffCiol());
      assertTrue(result.getIsEciCutOffBooking());
      assertTrue(result.getIsLcoCutOffCiol());
      assertTrue(result.getIsLcoCutOffBooking());
    } else {
      assertFalse(result.getIsEciCutOffCiol());
      assertFalse(result.getIsEciCutOffBooking());
      assertFalse(result.getIsLcoCutOffCiol());
      assertFalse(result.getIsLcoCutOffBooking());
    }
  }

  private static Stream<Arguments> provideCheckInTimeAndTimezoneScenarios() {
    return Stream.of(
        Arguments.of(1440, 1440, 1440, 1440, "Europe/London", "1/1/70, 3:00 PM", true),
        Arguments.of(1440, 1440, 1440, 1440, "Europe/London", "1/1/70, 6:00 AM", true),
        Arguments.of(1440, 1440, 1440, 1440, "Europe/London", "1/1/70, 11:00 PM", true),
        Arguments.of(1440, 1440, 1440, 1440, "America/New_York", "1/1/70, 3:00 PM", true),
        Arguments.of(60, 60, 60, 60, "Europe/London", "1/1/70, 3:00 PM", true),
        Arguments.of(360, 360, 360, 360, "Europe/London", "1/1/70, 3:00 PM", true)
    );
  }

  @Test
  void isCutOffByHotel_withAsymmetricCutOffMinutes_shouldSetFlagsIndependently() {
    var timeZone = "Europe/London";
    ZoneId zoneId = ZoneId.of(timeZone);
    var currentDateTime = ZonedDateTime.now(zoneId);

    DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    var time = currentDateTime.minusMinutes(2).toLocalTime().format(timeFormatter);

    var hotelInfo = mockHotelInfo(timeZone, "1/1/70, " + time);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    var futureDate = currentDateTime.plusDays(1).toLocalDate();
    var request = mockPackagesRequest(futureDate, false, false);

    var cutOffMinutes = mockCutOffMinutes(1440, 60, 60, 60);

    // Act
    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    // Assert
    assertThat(result, notNullValue());
    assertFalse(result.getIsEciCutOffCiol());
    assertTrue(result.getIsEciCutOffBooking());
    assertTrue(result.getIsLcoCutOffCiol());
    assertTrue(result.getIsLcoCutOffBooking());
  }

  @Test
  void isCutOffByHotel_today() {
    DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    var time = curentTime.plusMinutes(2).toLocalTime().format(timeFormatter);
    var hotelInfo = mockHotelInfo(ZoneId.systemDefault().getId(), "1/1/70, " + time);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    var futureDate = currentDate;
    var request = mockPackagesRequest(futureDate, false, false);

    var cutOffMinutes = mockCutOffMinutes(1, 1, 1, 1);

    // Act
    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    // Assert
    assertThat(result, notNullValue());
    assertTrue(result.getIsEciCutOffCiol());
    assertTrue(result.getIsEciCutOffBooking());
    assertTrue(result.getIsLcoCutOffCiol());
    assertTrue(result.getIsLcoCutOffBooking());
  }

  @ParameterizedTest(name = "checkInTime variant should parse: {0}")
  @MethodSource("provideWhitespaceVariantsBetweenTimeAndAmPm")
  void isCutOffByHotel_whenCheckInTimeContainsSupportedUnicodeWhitespace_shouldParse(
      String checkInTime) {
    var timeZone = ZoneId.systemDefault().getId();
    var hotelInfo = mockHotelInfo(timeZone, checkInTime);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    var now = ZonedDateTime.now(ZoneId.systemDefault());
    var arrival = now.plusMinutes(30);
    var request = mockPackagesRequest(arrival.toLocalDate(), false, false);
    var cutOffMinutes = mockCutOffMinutes(1, 1, 1, 1);

    var result = assertDoesNotThrow(() -> cutOffPort.isCutOffByHotel(request, cutOffMinutes));

    assertThat(result, notNullValue());
    assertTrue(result.getIsEciCutOffCiol());
    assertTrue(result.getIsEciCutOffBooking());
    assertTrue(result.getIsLcoCutOffCiol());
    assertTrue(result.getIsLcoCutOffBooking());
  }

  @ParameterizedTest(name = "availableCleanRooms={0}")
  @NullSource
  @CsvSource({"3", "0"})
  void isCutOffByHotel_today_Eci(Long available) {
    DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    var time = curentTime.plusMinutes(2).toLocalTime().format(timeFormatter);
    var hotelInfo = mockHotelInfo(ZoneId.systemDefault().getId(), "1/1/70, " + time);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);
    if (available != null) {
      when(cleanRoomPort.availableCleanRooms(any())).thenReturn(available);
    }
    var futureDate = currentDate;
    var request = mockPackagesRequest(futureDate, true, false);

    var cutOffMinutes = mockCutOffMinutes(1, null, null, null);

    // Act
    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    // Assert
    assertThat(result, notNullValue());
    assertTrue(result.getIsEciCutOffCiol());
    if (available != null) {
      assertEquals(available, result.getCiolCurrentDateAvailableRooms());
    } else {
      assertEquals(0, result.getCiolCurrentDateAvailableRooms());
    }
    assertFalse(result.getIsEciCutOffBooking());
    assertFalse(result.getIsLcoCutOffCiol());
    assertFalse(result.getIsLcoCutOffBooking());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideCiolCurrentDateCleanRoomScenarios")
  void isCutOffByHotel_shouldFetchCleanRoomsOnlyWhenCiolCurrentDateEciCutOffIsConfigured(
      String scenario,
      int daysOffset,
      boolean isCiol,
      Integer eciCiolCutOff,
      boolean shouldFetchCleanRooms) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    var time = curentTime.plusMinutes(2).toLocalTime().format(timeFormatter);
    var hotelInfo = mockHotelInfo(ZoneId.systemDefault().getId(), "1/1/70, " + time);
    when(hotelInfoOutPort.getHotelInfo(HOTEL_ID)).thenReturn(hotelInfo);

    if (shouldFetchCleanRooms) {
      when(cleanRoomPort.availableCleanRooms(any())).thenReturn(5L);
    }

    var request = mockPackagesRequest(currentDate.plusDays(daysOffset), isCiol, false);
    var cutOffMinutes = mockCutOffMinutes(eciCiolCutOff, null, null, null);

    var result = cutOffPort.isCutOffByHotel(request, cutOffMinutes);

    if (shouldFetchCleanRooms) {
      verify(cleanRoomPort).availableCleanRooms(any());
      assertEquals(5L, result.getCiolCurrentDateAvailableRooms());
    } else {
      verify(cleanRoomPort, never()).availableCleanRooms(any());
      assertNull(result.getCiolCurrentDateAvailableRooms());
    }
  }

  @ParameterizedTest(name = "available={0}, eci={1}, cleanRooms={2}, isCiol={3}, expected={4}")
  @CsvSource({
      "3, true, 4, true, 3",
      "4, true, 3, true, 3",
      "0, true, 3, true, 0",
      "3, true, 0, true, 0",
      "3, true,  , true, 3",
      "3, true, 4, false, 3",
      "0, true, 4, false, 0",
      "5, false, 4, false, 5"})
  void isCutOffByHotel_today_Eci(Integer available, Boolean eci, Long cleanRooms, boolean isCiol, int expected) {

    var extras = new CutOffExtras(eci, null, null, null, isCiol ? cleanRooms : null);
    var request = mockPackagesRequest(currentDate, isCiol, false);
    // Act
    var result = cutOffPort.availableRooms(request, extras, available);

    // Assert
    assertEquals(expected, result);
  }

  @ParameterizedTest(name = "available={0}, isCiol={1}, ciolAvailableRooms={2}, expected={3}")
  @MethodSource("provideAvailableRoomsTestCases")
  void availableRooms(int available, boolean isCiol, Long ciolAvailableRooms, int expected) {
    var packagesRequest = mockPackagesRequest(currentDate, isCiol, false);
    var cutOffExtras = new CutOffExtras(true, false, true, false, ciolAvailableRooms);

    int result = cutOffPort.availableRooms(packagesRequest, cutOffExtras, available);

    assertEquals(expected, result);
  }


  @ParameterizedTest(name = "eciCiolCutOff={0}, eciBookingCutOff={1}, lcoCiolCutOff={2}, lcoBookingCutOff={3}, scenario={4}")
  @MethodSource("provideCutOffMinuteTestCases")
  void getCutOffMinute_withVariousExtrasCutoffs_returnsCorrectCutOffMinute(
      Integer expectedEciCiol, Integer expectedEciBooking, Integer expectedLcoCiol,
      Integer expectedLcoBooking, String scenario) {

    HotelInformationExtended contentInfo = null;

    switch (scenario) {
      case "null_content_info" -> {
        // contentInfo is null
        contentInfo = null;
      }
      case "null_extras_cutoffs" -> {
        contentInfo = new HotelInformationExtended();
        contentInfo.setExtrasCutoffs(null);
      }
      case "empty_extras_cutoffs" -> {
        contentInfo = new HotelInformationExtended();
        contentInfo.setExtrasCutoffs(Collections.emptyList());
      }
      case "only_hsckin" -> {
        contentInfo = new HotelInformationExtended();
        var hsckinCutoff = new ExtrasCutoff();
        hsckinCutoff.setCode("HSCKIN");
        hsckinCutoff.setCutOffMinutesCIOL(60);
        hsckinCutoff.setCutOffMinutesBooking(120);
        contentInfo.setExtrasCutoffs(List.of(hsckinCutoff));
      }
      case "only_hscou2" -> {
        contentInfo = new HotelInformationExtended();
        var hscou2Cutoff = new ExtrasCutoff();
        hscou2Cutoff.setCode("HSCOU2");
        hscou2Cutoff.setCutOffMinutesCIOL(90);
        hscou2Cutoff.setCutOffMinutesBooking(150);
        contentInfo.setExtrasCutoffs(List.of(hscou2Cutoff));
      }
      case "both_hsckin_and_hscou2" -> {
        contentInfo = new HotelInformationExtended();
        var hsckinCutoff = new ExtrasCutoff();
        hsckinCutoff.setCode("HSCKIN");
        hsckinCutoff.setCutOffMinutesCIOL(60);
        hsckinCutoff.setCutOffMinutesBooking(120);
        var hscou2Cutoff = new ExtrasCutoff();
        hscou2Cutoff.setCode("HSCOU2");
        hscou2Cutoff.setCutOffMinutesCIOL(90);
        hscou2Cutoff.setCutOffMinutesBooking(150);
        contentInfo.setExtrasCutoffs(List.of(hsckinCutoff, hscou2Cutoff));
      }
      case "unknown_code" -> {
        contentInfo = new HotelInformationExtended();
        var unknownCutoff = new ExtrasCutoff();
        unknownCutoff.setCode("UNKNOWN");
        unknownCutoff.setCutOffMinutesCIOL(30);
        unknownCutoff.setCutOffMinutesBooking(45);
        contentInfo.setExtrasCutoffs(List.of(unknownCutoff));
      }
      default -> throw new IllegalArgumentException("Unknown scenario: " + scenario);
    }

    // Act - Use reflection to call the private method
    CutOffMinute result = cutOffPort.getCutOffMinute(contentInfo);

    // Assert
    assertNotNull(result, "CutOffMinute should never be null");
    assertEquals(expectedEciCiol, result.eciCutOffMinutesCiol(),
        "ECI CIOL cut-off minutes should match for scenario: " + scenario);
    assertEquals(expectedEciBooking, result.eciCutOffMinutesBooking(),
        "ECI Booking cut-off minutes should match for scenario: " + scenario);
    assertEquals(expectedLcoCiol, result.lcoCutOffMinutesCiol(),
        "LCO CIOL cut-off minutes should match for scenario: " + scenario);
    assertEquals(expectedLcoBooking, result.lcoCutOffMinutesBooking(),
        "LCO Booking cut-off minutes should match for scenario: " + scenario);
  }

  private static Stream<Arguments> provideCutOffMinuteTestCases() {
    return Stream.of(
        // null_content_info: all values should be null
        Arguments.of(null, null, null, null, "null_content_info"),

        // null_extras_cutoffs: all values should be null
        Arguments.of(null, null, null, null, "null_extras_cutoffs"),

        // empty_extras_cutoffs: all values should be null
        Arguments.of(null, null, null, null, "empty_extras_cutoffs"),

        // only_hsckin: ECI values should be populated from HSCKIN, LCO values should be null
        Arguments.of(60, 120, 60, 120, "only_hsckin"),

        // only_hscou2: LCO values should be populated from HSCOU2, ECI values should be null
        Arguments.of(null, null, 90, 150, "only_hscou2"),

        // both_hsckin_and_hscou2: all values should be populated
        Arguments.of(60, 120, 90, 150, "both_hsckin_and_hscou2"),

        // unknown_code: all values should be null (unknown codes are ignored)
        Arguments.of(null, null, null, null, "unknown_code"));
  }

  private static Stream<Arguments> provideAvailableRoomsTestCases() {
    return Stream.of(
        // Non-CIOL flow scenarios
        Arguments.of(5, false, null, 5),           // withNonCiolFlow_returnsAvailableAsIs
        Arguments.of(10, false, 5L,
            10),           // withCiolFalseAndAvailableCleanRooms_ignoresAvailableCleanRooms

        // CIOL flow with null available rooms
        Arguments.of(5, true, null,
            5),            // withCiolFlowAndNullAvailableRooms_returnsAvailableAsIs

        // CIOL flow with zero available clean rooms
        Arguments.of(5, true, 0L,
            0),              // withCiolFlowAndZeroAvailableCleanRooms_returnsZero
        Arguments.of(0, true, 0L,
            0),              // withCiolFlowAndAvailableCleanRoomsZeroAndOriginalZero_returnsZero

        // CIOL flow with available clean rooms less than original
        Arguments.of(5, true, 2L,
            2),              // withCiolFlowAndAvailableCleanRoomsLessThanOriginal_returnsMinimum

        // CIOL flow with available clean rooms greater than original
        Arguments.of(5, true, 10L,
            5),             // withCiolFlowAndAvailableCleanRoomsGreaterThanOriginal_returnsOriginal

        // CIOL flow with available clean rooms equal to original
        Arguments.of(5, true, 5L,
            5),              // withCiolFlowAndAvailableCleanRoomsEqualToOriginal_returnsOriginal

        // CIOL flow with single clean room
        Arguments.of(5, true, 1L,
            1),              // withCiolFlowAndAvailableCleanRoomsOne_returnsOne

        // CIOL flow with large clean room numbers
        Arguments.of(50, true, 100L,
            50),          // withCiolTrueAndLargeCleanRoomNumbers_returnsMinimumCorrectly

        // Additional boundary test cases
        Arguments.of(0, true, 5L, 0),              // available is 0
        Arguments.of(1, true, 1L, 1),              // both are 1
        Arguments.of(100, true, 50L, 50),          // larger numbers
        Arguments.of(8, true, 8L, 8),              // equal larger numbers
        Arguments.of(3, true, 5L,
            3)               // additional case from previous parametrized test
    );
  }

  private static Stream<Arguments> provideCiolCurrentDateCleanRoomScenarios() {
    return Stream.of(
        Arguments.of("ciol true, eci cutoff set, arrival today -> fetch clean rooms", 0, true, 1,
            true),
        Arguments.of("ciol false -> do not fetch clean rooms", 0, false, 1, false),
        Arguments.of("eci ciol cutoff null -> do not fetch clean rooms", 0, true, null, false),
        Arguments.of("arrival in past (<0 window) -> do not fetch clean rooms", -1, true, 1,
            false),
        Arguments.of("arrival beyond today window (>=1440) -> do not fetch clean rooms", 2, true,
            1, false)
    );
  }

  private static Stream<Arguments> provideWhitespaceVariantsBetweenTimeAndAmPm() {
    var format = DateTimeFormatter.ofPattern("h:mma", Locale.US);
    var plusThirtyMinutes = LocalDateTime.now().plusMinutes(30).format(format);
    var oneNbsp = plusThirtyMinutes.replace("AM", "\u00A0AM").replace("PM", "\u00A0PM");
    var oneNarrowNbsp = plusThirtyMinutes.replace("AM", "\u202FAM").replace("PM", "\u202FPM");
    var manyNbsp = plusThirtyMinutes.replace("AM", "\u00A0\u00A0AM").replace("PM", "\u00A0\u00A0PM");
    var mixedNbsp = plusThirtyMinutes.replace("AM", "\u00A0\u202FAM").replace("PM", "\u00A0\u202FPM");

    return Stream.of(
        Arguments.of("1/1/70, " + oneNbsp),
        Arguments.of("1/1/70, " + oneNarrowNbsp),
        Arguments.of("1/1/70, " + manyNbsp),
        Arguments.of("1/1/70, " + mixedNbsp)
    );
  }
}
