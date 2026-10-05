package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mocks.HotelMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelRatesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;


@ExtendWith(SpringExtension.class)

public class HotelRatesPostProcessorSrvTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(2).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";

  private HotelRatesPostProcessorPort hotelRatesPostProcessorPort;

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

//  @Value("${low.inventory.threshold}")
//  private int lowInventoryThreshold = 5;

  @BeforeEach
  public void setup() {
    searchCriteria = buildSearchCriteria();
    hotelRatesPostProcessorPort = new HotelRatesPostProcessorService();

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
  public void TestIsAllDaysRatesReturned() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void TestIsAllDaysRatesReturnedReturnFalse() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertFalse(result);
  }

  @Test
  public void TestIsAllDaysRatesReturnedReturnTrueForMultiRoom() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnFalseForMultiRoom() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    //hotelAvailList.add(hotelAvailabilitiesResultSet3);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertFalse(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnTrueForMultiRoom() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.SB.name()});
    searchCriteria.setRooms(2);
    searchCriteria.setAdults(new int[]{1, 1});
    searchCriteria.setChildren(new int[]{0, 0});

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnTrueFor3Room() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(3);
    searchCriteria.setAdults(new int[]{1, 1, 1});
    searchCriteria.setChildren(new int[]{0, 0, 0});

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnFalseFor3Room() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);

    searchCriteria.setType(new String[]{RoomType.SB.name(), RoomType.SB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(3);
    searchCriteria.setAdults(new int[]{1, 1, 1});
    searchCriteria.setChildren(new int[]{0, 0, 0});

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertFalse(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnTrueFor4Room() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet1);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);
    hotelAvailList.add(hotelAvailabilitiesResultSet3);

    searchCriteria.setType(
        new String[]{RoomType.SB.name(), RoomType.SB.name(), RoomType.DB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(4);
    searchCriteria.setAdults(new int[]{1, 1, 1, 1});
    searchCriteria.setChildren(new int[]{0, 0, 0, 0});

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnTrueFor2RoomSingleDay() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);
    hotelAvailList.add(hotelAvailabilitiesResultSet2);

    searchCriteria.setType(
        new String[]{RoomType.SB.name(), RoomType.SB.name(), RoomType.DB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(4);
    searchCriteria.setAdults(new int[]{1, 1, 1, 1});
    searchCriteria.setChildren(new int[]{0, 0, 0, 0});
    searchCriteria.setDeparture(
        LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnFlaseFor2RoomSingleDay() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);

    searchCriteria.setType(
        new String[]{RoomType.SB.name(), RoomType.SB.name(), RoomType.DB.name(), RoomType.DB.name()});
    searchCriteria.setRooms(4);
    searchCriteria.setAdults(new int[]{1, 1, 1, 1});
    searchCriteria.setChildren(new int[]{0, 0, 0, 0});
    searchCriteria.setDeparture(
        LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertFalse(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnTrueFor1RoomSingleDay() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();
    hotelAvailList.add(hotelAvailabilitiesResultSet);

    searchCriteria.setDeparture(
        LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());

    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertTrue(result);
  }

  @Test
  public void testIsAllDaysRatesReturnedReturnFalseForEmptyRates() {
    List<HotelAvailabilitiesResultSet> hotelAvailList = new ArrayList<>();

    searchCriteria.setDeparture(
        LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());
    final boolean result = hotelRatesPostProcessorPort.isAllDaysRatesReturned(searchCriteria, hotelAvailList, "A",
        "PLYPTI");
    assertFalse(result);
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
