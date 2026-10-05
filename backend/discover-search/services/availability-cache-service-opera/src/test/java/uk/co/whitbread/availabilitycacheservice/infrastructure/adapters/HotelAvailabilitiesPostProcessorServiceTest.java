package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import mocks.HotelMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelRatesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@ExtendWith(MockitoExtension.class)
class HotelAvailabilitiesPostProcessorServiceTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(2).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  //    @Value("${low.inventory.threshold}")
  private final int lowInventoryThreshold = 10;
  @Mock
  LosRestrictionPort losRestrictionPort;
  @Mock
  HotelRatesPostProcessorPort hotelRatesPostProcessorPort;
  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;
  private HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPersistencePostProcessorPort;
  private SearchCriteria searchCriteria;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet1;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet2;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet3;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet4;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet5;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet6;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet7;
  private HotelAvailabilitiesResultSet hotelAvailabilitiesResultSet8;
  private List<Hotel> hotelsList;
  //    @Value("${enable.citytax.currency}")
  private boolean isCitytaxCurrencySupported = false;

  @BeforeEach
  void setup() {
    searchCriteria = buildSearchCriteria();
    hotelAvailabilitiesPersistencePostProcessorPort = new HotelAvailabilitiesPostProcessorService(
        losRestrictionPort, hotelRatesPostProcessorPort, cityTaxFeatureUtil, lowInventoryThreshold,
        isCitytaxCurrencySupported);

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
    hotelsList = HotelMock.buildAllHotels();
  }

  @Test
  void testMapRateFromResultSetToRatePlan_emptyInput_returnsEmptyList() {
    List<RatePlan> result = hotelAvailabilitiesPersistencePostProcessorPort
        .mapRateFromResultSetToRatePlan("HOTEL1", buildSearchCriteria1(), Collections.emptyList());
    assertThat(result).isEmpty();
  }

  @Test
  void testMapRateFromResultSetToRatePlan_noRatesFetched_returnsEmptyList() {
    HotelAvailabilitiesResultSet resultSet = HotelAvailabilitiesResultSet.builder()
        .hotelCode("HOTEL1")
        .rateClassification("")
        .roomType("SB")
        .amount(BigDecimal.valueOf(100))
        .availableDate(LocalDate.now())
        .currency("GBP")
        .quantity(1)
        .minNights(1)
        .maxNights(2)
        .build();
    List<RatePlan> result = hotelAvailabilitiesPersistencePostProcessorPort
        .mapRateFromResultSetToRatePlan("HOTEL1", buildSearchCriteria1(), List.of(resultSet));
    assertThat(result).isEmpty();
  }

  @Test
  void testMapRateFromResultSetToRatePlan_addsRatePlan_andSetsRoomPriceWithCityTax() {
    SearchCriteria criteria = buildSearchCriteria1();
    HotelAvailabilitiesResultSet resultSet = HotelAvailabilitiesResultSet.builder()
        .hotelCode("HOTEL1")
        .rateClassification("A")
        .roomType("SB")
        .amount(BigDecimal.valueOf(100))
        .availableDate(LocalDate.parse(criteria.getArrival()))
        .currency("GBP")
        .quantity(1)
        .minNights(1)
        .maxNights(2)
        .amountWithCityTax(BigDecimal.valueOf(110))
        .build();

    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);

    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(),
          any())).thenReturn(true);

      List<RatePlan> result = hotelAvailabilitiesPersistencePostProcessorPort
          .mapRateFromResultSetToRatePlan("HOTEL1", criteria, List.of(resultSet));
      assertThat(result).hasSize(1);
      RatePlan ratePlan = result.get(0);
      assertThat(ratePlan.getRooms()).hasSize(1);

      Room room = ratePlan.getRooms().get(0);
      assertThat(room.getTotalPrice().getAmount()).isEqualTo(BigDecimal.valueOf(110));
      assertThat(room.getTotalPrice().getCurrency()).isEqualTo("GBP");
    }
  }

  @Test
  void testMapRateFromResultSetToRatePlan_addsRatePlan_andCalculatesCityTax() {
    SearchCriteria criteria = buildSearchCriteria1();
    criteria.setHotelsCityTaxInfo(HotelsCityTaxInfo.builder().hotelsCityTaxes(new HashMap<>()).build());
    HotelAvailabilitiesResultSet resultSet = HotelAvailabilitiesResultSet.builder()
        .hotelCode("HOTEL1")
        .rateClassification("A")
        .roomType("SB")
        .amount(BigDecimal.valueOf(100))
        .availableDate(LocalDate.parse(criteria.getArrival()))
        .currency("GBP")
        .quantity(1)
        .minNights(1)
        .maxNights(2)
        .build();

    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(false);

    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(),
          any(), any())).thenReturn(true);
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldCalculateCityTax(
          false, BigDecimal.ZERO)).thenReturn(true);
      cityTaxUtilMock.when(() -> CityTaxUtil.getAmountWithCityTax(
          any(), any(), any(), anyInt(), anyInt())).thenReturn(BigDecimal.valueOf(120));

      List<RatePlan> result = hotelAvailabilitiesPersistencePostProcessorPort
          .mapRateFromResultSetToRatePlan("HOTEL1", criteria, List.of(resultSet));
      assertThat(result).hasSize(1);
      RatePlan ratePlan = result.get(0);
      assertThat(ratePlan.getRooms()).hasSize(1);

      Room room = ratePlan.getRooms().get(0);
      assertThat(room.getTotalPrice().getAmount()).isEqualTo(BigDecimal.valueOf(120));
      assertThat(room.getTotalPrice().getCurrency()).isEqualTo("GBP");
    }
  }

  private SearchCriteria buildSearchCriteria1() {
    return SearchCriteria.builder()
        .hotelCodes(List.of("HOTEL1"))
        .arrival(LocalDate.now().toString())
        .departure(LocalDate.now().plusDays(1).toString())
        .adults(new int[]{1})
        .children(new int[]{0})
        .cot(false)
        .rooms(1)
        .type(new String[]{"SB"})
        .language("en")
        .country("gb")
        .build();
  }

  @Test
  void mapRoomFromResultSetToRoomReturnEmptyListTest() {
    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);

    List<Room> roomsList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRoomFromResultSetToRoom("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(Collections.emptyList(), roomsList);
  }

  @Test
  void mapRoomFromResultSetToRoomReturnWithEmptyHotelAvailabilitiesTest() {
    List<Room> roomsList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRoomFromResultSetToRoom("PLYPTI", searchCriteria, Collections.emptyList());
    assertEquals(Collections.emptyList(), roomsList);
  }


  @Test
  void mapRoomFromResultSetToRoomSuccessTest() {

    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    List<Room> roomsList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRoomFromResultSetToRoom("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(1, roomsList.size());
    assertEquals(BigDecimal.valueOf(200.00), roomsList.get(0).getTotalPrice().getAmount());
  }

  @Test
  void mapRoomFromResultSetToRoomEuroSuccessTest() {

    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setCurrency("E");
    hotelAvailabilitiesResultSet1.setCurrency("E");
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    List<Room> roomsList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRoomFromResultSetToRoom("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(1, roomsList.size());
    assertEquals(BigDecimal.valueOf(200.00), roomsList.get(0).getTotalPrice().getAmount());
    assertEquals("EUR", roomsList.get(0).getTotalPrice().getCurrency());
  }

  @Test
  void mapRoomFromResultSetToRoomWithMultiRoomsTest() {

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    List<Room> roomsList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRoomFromResultSetToRoom("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(2, roomsList.size());
    assertEquals(BigDecimal.valueOf(200.00), roomsList.get(0).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(150.00), roomsList.get(1).getTotalPrice().getAmount());
    assertEquals("SB", roomsList.get(0).getType());
    assertEquals("DB", roomsList.get(1).getType());
  }

  @Test
  void mapRoomFromResultSetToRoomWithMultiRoomsEuroCurrTest() {
    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setCurrency("E");
    hotelAvailabilitiesResultSet1.setCurrency("E");
    hotelAvailabilitiesResultSet2.setCurrency("E");
    hotelAvailabilitiesResultSet3.setCurrency("E");
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    List<Room> roomsList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRoomFromResultSetToRoom("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(2, roomsList.size());
    assertEquals(BigDecimal.valueOf(200.00), roomsList.get(0).getTotalPrice().getAmount());
    assertEquals("EUR", roomsList.get(0).getTotalPrice().getCurrency());
    assertEquals(BigDecimal.valueOf(150.00), roomsList.get(1).getTotalPrice().getAmount());
    assertEquals("EUR", roomsList.get(1).getTotalPrice().getCurrency());
    assertEquals("SB", roomsList.get(0).getType());
    assertEquals("DB", roomsList.get(1).getType());

  }

  @Test
  void mapRateFromResultSetToRatePlanEmptyAvailabilitiesTest() {
    List<RatePlan> ratePlanList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRateFromResultSetToRatePlan("PLYPTI", searchCriteria, Collections.emptyList());
    assertEquals(Collections.emptyList(), ratePlanList);
  }

  @Test
  void mapRateFromResultSetToRatePlanSuccessTest() {
    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);
    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);

    List<RatePlan> ratePlanList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRateFromResultSetToRatePlan("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(1, ratePlanList.size());
    assertEquals(2, ratePlanList.get(0).getRooms().size());
    assertEquals(BigDecimal.valueOf(200.00), ratePlanList.get(0).getRooms().get(0).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(150.00), ratePlanList.get(0).getRooms().get(1).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(350.00), ratePlanList.get(0).getTotalPrice().getAmount());
  }

  @Test
  void mapRateFromResultSetToRatePlanSuccessEuroCurrTest() {
    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setCurrency("E");
    hotelAvailabilitiesResultSet1.setCurrency("E");
    hotelAvailabilitiesResultSet2.setCurrency("E");
    hotelAvailabilitiesResultSet3.setCurrency("E");
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);
    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);
    List<RatePlan> ratePlanList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRateFromResultSetToRatePlan("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(1, ratePlanList.size());
    assertEquals(2, ratePlanList.get(0).getRooms().size());
    assertEquals(BigDecimal.valueOf(200.00), ratePlanList.get(0).getRooms().get(0).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(150.00), ratePlanList.get(0).getRooms().get(1).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(350.00), ratePlanList.get(0).getTotalPrice().getAmount());
    assertEquals("EUR", ratePlanList.get(0).getTotalPrice().getCurrency());
  }

  @Test
  void mapRateFromResultSetToRatePlanSuccessWithMultiRatePlanTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet5);
    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);

    List<RatePlan> ratePlanList = hotelAvailabilitiesPersistencePostProcessorPort.
        mapRateFromResultSetToRatePlan("PLYPTI", searchCriteria, hotelAvailList);
    assertEquals(2, ratePlanList.size());
    assertEquals(1, ratePlanList.get(0).getRooms().size());
    assertEquals(BigDecimal.valueOf(200.00), ratePlanList.get(0).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(100.00), ratePlanList.get(1).getTotalPrice().getAmount());
  }

  @Test
  void processHotelResultSetToHotelEmptyHotelAvailabilitiesTest() {
    assertEquals(Collections.emptyList(), hotelAvailabilitiesPersistencePostProcessorPort
        .processHotelResultSetToHotel(searchCriteria, Collections.emptyList()));
  }

  @Test
  void performPostProcessHotelAvailabilitiesTestWithEmptyList() {
    assertEquals(Collections.emptyList(),
        hotelAvailabilitiesPersistencePostProcessorPort.performPostProcessHotelAvailabilities(
            searchCriteria, Collections.emptyList()));
  }

  @Test
  void performPostProcessHotelAvailabilitiesTest() {
    HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPostProcessorPortSpy
        = Mockito.spy(hotelAvailabilitiesPersistencePostProcessorPort);
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet5);
    Mockito.doReturn(hotelsList).when(hotelAvailabilitiesPostProcessorPortSpy).
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    when(losRestrictionPort.applyLosRestrictions(searchCriteria, hotelsList)).thenReturn(hotelsList);

    hotelAvailabilitiesPostProcessorPortSpy.performPostProcessHotelAvailabilities(
        searchCriteria, hotelAvailList);
    verify(losRestrictionPort, times(1)).
        applyLosRestrictions(searchCriteria, hotelsList);
    verify(hotelAvailabilitiesPostProcessorPortSpy
        , times(1))
        .processHotelResultSetToHotel(searchCriteria, hotelAvailList);
  }

  @Test
  void processHotelResultSetToHotelSuccessTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    hotelAvailList.add(hotelAvailabilitiesResultSet4);
    hotelAvailList.add(hotelAvailabilitiesResultSet5);
    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);
    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    assertEquals(5, hotelList.size());
    assertEquals(2, hotelList.get(0).getRates().size());
    assertEquals(BigDecimal.valueOf(200.00), hotelList.get(0).getRates().get(0).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(100.00), hotelList.get(0).getRates().get(1).getTotalPrice().getAmount());
  }

  @Test
  void processHotelResultSetToHotelSuccessWithMultiHotelResultsTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);

    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    List<Hotel> hotelList1 = hotelList.stream().filter(h -> h.getHotelCode().equals("PLYPTI"))
        .collect(Collectors.toList());
    Hotel hotel1 = hotelList1.get(0);

    hotelList1 = hotelList.stream().filter(h -> h.getHotelCode().equals("PLYLOC")).toList();
    Hotel hotel2 = hotelList1.get(0);

    assertEquals(5, hotelList.size());
    assertEquals(BigDecimal.valueOf(200.00), hotel1.getRates().get(0).getTotalPrice().getAmount());
    assertEquals(BigDecimal.valueOf(140.00), hotel2.getRates().get(0).getTotalPrice().getAmount());
  }

  @Test
  void isQuantityLessThanRoomsRequestedReturnTrueTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setQuantity(1);
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.SB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    assertTrue(hotelAvailabilitiesPersistencePostProcessorPort
        .isQuantityLessThanRoomsRequested(hotelAvailList, Long.valueOf(2), "SB"));
  }

  @Test
  void isQuantityLessThanRoomsRequestedReturnFalseTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet.setQuantity(2);
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.SB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    assertFalse(hotelAvailabilitiesPersistencePostProcessorPort
        .isQuantityLessThanRoomsRequested(hotelAvailList, Long.valueOf(2), "SB"));
  }

  @Test
  void populateLimitedAvailabilityFalseTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    when(hotelRatesPostProcessorPort.isAllDaysRatesReturned(any(), any(), any(), any())).thenReturn(true);
    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    assertEquals(5, hotelList.size());
    assertFalse(hotelList.get(0).getLimitedAvailability());
    assertTrue(hotelList.get(0).getAvailable());
  }

  @Test
  void populateLimitedAvailabilityTrueTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();

    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    assertEquals(5, hotelList.size());
    assertTrue(hotelList.get(0).getLimitedAvailability());
    assertTrue(hotelList.get(1).getLimitedAvailability());
    assertFalse(hotelList.get(1).getAvailable());
    assertFalse(hotelList.get(0).getEuroCurrencyHotel());
    assertFalse(hotelList.get(1).getEuroCurrencyHotel());
  }

  @Test
  void populateLimitedAvailabilityEuroCurrencyTest() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet6.setCurrency("E");
    hotelAvailabilitiesResultSet7.setCurrency("E");
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    assertEquals(5, hotelList.size());
    assertTrue(hotelList.get(0).getEuroCurrencyHotel());
  }

  @Test
  void populateLimitedAvailabilityEuroCurrency1Test() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailabilitiesResultSet6.setCurrency("E");
    hotelAvailList.add(hotelAvailabilitiesResultSet6);
    hotelAvailList.add(hotelAvailabilitiesResultSet7);

    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    assertEquals(5, hotelList.size());
    assertTrue(hotelList.get(0).getEuroCurrencyHotel());
  }

  @Test
  void populateLimitedAvailabilityWithEmptyRateTest() {
    searchCriteria.setAdults(new int[]{2});
    searchCriteria.setRooms(2);
    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.SB.name()});
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet8);

    List<Hotel> hotelList = hotelAvailabilitiesPersistencePostProcessorPort.
        processHotelResultSetToHotel(searchCriteria, hotelAvailList);
    assertEquals(5, hotelList.size());
    assertTrue(hotelList.get(0).getLimitedAvailability());
  }

  private SearchCriteria buildSearchCriteria() {
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(false)
        .rooms(1)
        .type(new String[]{RoomType.SB.name()})
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
