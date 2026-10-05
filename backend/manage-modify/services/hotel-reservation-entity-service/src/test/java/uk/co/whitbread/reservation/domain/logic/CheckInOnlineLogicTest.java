package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_THIRD_PARTY_PREPAID_NOT_ALLOWED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_ROOM_STAY_RATES_INVALID;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
import org.springframework.data.util.Pair;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.out.*;
import uk.co.whitbread.reservation.domain.properties.CheckInOnlineProperties;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;

@ExtendWith(MockitoExtension.class)
class CheckInOnlineLogicTest {

  @Mock
  private CheckInOnlineProperties checkInOnlineProperties;

  @Mock
  private ThirdpartyBookingProperties thirdpartyBookingProperties;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private FeatureFlag mockedFeatureFlag;

  @InjectMocks
  private CheckInOnlineLogic checkInOnlineLogic;

  private final List<String> mobileCiolPibaList = List.of(
      "Should return false when PIBA UK card is present in the response and FF mobileCiolPiba = true, FF mobileCiolPibaCNP = false",
      "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, FF mobileCiolPibaCNP = true",
      "Should return false when PIBA DE card is present in the response and ,FF mobileCiolPiba = true, FF mobileCiolPibaCNP = false",
      "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = true, FF mobileCiolPibaCNP = false",
      "Should return false when PIBA DE card is present in the response and ,FF mobileCiolPiba = false, FF mobileCiolPibaCNP = true",
      "Should return true when no restrictions are set, FF mobileCiolPiba = false, FF mobileCiolPibaCNP = false",
      "Should return true when no restrictions are set, FF mobileCiolPiba = true, FF mobileCiolPibaCNP = true",
      "Should return true when all are compliant except country, FF mobileCiolPiba = false,FF mobileCiolPibaCNP = false",
      "Should return true when PIBA DE card is present in the response and FF mobileCiolPiba = false, and FF mobileCiolPibaCNP = false",
      "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, FF mobileCiolPibaCNP = true",
      "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, and FF mobileCiolPibaCNP = false",
      "Should return true when no restrictions are set for OTA booking",
      "Should return false when 3rd party idContext and folioView = 2",
      "Should return true when 3rd party idContext and folioView = 2",
      "Should return false when PIBA card is present and folio view = 2 in the response and FF mobileCiolPiba = true,FF mobileCiolPibaCNP = true",
      "Should return true when PIBA card is present and folio view = null in the response and FF mobileCiolPiba = true,FF mobileCiolPibaCNP = true",
      "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, and FF mobileCiolPibaCNP = true",
      "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = true, and FF mobileCiolPibaCNP = false",
      "Should return true when 3rd party and folioView = null",
      "Should return true when 3rd party and folioView = 1");

