package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@ExtendWith(MockitoExtension.class)
class OperaHotelsPostProcessorServiceTest {

  private static final String ARRIVAL = LocalDate
      .parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate
      .parse(LocalDate.now().plusDays(2).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  @Mock
  LosRestrictionPort losRestrictionPort;
  @Mock
  HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPostProcessorPort;

  @Mock
  CityTaxFeatureUtil cityTaxFeatureUtil;

  @Mock
  ContentClientLookUpService contentClientLookUpService;

  OperaHotelsPostProcessorPort operaHotelsPostProcessorPort;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet2;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet3;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet4;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet5;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet6;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet7;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet8;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet9;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet10;
  private List<Hotel> hotelsList;
  private OperaHotelsSearchCriteria operaHotelsSearchCriteria;
  private SearchCriteria searchCriteria;

  @BeforeEach
  void setup() {
    operaHotelsPostProcessorPort = new OperaHotelsPostProcessorService(losRestrictionPort,
        hotelAvailabilitiesPostProcessorPort, cityTaxFeatureUtil, contentClientLookUpService);
    hotelAvailabilitiesResultSet =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(0),
            "A", "SB", BigDecimal.valueOf(100.00), 10, 0, 0);

    hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(1),
            "A", "SB", BigDecimal.valueOf(100.00), 10, 1, 0);

