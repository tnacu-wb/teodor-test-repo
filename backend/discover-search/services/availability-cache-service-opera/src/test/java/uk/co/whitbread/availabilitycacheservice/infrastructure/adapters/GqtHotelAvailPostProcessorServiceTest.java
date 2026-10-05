package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Rate;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@ExtendWith(MockitoExtension.class)
class GqtHotelAvailPostProcessorServiceTest {

  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;

  private GqtHotelAvailPostProcessorPort gqtHotelAvailPostProcessorPort;

  @BeforeEach
  void setup() {
    gqtHotelAvailPostProcessorPort = new GqtHotelAvailPostProcessorService(cityTaxFeatureUtil);
  }


  @Test
  void shouldReturnEmptyCollectionIfResultSetIsEmpty() {

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("MANOLD");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailPostProcessorPort
            .processHotelResultSetToGqtHotelAvail(Collections.emptyList(), gqtSearchPayload);

    assertTrue(gqtOperaHotelAvailabilitiesList.isEmpty());

  }

  @Test
  void performGqtHotelsPostProcessTest() {
    final String opera = "OPERA";

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("MANOLD");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    final String hotelId1 = "MANOLD_".concat(arrival.toString()).concat("_OPERA");
    //rateId=F_MANOLD_PRE_OPERA_2023-04-06
    final String rateId1 = "F_".concat("MANOLD_PRE_OPERA_").concat(arrival.toString());
    //roomId=PRE_MANOLD_OPERA_2023-04-06
    final String roomId = "PRE_MANOLD_OPERA_".concat(arrival.toString());

    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet =
        new ArrayList<>();

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet(hotelId1, "MANOLD", arrival, opera, rateId1,
            true, "F", "BUSIFLEX",
            new BigDecimal("80.00"), "G", 1, 5, roomId, 9,
            "PRE");

    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet1);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailPostProcessorPort.
            processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSet, gqtSearchPayload);

    assertNotNull(gqtOperaHotelAvailabilitiesList);
    assertFalse(gqtOperaHotelAvailabilitiesList.isEmpty());

  }

  @Test
  void performGqtHotelsPostProcessWithMultipleRatesAndRoomsTest() {
    final String opera = "OPERA";

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("HEAPTI");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(1);
    final String hotelId1 = "HEAPTI_".concat(arrival.toString()).concat("_OPERA");
    //rateId=F_MANOLD_PRE_OPERA_2023-04-06
    final String rateId1 = "S_".concat("HEAPTI_SB_OPERA_").concat(arrival.toString());
    //roomId=PRE_MANOLD_OPERA_2023-04-06
    final String roomId1 = "SB_HEAPTI_OPERA_".concat(arrival.toString());

    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet =
        new ArrayList<>();

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet(hotelId1, "HEAPTI", arrival, opera, rateId1,
            true, "S", "ADVANCE",
            new BigDecimal("80.00"), "G", 1, 5, roomId1, 9,
            "SB");

    final String rateId2 = "S_".concat("HEAPTI_DB_OPERA_").concat(arrival.toString());
    final String roomId2 = "DB_HEAPTI_OPERA_".concat(arrival.toString());

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet2 =
        buildHotelAvailabilitiesResultSet(hotelId1, "HEAPTI", arrival, opera, rateId2,
            true, "S", "ADVANCE",
            new BigDecimal("60.00"), "G", 2, 6, roomId2, 7,
            "DB");

    final String rateId3 = "U_".concat("HEAPTI_SB_OPERA_").concat(arrival.toString());
    final String roomId3 = "SB_HEAPTI_OPERA_".concat(arrival.toString());

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet3 =
        buildHotelAvailabilitiesResultSet(hotelId1, "HEAPTI", arrival, opera, rateId3,
            true, "U", "STANDARD",
            new BigDecimal("50.00"), "G", 1, 9, roomId3, 5,
            "SB");

    final String rateId4 = "U_".concat("HEAPTI_DB_OPERA_").concat(arrival.toString());
    final String roomId4 = "DB_HEAPTI_OPERA_".concat(arrival.toString());

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet4 =
        buildHotelAvailabilitiesResultSet(hotelId1, "HEAPTI", arrival, opera, rateId4,
            true, "U", "STANDARD",
            new BigDecimal("20.00"), "G", 999, 0, roomId4, 5,
            "DB");

    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet1);
    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet2);
    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet3);
    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet4);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailPostProcessorPort.
            processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSet, gqtSearchPayload);

    assertNotNull(gqtOperaHotelAvailabilitiesList);
    assertFalse(gqtOperaHotelAvailabilitiesList.isEmpty());
    //single hotel
    assertEquals(1, gqtOperaHotelAvailabilitiesList.size());
    //single date
    assertEquals(1,
        gqtOperaHotelAvailabilitiesList.get(0).getAvailabilities().size());

    Set<String> expectedClassifications = new HashSet<>();
    expectedClassifications.add("S");
    expectedClassifications.add("U");

    Set<String> expectedRoomTypes = new HashSet<>();
    expectedRoomTypes.add("SB");
    expectedRoomTypes.add("DB");

    for (GqtOperaHotelAvailabilities OperaHotelAvailabilities : gqtOperaHotelAvailabilitiesList) {
      assertTrue(hotelCodes.contains(OperaHotelAvailabilities.getHotelCode()));
      Set<Availabilities> availabilitiesSet = OperaHotelAvailabilities.getAvailabilities();
      for (Availabilities availabilities : availabilitiesSet) {

        assertEquals(arrival, availabilities.getAvailableDate());
        assertEquals(2, availabilities.getRates().size());
        Set<String> actualClassifications =
            availabilities.getRates().stream()
                .map(Rate::getClassification).collect(Collectors.toSet());

        assertEquals(expectedClassifications, actualClassifications);

        Set<Rate> rateSet = availabilities.getRates();
        for (Rate rate : rateSet) {
          Set<Room> roomSet = rate.getRooms();
          assertEquals(2, roomSet.size());
          Set<String> actualRoomTypes =
              rate.getRooms().stream()
                  .map(Room::getRoomType).collect(Collectors.toSet());
          assertEquals(expectedRoomTypes, actualRoomTypes);
        }

      }
    }
  }

  @Test
  void ifNoAvailabilitiesForAHotelInDbThenItShouldBeReturnedAsEmpty() {
    final String opera = "OPERA";

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("MANOLD");
    hotelCodes.add("OXFORD");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    final String hotelId1 = "MANOLD_".concat(arrival.toString()).concat("_OPERA");
    //rateId=F_MANOLD_PRE_OPERA_2023-04-06
    final String rateId1 = "F_".concat("MANOLD_PRE_OPERA_").concat(arrival.toString());
    //roomId=PRE_MANOLD_OPERA_2023-04-06
    final String roomId = "PRE_MANOLD_OPERA_".concat(arrival.toString());

    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet =
        new ArrayList<>();

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet(hotelId1, "MANOLD", arrival, opera, rateId1,
            true, "F", "BUSIFLEX",
            new BigDecimal("80.00"), "G", 1, 5, roomId, 9,
            "PRE");

    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet1);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailPostProcessorPort.
            processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSet, gqtSearchPayload);

    assertNotNull(gqtOperaHotelAvailabilitiesList);
    assertFalse(gqtOperaHotelAvailabilitiesList.isEmpty());
    Map<String, List<GqtOperaHotelAvailabilities>> codeAvailabilities =
        gqtOperaHotelAvailabilitiesList.stream()
            .collect(Collectors.groupingBy(GqtOperaHotelAvailabilities::getHotelCode));

    assertEquals(1, codeAvailabilities.get("MANOLD").size());
    assertEquals(1, codeAvailabilities.get("OXFORD").size());

    assertFalse(codeAvailabilities.get("MANOLD").get(0).getAvailabilities().isEmpty());

    assertTrue(codeAvailabilities.get("OXFORD").get(0).getAvailabilities().isEmpty());

  }

  @Test
  void performGqtHotelsPostProcessTest_with_negative_value_house_level_check() {
    final String opera = "OPERA";

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("MANOLD");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    final String hotelId1 = "MANOLD_".concat(arrival.toString()).concat("_OPERA");
    //rateId=F_MANOLD_PRE_OPERA_2023-04-06
    final String rateId1 = "F_".concat("MANOLD_PRE_OPERA_").concat(arrival.toString());
    //roomId=PRE_MANOLD_OPERA_2023-04-06
    final String roomId = "PRE_MANOLD_OPERA_".concat(arrival.toString());

    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet =
        new ArrayList<>();

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet(hotelId1, "MANOLD", arrival, opera, rateId1,
            true, "F", "BUSIFLEX",
            new BigDecimal("80.00"), "G", 1, 5, roomId, 4,
            "DOUBLE");

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet2 =
        buildHotelAvailabilitiesResultSet(hotelId1, "MANOLD", arrival, opera, rateId1,
            true, "F", "BUSIFLEX",
            new BigDecimal("80.00"), "G", 1, 5, roomId, -5,
            "FMTRPL");

    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet1);
    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet2);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailPostProcessorPort.
            processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSet, gqtSearchPayload);

    assertNotNull(gqtOperaHotelAvailabilitiesList);
    assertTrue(gqtOperaHotelAvailabilitiesList.isEmpty());
  }

  @Test
  void performGqtHotelsPostProcessTest_with_zero_value_house_level_check() {
    final String opera = "OPERA";

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("MANOLD");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    final String hotelId1 = "MANOLD_".concat(arrival.toString()).concat("_OPERA");
    //rateId=F_MANOLD_PRE_OPERA_2023-04-06
    final String rateId1 = "F_".concat("MANOLD_PRE_OPERA_").concat(arrival.toString());
    //roomId=PRE_MANOLD_OPERA_2023-04-06
    final String roomId = "PRE_MANOLD_OPERA_".concat(arrival.toString());

    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet =
        new ArrayList<>();

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet(hotelId1, "MANOLD", arrival, opera, rateId1,
            true, "F", "BUSIFLEX",
            new BigDecimal("80.00"), "G", 1, 5, roomId, 4,
            "DOUBLE");

    final HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet2 =
        buildHotelAvailabilitiesResultSet(hotelId1, "MANOLD", arrival, opera, rateId1,
            true, "F", "BUSIFLEX",
            new BigDecimal("80.00"), "G", 1, 5, roomId, -4,
            "FMTRPL");

    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet1);
    hotelAvailabilitiesResultSet.add(hotelAvailabilitiesResultSet2);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailPostProcessorPort.
            processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSet, gqtSearchPayload);

    assertNotNull(gqtOperaHotelAvailabilitiesList);
    assertTrue(gqtOperaHotelAvailabilitiesList.isEmpty());
  }

  @Test
  void testProcessHotelResultSetToGqtHotelAvail_cityTaxAppliedToRoom() {
    // Arrange
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(false);

    String hotelCode = "HOTEL1";
    LocalDate arrival = LocalDate.now();
    LocalDate departure = arrival.plusDays(1);

    BigDecimal originalAmount = BigDecimal.valueOf(100);
    BigDecimal expectedAmountWithCityTax = BigDecimal.valueOf(110);

    HotelCityTax hotelCityTax = mock(HotelCityTax.class);
    HotelsCityTaxInfo hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);
    when(hotelsCityTaxInfo.getHotelsCityTaxes()).thenReturn(Map.of(hotelCode, hotelCityTax));

    HotelAvailabilitiesResultSet resultSet = HotelAvailabilitiesResultSet.builder()
        .hotelId("HOTEL1_" + arrival)
        .hotelCode(hotelCode)
        .availableDate(arrival)
        .pmsSource("OPERA")
        .rateId("R1")
        .availability(true)
        .rateClassification("S")
        .rateCode("ADVANCE")
        .amount(originalAmount)
        .currency("G")
        .minNights(1)
        .maxNights(5)
        .roomId("ROOM1")
        .quantity(2)
        .roomType("DB")
        .build();

    GqtSearchPayload criteria = GqtSearchPayload.builder()
        .hotelCodes(List.of(hotelCode))
        .arrival(arrival.toString())
        .departure(departure.toString())
        .country("GB")
        .language("EN")
        .hotelsCityTaxInfo(hotelsCityTaxInfo)
        .build();

    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any(), any()))
          .thenReturn(true);
      cityTaxUtilMock.when(() ->
          CityTaxUtil.shouldCalculateCityTax(eq(false), eq(null))).thenReturn(true);
      cityTaxUtilMock.when(() -> CityTaxUtil.getAmountWithCityTax(eq(hotelCityTax), eq(hotelCode),
              eq(originalAmount), anyInt(), anyInt()))
          .thenReturn(expectedAmountWithCityTax);

      // Act
      List<GqtOperaHotelAvailabilities> result = gqtHotelAvailPostProcessorPort
          .processHotelResultSetToGqtHotelAvail(List.of(resultSet), criteria);

      // Assert
      assertThat(result).hasSize(1);
      GqtOperaHotelAvailabilities hotelAvail = result.get(0);
      assertThat(hotelAvail.getHotelCode()).isEqualTo(hotelCode);
      Set<Availabilities> availabilities = hotelAvail.getAvailabilities();
      assertThat(availabilities).hasSize(1);

      Availabilities avail = availabilities.iterator().next();
      Set<Rate> rates = avail.getRates();
      assertThat(rates).hasSize(1);

      Rate rate = rates.iterator().next();
      Set<Room> rooms = rate.getRooms();
      assertThat(rooms).hasSize(1);

      Room room = rooms.iterator().next();
      assertThat(room.getAmount()).isEqualTo(expectedAmountWithCityTax);
    }
  }

  @Test
  void testProcessHotelResultSetToGqtHotelAvail_applyCityTaxFromResultSet() {
    // Arrange
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(false);

    String hotelCode = "HOTEL1";
    LocalDate arrival = LocalDate.now();
    LocalDate departure = arrival.plusDays(1);

    BigDecimal originalAmount = BigDecimal.valueOf(100);
    BigDecimal expectedAmountWithCityTax = BigDecimal.valueOf(120);

    HotelsCityTaxInfo hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);

    HotelAvailabilitiesResultSet resultSet = HotelAvailabilitiesResultSet.builder()
        .hotelId("HOTEL1_" + arrival)
        .hotelCode(hotelCode)
        .availableDate(arrival)
        .pmsSource("OPERA")
        .rateId("R1")
        .availability(true)
        .rateClassification("S")
        .rateCode("ADVANCE")
        .amount(originalAmount)
        .amountWithCityTax(expectedAmountWithCityTax)
        .currency("G")
        .minNights(1)
        .maxNights(5)
        .roomId("ROOM1")
        .quantity(2)
        .roomType("DB")
        .build();

    GqtSearchPayload criteria = GqtSearchPayload.builder()
        .hotelCodes(List.of(hotelCode))
        .arrival(arrival.toString())
        .departure(departure.toString())
        .country("GB")
        .language("EN")
        .hotelsCityTaxInfo(hotelsCityTaxInfo)
        .build();

    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any(), any()))
          .thenReturn(true);
      cityTaxUtilMock.when(() ->
          CityTaxUtil.shouldCalculateCityTax(eq(false),
              eq(resultSet.getAmountWithCityTax()))).thenReturn(false);

      // Act
      List<GqtOperaHotelAvailabilities> result = gqtHotelAvailPostProcessorPort
          .processHotelResultSetToGqtHotelAvail(List.of(resultSet), criteria);

      // Assert
      assertThat(result).hasSize(1);
      GqtOperaHotelAvailabilities hotelAvail = result.get(0);
      assertThat(hotelAvail.getHotelCode()).isEqualTo(hotelCode);
      Set<Availabilities> availabilities = hotelAvail.getAvailabilities();
      assertThat(availabilities).hasSize(1);

      Availabilities avail = availabilities.iterator().next();
      Set<Rate> rates = avail.getRates();
      assertThat(rates).hasSize(1);

      Rate rate = rates.iterator().next();
      Set<Room> rooms = rate.getRooms();
      assertThat(rooms).hasSize(1);

      Room room = rooms.iterator().next();
      assertThat(room.getAmount()).isEqualTo(expectedAmountWithCityTax);
    }
  }

  private HotelAvailabilitiesResultSet buildHotelAvailabilitiesResultSet(
      final String hotelId, final String hotelCode, final LocalDate availDate,
      final String pmsSource, final String rateId, final boolean availability,
      final String classification, final String rateCode, BigDecimal amount,
      final String currency, final int minNights, final int maxNights, final String roomId,
      final int quantity, final String roomType
  ) {

    return HotelAvailabilitiesResultSet.builder()
        .hotelId(hotelId)
        .hotelCode(hotelCode)
        .availableDate(availDate)
        .pmsSource(pmsSource)
        .rateId(rateId)
        .availability(availability)
        .rateClassification(classification)
        .rateCode(rateCode)
        .amount(amount)
        .currency(currency)
        .minNights(minNights)
        .maxNights(maxNights)
        .roomId(roomId)
        .quantity(quantity)
        .roomType(roomType)
        .build();
  }

  private GqtSearchPayload buildGqtSearchPayload(
      final List<String> hotelCodes, final String arrival, final String departure,
      final String country, final String language
  ) {
    return GqtSearchPayload.builder()
        .hotelCodes(hotelCodes)
        .arrival(arrival)
        .departure(departure)
        .country(country)
        .language(language)
        .build();
  }

}