  static Stream<Arguments> ciolTestCases() {
    return Stream.of(

        // Emails not compliant
        Arguments.of(new CiolTestCase(
            "Should return false email not compliant",
            ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("wrong@wb.com"),
            null,
            false,
            null,
            false,
            false,
            false
        )),

        // All Compliant
        Arguments.of(new CiolTestCase(
            "Should return true when no restrictions are set, FF mobileCiolPiba = false, FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            false,
            false,
            false
        )),
        // All Compliant
        Arguments.of(new CiolTestCase(
            "Should return true when no restrictions are set, FF mobileCiolPiba = true, FF mobileCiolPibaCNP = true",
            ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            true,
            true,
            false
        )),

        // All Compliant except country
        Arguments.of(new CiolTestCase(
            "Should return true when all are compliant except country, FF mobileCiolPiba = false,FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Set.of("GB"),
            3,
            null,
            null,
            null,
            null,
            true,
            null,
            false,
            false,
            false
        )),

        // Null booking
        Arguments.of(new CiolTestCase(
            "Should return false when booking is null",
            null,
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            null,
            null,
            null,
            null,
            null,
            false,
            false,
            false,
            false,
            false
        )),

        // Null Country Code
        Arguments.of(new CiolTestCase(
            "Should return false when country code is null",
            ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", null),
            Collections.emptySet(),
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            false,
            false,
            false
        )),

        // Null HotelInfo
        Arguments.of(new CiolTestCase(
            "Should return false when hotel info is null",
            ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
                LocalDate.now(), "FRAMTI"),
            null,
            Collections.emptySet(),
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            false,
            false,
            false
        )),
        //booker is null
        Arguments.of(new CiolTestCase(
            "Should return false when booker is null",
            ManageReservationUtils.mockReservationByBasketRefResponseWithBooker(
                null),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("wrong@wb.com"),
            null,
            false,
            null,
            false,
            false,
            false
        )),

        //booker email is null
        Arguments.of(new CiolTestCase(
            "Should return false when booker email is null",
            ManageReservationUtils.mockReservationByBasketRefResponseWithBooker(
                ReservationBooker.builder().email(null).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("wrong@wb.com"),
            null,
            false,
            null,
            false,
            false,
            false
        )),

        //booker email is empty
        Arguments.of(new CiolTestCase(
            "Should return false when booker email is empty",
            ManageReservationUtils.mockReservationByBasketRefResponseWithBooker(
                ReservationBooker.builder().email("").build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("wrong@wb.com"),
            null,
            false,
            null,
            false,
            false,
            false
        )),
        //PIBA card, FF ciolPiba = true
        Arguments.of(new CiolTestCase(
            "Should return false when PIBA UK card is present in the response and FF mobileCiolPiba = true, FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_UK_CARD_TYPE).folioView(1).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            true,
            false,
            false
        )),
        //PIBA card, FF ciolPiba = true
        Arguments.of(new CiolTestCase(
            "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, FF mobileCiolPibaCNP = true",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_UK_CARD_TYPE).folioView(2).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            false,
            null,
            false,
            true,
            false
        )),
        //PIBADE card, FF ciolPiba = true
        Arguments.of(new CiolTestCase(
            "Should return false when PIBA DE card is present in the response and ,FF mobileCiolPiba = true, FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_EURO_CARD_TYPE).folioView(1).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            true,
            false,
            false
        )),
        Arguments.of(new CiolTestCase(
            "Should return false when PIBA DE card is present in the response and ,FF mobileCiolPiba = false, FF mobileCiolPibaCNP = true",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_EURO_CARD_TYPE).folioView(2).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            true,
            false,
            false
        )),
        //PIBA card, FF ciolPiba = true
        Arguments.of(new CiolTestCase(
            "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, and FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_UK_CARD_TYPE).folioView(1).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            false,
            false,
            false
        )),
        Arguments.of(new CiolTestCase(
            "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = false, and FF mobileCiolPibaCNP = true",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_UK_CARD_TYPE).folioView(1).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            false,
            null,
            false,
            true,
            false
        )),
        Arguments.of(new CiolTestCase(
            "Should return true when PIBA UK card is present in the response and FF mobileCiolPiba = true, and FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_UK_CARD_TYPE).folioView(2).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            false,
            null,
            false,
            true,
            false
        )),
        //PIBADE card, FF ciolPiba = false
        Arguments.of(new CiolTestCase(
            "Should return true when PIBA DE card is present in the response and FF mobileCiolPiba = false, and FF mobileCiolPibaCNP = false",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_EURO_CARD_TYPE).folioView(1).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            false,
            false,
            false
        )),
        //PIBA card, folio view = null and FF ciolPiba = true
        Arguments.of(new CiolTestCase(
            "Should return true when PIBA card is present and folio view = null in the response and FF mobileCiolPiba = true,FF mobileCiolPibaCNP = true",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_EURO_CARD_TYPE).folioView(null).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,
            null,
            true,
            true,
            false
        )),
        //PIBA card, folio view = null and FF ciolPiba = true
        Arguments.of(new CiolTestCase(
            "Should return false when PIBA card is present and folio view = 2 in the response and FF mobileCiolPiba = true,FF mobileCiolPibaCNP = true",
            ManageReservationUtils.mockReservationByBasketRefResponseWithPiba(
                LocalDate.now(), "FRAMTI", ReservationPaymentCardType.builder()
                    .cardType(HotelReservationConstants.PIBA_EURO_CARD_TYPE).folioView(2).build()),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            false,
            null,
            true,
            true,
            false
        )),
        Arguments.of(new CiolTestCase(
            "Should return true when 3rd party idContext and folioView = 2",
            ManageReservationUtils.mockReservationByBasketRefResponseThirdParty(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,     // expectedResult
            null,
            false,
            false,
            false
        )),
        Arguments.of(new CiolTestCase(
            "Should return true when 3rd party and folioView = null",
            ManageReservationUtils.mockReservationByBasketRefResponseThirdParty(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,     // expectedResult
            null,
            false,
            false,
            false
        )),
        Arguments.of(new CiolTestCase(
            "Should return true when 3rd party and folioView = 1",
            ManageReservationUtils.mockReservationByBasketRefResponseThirdParty(
                LocalDate.now(), "FRAMTI"),
            new HotelInformationResponse("UTC", "RO"),
            Collections.emptySet(),
            3,
            null,
            Set.of("TEST"),
            Set.of("test@wb.com"),
            1,
            true,     // expectedResult
            null,
            false,
            false,
            false
        ))
        );
  }


  @ParameterizedTest(name = "{0}")
  @MethodSource("ciolTestCases")
  void isCiolAvailable(CiolTestCase testCase) {
    //Arrange
    var isBookingValid = Objects.nonNull(testCase.reservation()) && Objects.nonNull(
        testCase.reservation().getReservationByIdList());
    var isHotelInfoValid = Objects.nonNull(testCase.hotelInfo()) && Objects.nonNull(
        testCase.hotelInfo().getHotelCountryCode());
    var isCiolDataValid = isBookingValid && isHotelInfoValid;

    if (testCase.countryCodes() != null && isCiolDataValid) {
      when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(testCase.countryCodes());
    }
    if (testCase.maxRooms() != null && isCiolDataValid &&
        !"DE".equals(testCase.hotelInfo.getHotelCountryCode())) {
      when(checkInOnlineProperties.getMaxRooms()).thenReturn(testCase.maxRooms());
    }
    if (testCase.hotelCodes() != null && isCiolDataValid) {
      if ("DE".equals(testCase.hotelInfo.getHotelCountryCode()) && Boolean.TRUE.equals(testCase.deRegCardFf)) {
        when(checkInOnlineProperties.getDeRegCardHotels()).thenReturn(testCase.hotelCodes());
      } else {
        when(checkInOnlineProperties.getCiolHotels()).thenReturn(testCase.hotelCodes());
      }
    }
    if (testCase.rates() != null && isCiolDataValid && isDeRegCardFfon(testCase)) {
      when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(testCase.rates());
    }
    if (!testCase.isOta() && testCase.emails() != null && isCiolDataValid && (
        null == testCase.deRegCardFf || testCase.deRegCardFf)) {
      when(checkInOnlineProperties.getCiolEmails()).thenReturn(testCase.emails());
    }
    if (testCase.deRegRooms() != null && isCiolDataValid &&
        "DE".equals(testCase.hotelInfo.getHotelCountryCode()) && Boolean.TRUE.equals(testCase.deRegCardFf)) {
      when(checkInOnlineProperties.getDeRegCardRooms())
          .thenReturn(testCase.deRegRooms());
    }
    if (testCase.deRegCardFf != null && isCiolDataValid &&
        "DE".equals(testCase.hotelInfo.getHotelCountryCode())) {
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      mockDeRegCardFeatureFlag(testCase.deRegCardFf);
    }
    if (mobileCiolPibaList.contains(testCase.description)) {
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      mockCiolPibaFeatureFlag(testCase.ciolPiba());
      mockCiolPibaCNPFeatureFlag(testCase.ciolPibaCNP());
    }
    if ("Should return false when 3rd party idContext and folioView = 2".equals(
        testCase.description())) {
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPrepaid3rdParty()))
          .thenReturn(false);
    }
    if ("Should return true when 3rd party idContext and folioView = 2".equals(
        testCase.description())) {
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPrepaid3rdParty()))
          .thenReturn(true);
    }
    String idContext = null;

    //Act
    Pair<Boolean, String> result = checkInOnlineLogic.isCiolAvailable(testCase.reservation(), testCase.hotelInfo(),
        testCase.isOta(), idContext);

    //Assert
    assertEquals(testCase.expectedResult(), result.getFirst());
  }

  @Test
  void shouldDisableCiol_returnFalse() {
    var basketResponse = ManageReservationUtils.mockBasketResponse("PI");
    assertFalse(checkInOnlineLogic.shouldDisableCiol(basketResponse));
  }

  @Test
  void shouldDisableCiol_PreCheckedIn_returnTrue() {
    var basketResponse = ManageReservationUtils.mockBasketResponse("PI");
    basketResponse.setStatus("PRE_CHECKED_IN");
    assertTrue(checkInOnlineLogic.shouldDisableCiol(basketResponse));
  }

  @Test
  void shouldDisableCiol_CiolRcFailed_returnTrue() {
    var basketResponse = ManageReservationUtils.mockBasketResponse("PI");
    basketResponse.setStatus("CIOL_RC_FAILED");
    assertTrue(checkInOnlineLogic.shouldDisableCiol(basketResponse));
  }

  @Test
  void germanHotel_MoreThanOneRoom_shouldReturnFalse() {
    //Arrange
    mockDeRegCardFeatureFlag(true);
    var booking = ManageReservationUtils.mockReservationByBasketRefFiveRooms();
    var hotelInfo = new HotelInformationResponse("UTC","DE");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getDeRegCardHotels()).thenReturn(Set.of("TESTHOTEL"));
    when(checkInOnlineProperties.getDeRegCardRooms()).thenReturn(1);

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false,null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @Test
  void ukHotel_MoreThanThreeRooms_shouldReturnFalse() {
    //Arrange
    var booking = ManageReservationUtils.mockReservationByBasketRefFiveRooms();
    var hotelInfo = new HotelInformationResponse("UTC","GB");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(3);
    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false,null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @Test
  void ukHotel_ciolEligible_DeRegCardOff() {
    //Arrange
    var booking =  ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
        LocalDate.now(), "HEAPTI");
    var hotelInfo = new HotelInformationResponse("UTC","GB");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("TEST"));
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
    mockCiolPibaFeatureFlag(false);
    mockCiolPibaCNPFeatureFlag(false);

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false,null);

    //Assert
    assertTrue(ciolAvailable.getFirst());
  }

  @Test
  void germanHotel_ciolEligible_ShouldReturnTrue() {
    //Arrange
    mockDeRegCardFeatureFlag(true);
    var booking =  ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
        LocalDate.now(), "FRAMTI");
    var hotelInfo = new HotelInformationResponse("UTC","DE");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getDeRegCardHotels()).thenReturn(Set.of("FRAMTI"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("TEST"));
    when(checkInOnlineProperties.getDeRegCardRooms()).thenReturn(1);

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertTrue(ciolAvailable.getFirst());
  }

  @Test
  void germanHotel_RegFfOff_shouldReturnFalse() {
    //Arrange
    mockDeRegCardFeatureFlag(false);
    var booking =  ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
        LocalDate.now(), "FRAMTI");
    var hotelInfo = new HotelInformationResponse("UTC","DE");

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @Test
  void countryCodeAndHotel_notCompliant_shouldReturnFalse() {
    //Arrange
    var booking =  ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
        LocalDate.now(), "HEAPTI");
    var hotelInfo = new HotelInformationResponse("UTC","GB");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("TEST"));
    when(checkInOnlineProperties.getCiolHotels()).thenReturn(Set.of("TestHotel"));

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("nonCompliantDates")
  void ukHotel_datesNotCompliant_shouldReturnFalse(ReservationByBasketRefResponse booking) {
    //Arrange
    var hotelInfo = new HotelInformationResponse("UTC","GB");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("TEST"));
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
    mockCiolPibaFeatureFlag(false);
    mockCiolPibaCNPFeatureFlag(false);

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("emptyAndNullReservationList")
  void ukHotel_emptyAndReservationList_shouldReturnFalse(ReservationByBasketRefResponse booking) {
    //Arrange
    var hotelInfo = new HotelInformationResponse("UTC","GB");
    if (Objects.nonNull(booking.getReservationByIdList())) {
      when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
      when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
    }

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @Test
  void germanHotel_ratesNotCompliant_ShouldReturnFalse() {
    //Arrange
    mockDeRegCardFeatureFlag(true);
    var booking =  ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
        LocalDate.now(), "FRAMTI");
    var hotelInfo = new HotelInformationResponse("UTC","DE");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getDeRegCardHotels()).thenReturn(Set.of("FRAMTI"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("FLEX"));
    when(checkInOnlineProperties.getDeRegCardRooms()).thenReturn(1);

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  @Test
  void ukHotel_ratesNotCompliant_ShouldReturnFalse() {
    //Arrange
    var booking =  ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
        LocalDate.now(), "Heapti");
    var hotelInfo = new HotelInformationResponse("UTC","GB");
    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of("FLEX"));
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);

    //Act
    Pair<Boolean, String> ciolAvailable = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, false, null);

    //Assert
    assertFalse(ciolAvailable.getFirst());
  }

  record CiolTestCase(
      String description,
      ReservationByBasketRefResponse reservation,
      HotelInformationResponse hotelInfo,
      Set<String> countryCodes,
      Integer maxRooms,
      Set<String> hotelCodes,
      Set<String> rates,
      Set<String> emails,
      Integer deRegRooms,
      boolean expectedResult,
      Boolean deRegCardFf,
      Boolean ciolPiba,
      Boolean ciolPibaCNP,
      Boolean isOta
  ) {

  }

  private void mockDeRegCardFeatureFlag(boolean ffToggle) {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiBbMobileDeRegCard()))
        .thenReturn(ffToggle);
  }

  private void mockCiolPibaFeatureFlag(boolean ffToggle) {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPiba()))
        .thenReturn(ffToggle);
  }

  private void mockCiolPibaCNPFeatureFlag(boolean ffToggle) {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPibaCnp()))
        .thenReturn(ffToggle);
  }

  private boolean isDeRegCardFfon(CiolTestCase testCase) {
    return null == testCase.deRegCardFf || testCase.deRegCardFf;
  }

  private static Stream<Arguments> nonCompliantDates() {
    return Stream.of(
        Arguments.of(ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
            LocalDate.now().plusDays(3), "HEAPTI")),
        Arguments.of(ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(
            LocalDate.now().minusDays(3), "HEAPTI"))
    );
  }

  private static Stream<Arguments> emptyAndNullReservationList() {
    return Stream.of(
        Arguments.of(ManageReservationUtils.mockReservationByBasketRefResponseNoReservation(List.of())),
        Arguments.of(ManageReservationUtils.mockReservationByBasketRefResponseNoReservation(null))
    );
  }

  @ParameterizedTest(name = "prepaidFFOff idContext={0}, folio={1} -> available={2}")
  @MethodSource("prepaidThirdPartyGateCases")
  void isCiolAvailable_prepaidGateElseBranch_shouldMatchExpectedOutcome(String idContext,
      Integer folioView, boolean expectedAvailability, String expectedReason) {
    lenient().when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPrepaid3rdParty()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPiba()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPibaCnp()))
        .thenReturn(false);

    when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
    when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
    when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of());
    when(checkInOnlineProperties.getCiolEmails()).thenReturn(Set.of("tester@wb.com"));
    lenient().when(checkInOnlineProperties.getDaysWithinArrival()).thenReturn(1);

    var reservation = buildReservation("ABC123");
    reservation.getReservationByIdList().getFirst().setPaymentCard(
        ReservationPaymentCardType.builder()
            .cardType("VA")
            .paymentMethod("VA")
            .folioView(folioView)
            .build());

    var result = checkInOnlineLogic.isCiolAvailable(reservation,
        new HotelInformationResponse("UTC", "GB"), false, idContext);

    assertEquals(expectedAvailability, result.getFirst());
    assertEquals(expectedReason, result.getSecond());
  }

  private static Stream<Arguments> prepaidThirdPartyGateCases() {
    return Stream.of(
        Arguments.of("3rd Party", 2, false, DIGITAL_CIOL_THIRD_PARTY_PREPAID_NOT_ALLOWED),
        Arguments.of("3rd Party", 1, true, ""),
        Arguments.of(null, 2, true, "")
    );
  }

    @ParameterizedTest(name = "[{index}] rate={0}, excluded={1} → expectedSuccess={2}")
    @MethodSource("rateComplianceArguments")
    void isCiolAvailable_rateCompliance_only(
            String rateCode,
            Set<String> excludedFromConfig,
            boolean expectedSuccess
    ) {
        lenient().when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        lenient().when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileCiolPrepaid3rdParty()))
            .thenReturn(true);
        var hotelInfo = new HotelInformationResponse("UTC", "GB");
        when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
        when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
        when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(excludedFromConfig);
        var reservation = buildReservation(rateCode);
        var result = checkInOnlineLogic.isCiolAvailable(reservation, hotelInfo, false,
            "3rd Party");
        var reason = result.getSecond();
        if (expectedSuccess) {
            assertEquals("", reason, "Expected empty reason message on success");
        } else {
            String expectedReason = String.format(DIGITAL_CIOL_ROOM_STAY_RATES_INVALID, List.of(rateCode));
            assertEquals(expectedReason, reason, "Rate error message mismatch");
        }
    }

    private ReservationByBasketRefResponse buildReservation(String rateCode) {
        var room = RoomStayByIdResponse.builder()
                .arrivalDate(LocalDate.now().toString())
                .departureDate(LocalDate.now().plusDays(1).toString())
                .adultsNumber(1)
                .childrenNumber(0)
                .ratePlanCode(rateCode)
                .roomType("SINGLE")
                .cellCode("X")
                .roomNumber("101")
                .bookingChannel("PI.com")
                .sourceCode("44")
                .build();

        var rsv = ReservationByIdResponse.builder()
                .reservationId("R1")
                .roomStay(room)
                .reservationBooker(ReservationBooker.builder().email("tester@wb.com").build())
                .paymentCard(ReservationPaymentCardType.builder()
                        .cardType("VA")
                        .paymentMethod("VA")
                        .folioView(2)
                        .cardNumberMasked("XXXXX1111")
                        .token("TOKEN")
                        .build())
                .cashiering(ResCashieringType.builder()
                        .taxType(ReservationTaxTypeInfo.builder().code("GB").build())
                        .build())
                .build();

        return ReservationByBasketRefResponse.builder()
                .hotelId("TESTHOTEL")
                .reservationByIdList(List.of(rsv))
                .build();
    }

    private static Stream<Arguments> rateComplianceArguments() {
        return Stream.of(
                Arguments.of("TM123", Set.of("TM"), false),
                Arguments.of("TM123", Set.of("ABC"), true),
                Arguments.of("TM001", Set.of("TM001"), false),
                Arguments.of("TM12", Set.of("TM"), true),
                Arguments.of("TM1234", Set.of("TM"), true),
                Arguments.of("ABC123", Set.of("TM"), true)
        );
    }

    @Test
    void otaBooking_arrivalWithinLimit_shouldReturnTrue() {
        // Arrange
        lenient().when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        when(thirdpartyBookingProperties.getDaysWithinArrival()).thenReturn(1);
        when(checkInOnlineProperties.getCiolCountryCodes()).thenReturn(Set.of("GB"));
        when(checkInOnlineProperties.getMaxRooms()).thenReturn(1);
        when(checkInOnlineProperties.getCiolRatesExcluded()).thenReturn(Set.of());

        var booking = ManageReservationUtils.mockReservationByBasketRefResponseCurrentDateRoom(LocalDate.now(), "HEAPTI");
        var hotelInfo = new HotelInformationResponse("UTC", "GB");

        // Act
        Pair<Boolean, String> result = checkInOnlineLogic.isCiolAvailable(booking, hotelInfo, true, null);

        // Assert
        assertTrue(result.getFirst(), "OTA arrival date inside allowed range should be valid");
    }
}