    hotelAvailabilitiesResultSet2 =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(0),
            "A", "DB", BigDecimal.valueOf(100.00), 10, 0, 1);

    hotelAvailabilitiesResultSet3 =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(1),
            "A", "DB", BigDecimal.valueOf(50.00), 10, 1, 1);

    hotelAvailabilitiesResultSet4 =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(0),
            "S", "SB", BigDecimal.valueOf(50.00), 10, 1, 1);

    hotelAvailabilitiesResultSet9 =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(0),
            "S", "SB", BigDecimal.valueOf(50.00), 1, 1, 1);

    hotelAvailabilitiesResultSet5 =
        buildHotelAvailabilitiesResultSet("PLYPTI", LocalDate.now().plusDays(1),
            "S", "SB", BigDecimal.valueOf(50.00), 10, 1, 1);

    hotelAvailabilitiesResultSet6 =
        buildHotelAvailabilitiesResultSet("PLYLOC", LocalDate.now().plusDays(0),
            "A", "SB", BigDecimal.valueOf(70.00), 5, 1, 1);

    hotelAvailabilitiesResultSet7 =
        buildHotelAvailabilitiesResultSet("PLYLOC", LocalDate.now().plusDays(1),
            "A", "SB", BigDecimal.valueOf(70.00), 4, 1, 1);

    hotelAvailabilitiesResultSet8 = buildHotelAvailabilitiesResultSet("PLYLOC", LocalDate.now().plusDays(1),
        "A", "SB", BigDecimal.valueOf(70.00), 1, 1, 1);

    hotelAvailabilitiesResultSet10 =
        buildHotelAvailabilitiesResultSet("PLYLOC", LocalDate.now().plusDays(1),
            "A", "SB", BigDecimal.valueOf(70.00), 4, 666, 1);
  }

  @Test
  void processHotelResultSetToHotelTest() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{2},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "SB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);
    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
  }

  @Test
  void processHotelResultSetToHotelTest1() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);
    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet9);
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);
    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    Optional<Hotel> hotelOpt = hotelsReturned.stream().filter(hotel -> hotel.getHotelCode().equals("PLYPTI"))
        .findFirst();
    assertTrue(hotelOpt.isPresent());
    Hotel hotelForPlyptiCode = hotelOpt.get();
    assertEquals(2, hotelForPlyptiCode.getRates().size());
    Optional<RatePlan> ratePlan = hotelForPlyptiCode.getRates().stream()
        .filter(rate -> rate.getClassification().equals("A")).findFirst();
    assertTrue(ratePlan.isPresent());
    assertEquals(3, ratePlan.get().getRooms().size());
  }

  @Test
  void processHotelResultSetToHotelTestForHouseLevelCheck() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1},
        new int[]{0}, new int[]{1},
        new boolean[]{false}, 1,
        new String[][]{{"DOUBLE"}},
        Arrays.asList("LONEUS"));

    searchCriteria = buildSearchCriteria(Arrays.asList("STUAIR"),
        new int[]{1}, new int[]{0}, 1, new String[]{"DOUBLE"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();

    hotelAvailabilitiesResultSet =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now(),
            "A", "FMTRPL", BigDecimal.valueOf(100.00), 0, 0, 1);

    hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now(),
            "A", "DOUBLE", BigDecimal.valueOf(50.00), 1, 1, 1);

    hotelAvailabilitiesResultSet2 =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now(),
            "A", "DOUBLE", BigDecimal.valueOf(50.00), 1, 1, 1);

    hotelAvailabilitiesResultSet3 =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now(),
            "A", "PPLDBL", BigDecimal.valueOf(50.00), -2, 1, 1);

    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(0, hotelsReturned.get(0).getRates().size());
  }

  @Test
  void processHotelResultSetToHotelTestForHouseLevelCheck1() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1},
        new int[]{0}, new int[]{1},
        new boolean[]{false}, 1,
        new String[][]{{"DOUBLE"}},
        Arrays.asList("LONEUS"));

    searchCriteria = buildSearchCriteria(Arrays.asList("STUAIR"),
        new int[]{1}, new int[]{0}, 1, new String[]{"DOUBLE"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();

    hotelAvailabilitiesResultSet =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now(),
            "A", "FMTRPL", BigDecimal.valueOf(100.00), 5, 0, 1);

    hotelAvailabilitiesResultSet1 =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now().plusDays(1),
            "A", "DOUBLE", BigDecimal.valueOf(50.00), 0, 1, 1);

    hotelAvailabilitiesResultSet2 =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now().plusDays(2),
            "A", "DOUBLE", BigDecimal.valueOf(50.00), 0, 1, 1);

    hotelAvailabilitiesResultSet3 =
        buildHotelAvailabilitiesResultSet("LONEUS", LocalDate.now().plusDays(2),
            "A", "PPLDBL", BigDecimal.valueOf(50.00), 0, 1, 1);

    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(0, hotelsReturned.get(0).getRates().size());
  }

  @Test
  void processHotelResultSetToHotelTestWithMultiRoom() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{2, 2},
        new boolean[]{false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1, 1, 1}, new int[]{0, 0, 0, 0}, 4, new String[]{"SB", "DB", "SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);
    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet9);
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);
    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    Optional<Hotel> hotelOpt = hotelsReturned.stream().filter(hotel -> hotel.getHotelCode().equals("PLYPTI"))
        .findFirst();
    assertTrue(hotelOpt.isPresent());
    Hotel hotelForPlyptiCode = hotelOpt.get();
    assertEquals(1, hotelForPlyptiCode.getRates().size());
    Optional<RatePlan> ratePlan = hotelForPlyptiCode.getRates().stream()
        .filter(rate -> rate.getClassification().equals("A")).findFirst();
    assertTrue(ratePlan.isPresent());
    assertEquals(2, ratePlan.get().getRooms().size());
    long qtyRequested = ratePlan.get().getRooms().get(0).getQtyRequested();
    assertEquals(2, qtyRequested);
  }

  @Test
  void processHotelResultSetToHotelTestHotelCodesNotPresentInDb() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    Optional<Hotel> hotelOpt = hotelsReturned.stream().filter(hotel -> hotel.getHotelCode().equals("PLYLOC"))
        .findFirst();
    assertTrue(hotelOpt.isPresent());
    Hotel hotelForPLYLOCCode = hotelOpt.get();
    assertEquals(0, hotelForPLYLOCCode.getRates().size());
  }


  @Test
  void processHotelResultSetToHotelTestHotelCodesNotPresentInDb_1() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    Optional<Hotel> hotelOpt = hotelsReturned.stream().filter(hotel -> hotel.getHotelCode().equals("PLYPTI"))
        .findFirst();
    assertTrue(hotelOpt.isPresent());
    Hotel hotelForPlyptiCode = hotelOpt.get();
    assertEquals(0, hotelForPlyptiCode.getRates().size());
  }

  @Test
  void processHotelResultSetToHotelTestHotelCodesWithEmptyResultset() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, Collections.emptyList());
    assertTrue(hotelsReturned.isEmpty());
  }

  @Test
  void isAtleastOneRoomPresentForEachRoomTypeTest() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));
    final List<Room> room = Arrays.asList(Room.builder().type("EXTSB").build());
    assertFalse(operaHotelsPostProcessorPort
        .isAtleastOneRoomPresentForEachRoomType(room, operaHotelsSearchCriteria));
  }

  @Test
  void isAtleastOneRoomPresentForEachRoomTypeTrueTest() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));
    final List<Room> room = Arrays.asList(Room.builder().type("SBDB").build());
    assertTrue(operaHotelsPostProcessorPort
        .isAtleastOneRoomPresentForEachRoomType(room, operaHotelsSearchCriteria));
  }

  @Test
  void isAtleastOneRoomPresentForEachRoomTypeTrueWithMultiRoomsTest() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB"}, {"DB", "SBDB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));
    final List<Room> room = Arrays.asList(Room.builder().type("EXTSB").build(), Room.builder().type("SBDB").build());
    assertTrue(operaHotelsPostProcessorPort
        .isAtleastOneRoomPresentForEachRoomType(room, operaHotelsSearchCriteria));
  }

  @Test
  void testForClosedLosRestrictionTrue() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setMinNights(666);
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);
    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet9);
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet10);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    Optional<Hotel> hotelOpt = hotelsReturned.stream().filter(hotel -> hotel.getHotelCode().equals("PLYPTI"))
        .findFirst();
    assertTrue(hotelOpt.isPresent());
    Hotel hotelForPlyptiCode = hotelOpt.get();
    assertEquals(1, hotelForPlyptiCode.getRates().size());
    Optional<RatePlan> ratePlan = hotelForPlyptiCode.getRates().stream()
        .filter(rate -> rate.getClassification().equals("A")).findFirst();
    assertFalse(ratePlan.isPresent());
  }

  @Test
  void testForClosedLosRestrictionWithFalse() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SB"}},
        Arrays.asList("PLYPTI", "PLYLOC"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "DB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setMinNights(666);
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);
    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet9);
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet10);

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);
    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    Optional<Hotel> hotelOpt = hotelsReturned.stream().filter(hotel -> hotel.getHotelCode().equals("PLYPTI"))
        .findFirst();
    assertTrue(hotelOpt.isPresent());
    Hotel hotelForPlyptiCode = hotelOpt.get();
    assertEquals(1, hotelForPlyptiCode.getRates().size());
    Optional<RatePlan> ratePlan = hotelForPlyptiCode.getRates().stream()
        .filter(rate -> rate.getClassification().equals("S")).findFirst();
    assertTrue(ratePlan.isPresent());
    RatePlan ratePlanFrmDb = ratePlan.get();
    Integer qtyAvailable = ratePlanFrmDb.getRooms().stream().findAny().get().getQuantityAvailable();
    assertNotNull(qtyAvailable);
    assertEquals(1, qtyAvailable.intValue());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void processHotelResultSetToHotel_cityTaxEnabled(boolean isFallbackEnabled) {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{2},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}},
        Arrays.asList("PLYPTI"));

    searchCriteria = buildSearchCriteria(Arrays.asList("PLYLOC"),
        new int[]{1, 1}, new int[]{0, 0}, 2, new String[]{"SB", "SB"});

    final List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    doReturn(true).when(cityTaxFeatureUtil).isFeatureEnabled();
    doReturn(isFallbackEnabled).when(cityTaxFeatureUtil).isFallbackEnabled();
    applyCityTaxToHotelAvailabilities(hotelAvailList);

    var cityTaxInfo = createMockHotelsCityTaxInfo();
    doReturn(cityTaxInfo).when(contentClientLookUpService).getHotelsCityTaxInfo(any(), any(), any());

    final List<Hotel> hotelsReturned = operaHotelsPostProcessorPort
        .processHotelResultSetToHotel(operaHotelsSearchCriteria, searchCriteria, hotelAvailList);

    assertFalse(hotelsReturned.isEmpty());
    assertEquals(2, hotelsReturned.size());
    verify(contentClientLookUpService).getHotelsCityTaxInfo(any(), any(), any());
  }

  private void applyCityTaxToHotelAvailabilities(List<HotelAvailabilitiesResultSet> hotelAvailList) {
    if (hotelAvailList != null) {
      hotelAvailList.stream()
          .filter(hotelAvailabilities -> hotelAvailabilities != null && hotelAvailabilities.getAmount() != null)
          .forEach(hotelAvailabilities ->
              hotelAvailabilities.setAmountWithCityTax(hotelAvailabilities.getAmount().add(BigDecimal.TEN))
          );
    }
  }

  private HotelsCityTaxInfo createMockHotelsCityTaxInfo() {
    List<String> hotelsWithCityTax = List.of("PLYPTI");
    Map<String, HotelCityTax> hotelsCityTaxes = Map.of("PLYPTI",
        HotelCityTax.builder().effectiveFrom(LocalDate.now().minusDays(1).toString())
            .amount(BigDecimal.TEN).vat(BigDecimal.ONE).build());

    return HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(hotelsWithCityTax)
        .hotelsCityTaxes(hotelsCityTaxes)
        .build();
  }

  private OperaHotelsSearchCriteria buildOperaHotelsSearchCriteria(int[] adults,
                                                                   int[] children,
                                                                   int[] roomQty, boolean[] cot,
                                                                   int rooms, String[][] roomTypes,
                                                                   List<String> hotelCodes) {
    return OperaHotelsSearchCriteria.builder()
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .country(COUNTRY)
        .language(LANGUAGE)
        .adults(adults)
        .children(children)
        .roomQty(roomQty)
        .cot(cot)
        .rooms(rooms)
        .roomTypes(roomTypes)
        .hotelCodes(hotelCodes)
        .build();
  }

  private SearchCriteria buildSearchCriteria(List<String> hotelCodes, int[] adults,
      int[] children, int rooms, String[] roomTypes) {
    return SearchCriteria.builder()
        .hotelCodes(hotelCodes)
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .adults(adults)
        .children(children)
        .cot(false)
        .rooms(rooms)
        .type(roomTypes)
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }

  private HotelAvailabilitiesResultSet buildHotelAvailabilitiesResultSet(final String hotelCode,
      final LocalDate availDate,
      final String rateClassification,
      final String roomType,
      final BigDecimal amount,
      final int quantity,
      final int minNights,
      final int maxNights) {
    return HotelAvailabilitiesResultSet.builder()
        .hotelCode(hotelCode)
        .availableDate(availDate)
        .currency("GBP")
        .rateClassification(rateClassification)
        .roomType(roomType)
        .amount(amount)
        .quantity(quantity)
        .minNights(minNights)
        .maxNights(maxNights)
        .build();
  }


}
