package uk.co.whitbread.availabilitycacheservice.domain.logic.pricefinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.availabilitycacheservice.domain.logic.PriceFinderHotelAvailabilitiesService;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.LowestRate;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderLocationAvailabilititesOutPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.RulesAgentClientWebFlux;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SortingOption;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

@ExtendWith(MockitoExtension.class)
class PriceFinderHotelAvailabilitiesServiceTest {

  @Mock
  private PriceFinderAvailabilitiesPersistencePort persistencePort;

  @Mock
  private PriceFinderLocationAvailabilititesOutPort priceFinderLocationAvailabilititesOutPort;
  @Mock
  private PriceFinderAvailabilitiesPostProcessorPort postProcessorPort;

  @Mock
  private RulesAgentClientWebFlux rulesAgentClientWebFlux;

  private PriceFinderHotelAvailabilitiesService service;

  @BeforeEach
  void setup() {
    service = new PriceFinderHotelAvailabilitiesService(persistencePort, priceFinderLocationAvailabilititesOutPort,
        postProcessorPort, rulesAgentClientWebFlux);
  }

  @Test
  void getLowestPricesByLocationWithRoomTypeSubstitution_success() {
    // Arrange
    final PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    final List<String> filterByRoomType = List.of("FAM", "DB");

    List<PriceFinderOperaHotelAvailabilities> initialAvailabilities = new ArrayList<>();
    initialAvailabilities.add(createHotelAvailability("HOTEL1", "Hotel One", "DBL,TWIN"));
    initialAvailabilities.add(createHotelAvailability("HOTEL2", "Hotel Two", "SGL,DBL"));
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        initialAvailabilities);

    when(rulesAgentClientWebFlux.getRoomSubstitutions("FAM", 2, 1)).thenReturn(
        Mono.just(Map.of("substitution-list", List.of(Map.of("type", "FMTRPL"), Map.of("type", "FMQUAD")))));
    when(rulesAgentClientWebFlux.getRoomSubstitutions("DB", 2, 0)).thenReturn(
        Mono.just(Map.of("substitution-list", List.of(Map.of("type", "DBL"), Map.of("type", "DBLDBL")))));

    PriceFinderResultSet resultSet1 = new PriceFinderResultSet();
    resultSet1.setHotelCode("HOTEL1");
    resultSet1.setRoomType("DBL,TWIN");

    PriceFinderResultSet resultSet2 = new PriceFinderResultSet();
    resultSet2.setHotelCode("HOTEL2");
    resultSet2.setRoomType("SGL,DBL");

    when(persistencePort.getLowestPricesByHotels(any(PriceFinderSearchCriteria.class))).thenReturn(
        List.of(resultSet1, resultSet2));

    PriceFinderOperaHotelAvailabilities processedHotel1 = createHotelAvailability("HOTEL1", null, "DBL");
    PriceFinderOperaHotelAvailabilities processedHotel2 = createHotelAvailability("HOTEL2", null, "DBL");
    when(postProcessorPort.processHotelResultSet(any(), any(), any())).thenReturn(
        List.of(processedHotel1, processedHotel2));

    // Act
    PriceFinderHotelAvailabilities result =
        service.getLowestPricesByLocationWithRoomTypeSubstitution(criteria, filterByRoomType);

    // Assert
    assertEquals(2, result.getTotal());
    assertEquals(2, result.getPageSize());

    PriceFinderOperaHotelAvailabilities resultHotel1 = result.getPriceFinderOperaHotelAvailabilitiesDtoList().get(0);
    assertEquals("HOTEL1", resultHotel1.getHotelCode());
    assertEquals("Hotel One", resultHotel1.getHotelName());
    assertEquals("DB", resultHotel1.getAvailabilities().iterator().next().getRoomCategory());

    PriceFinderOperaHotelAvailabilities resultHotel2 = result.getPriceFinderOperaHotelAvailabilitiesDtoList().get(1);
    assertEquals("HOTEL2", resultHotel2.getHotelCode());
    assertEquals("Hotel Two", resultHotel2.getHotelName());
    assertEquals("DB", resultHotel2.getAvailabilities().iterator().next().getRoomCategory());
  }

  @Test
  void getLowestPricesByLocationWithRoomTypeSubstitution_roomCategoryPrecedence() {
    // Arrange
    final PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    final List<String> filterByRoomType = List.of("FAM", "DB");

    List<PriceFinderOperaHotelAvailabilities> initialAvailabilities = new ArrayList<>();
    initialAvailabilities.add(createHotelAvailability("HOTEL1", "Hotel One", "FMTRPL,DBL"));
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        initialAvailabilities);

    when(rulesAgentClientWebFlux.getRoomSubstitutions("FAM", 2, 1)).thenReturn(
        Mono.just(Map.of("substitution-list", List.of(Map.of("type", "FMTRPL")))));
    when(rulesAgentClientWebFlux.getRoomSubstitutions("DB", 2, 0)).thenReturn(
        Mono.just(Map.of("substitution-list", List.of(Map.of("type", "DBL")))));

    PriceFinderResultSet resultSet = new PriceFinderResultSet();
    resultSet.setHotelCode("HOTEL1");
    resultSet.setRoomType("FMTRPL,DBL");
    when(persistencePort.getLowestPricesByHotels(any(PriceFinderSearchCriteria.class))).thenReturn(List.of(resultSet));

    PriceFinderOperaHotelAvailabilities processedHotel = createHotelAvailability("HOTEL1", null, "FMTRPL,DBL");
    when(postProcessorPort.processHotelResultSet(any(), any(), any())).thenReturn(List.of(processedHotel));

    // Act
    PriceFinderHotelAvailabilities result =
        service.getLowestPricesByLocationWithRoomTypeSubstitution(criteria, filterByRoomType);

    // Assert
    assertEquals(1, result.getTotal());
    PriceFinderOperaHotelAvailabilities resultHotel = result.getPriceFinderOperaHotelAvailabilitiesDtoList().get(0);
    assertEquals("DB", resultHotel.getAvailabilities().iterator().next().getRoomCategory());
  }


  @Test
  void getLowestPricesByLocationWithRoomTypeSubstitution_rulesAgentReturnsEmpty() {
    // Arrange
    PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    List<String> filterByRoomType = List.of("DB");
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        List.of(createHotelAvailability("HOTEL1", "Hotel One", "SGL")));
    when(rulesAgentClientWebFlux.getRoomSubstitutions(anyString(), anyInt(), anyInt())).thenReturn(
        Mono.empty()); // Simulate empty response
    when(persistencePort.getLowestPricesByHotels(any(PriceFinderSearchCriteria.class))).thenReturn(
        Collections.emptyList());
    when(postProcessorPort.processHotelResultSet(any(), any(), any())).thenReturn(Collections.emptyList());

    // Act
    PriceFinderHotelAvailabilities result =
        service.getLowestPricesByLocationWithRoomTypeSubstitution(criteria, filterByRoomType);

    // Assert
    assertEquals(0, result.getTotal());
    assertTrue(result.getPriceFinderOperaHotelAvailabilitiesDtoList().isEmpty());
  }

  @Test
  void getLowestPricesByLocationWithRoomTypeSubstitution_rulesAgentReturnsError() {
    // Arrange
    PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    List<String> filterByRoomType = List.of("TWIN");
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        List.of(createHotelAvailability("HOTEL1", "Hotel One", "DBL")));
    when(rulesAgentClientWebFlux.getRoomSubstitutions(anyString(), anyInt(), anyInt())).thenReturn(
        Mono.error(new RuntimeException("Rules Agent Down!"))); // Simulate error
    when(persistencePort.getLowestPricesByHotels(any(PriceFinderSearchCriteria.class))).thenReturn(
        Collections.emptyList());
    when(postProcessorPort.processHotelResultSet(any(), any(), any())).thenReturn(Collections.emptyList());

    // Act
    PriceFinderHotelAvailabilities result =
        service.getLowestPricesByLocationWithRoomTypeSubstitution(criteria, filterByRoomType);

    // Assert
    assertEquals(0, result.getTotal());
    assertTrue(result.getPriceFinderOperaHotelAvailabilitiesDtoList().isEmpty());
  }

  @Test
  void getLowestPricesByLocationWithRoomTypeSubstitution_persistenceReturnsEmpty() {
    // Arrange
    PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    List<String> filterByRoomType = List.of("SB");
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        List.of(createHotelAvailability("HOTEL1", "Hotel One", "DBL")));
    when(rulesAgentClientWebFlux.getRoomSubstitutions("SB", 1, 0)).thenReturn(
        Mono.just(Map.of("substitution-list", List.of(Map.of("type", "SGL")))));
    when(persistencePort.getLowestPricesByHotels(any(PriceFinderSearchCriteria.class))).thenReturn(
        Collections.emptyList()); // Simulate empty persistence result
    when(postProcessorPort.processHotelResultSet(any(), any(), any())).thenReturn(Collections.emptyList());

    // Act
    PriceFinderHotelAvailabilities result =
        service.getLowestPricesByLocationWithRoomTypeSubstitution(criteria, filterByRoomType);

    // Assert
    assertEquals(0, result.getTotal());
    assertTrue(result.getPriceFinderOperaHotelAvailabilitiesDtoList().isEmpty());
  }

  // --- Existing Tests ---

  @Test
  void getPriceFinderAvailabilities_ifArrivalDateIsToday() {

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    priceFinderSearchCriteria.setDeparture(String.valueOf(LocalDate.now().plusDays(14)));

    final List<PriceFinderOperaHotelAvailabilities> hotelAvailabilitiesListExpected =
        buildListOfOperaHotelAvailabilities();

    List<PriceFinderResultSet> resultSet = null;
    when(persistencePort.getLowestPricesByHotels(priceFinderSearchCriteria)).thenReturn(resultSet);

    when(postProcessorPort.processHotelResultSet(resultSet, priceFinderSearchCriteria)).thenReturn(
        hotelAvailabilitiesListExpected);

    final List<PriceFinderOperaHotelAvailabilities> hotelAvailabilitiesListActual =
        service.getLowestPricesByHotel(buildSearchCriteria());

    assertEquals(hotelAvailabilitiesListExpected, hotelAvailabilitiesListActual);
  }

  @Test
  void getPriceFinderAvailabilities_ifArrivalDateIsCloser() {

    LocalDate testArrivalDate = LocalDate.now().plusDays(5);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    priceFinderSearchCriteria.setArrival(String.valueOf(testArrivalDate));
    priceFinderSearchCriteria.setDeparture(String.valueOf(testArrivalDate.plusDays(14)));

    final List<PriceFinderOperaHotelAvailabilities> hotelAvailabilitiesListExpected =
        buildListOfOperaHotelAvailabilities();

    List<PriceFinderResultSet> resultSet = null;
    when(persistencePort.getLowestPricesByHotels(priceFinderSearchCriteria)).thenReturn(resultSet);

    when(postProcessorPort.processHotelResultSet(resultSet, priceFinderSearchCriteria)).thenReturn(
        hotelAvailabilitiesListExpected);

    final List<PriceFinderOperaHotelAvailabilities> hotelAvailabilitiesListActual =
        service.getLowestPricesByHotel(priceFinderSearchCriteria);

    assertEquals(hotelAvailabilitiesListExpected, hotelAvailabilitiesListActual);
  }

  @Test
  void shouldReturnEmptyIfPersistencePortIsReturningEmptyTest() {

    PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    when(persistencePort.getLowestPricesByHotels(priceFinderSearchCriteria)).thenReturn(Collections.emptyList());

    final List<PriceFinderOperaHotelAvailabilities> hotelAvailabilitiesListActual =
        service.getLowestPricesByHotel(priceFinderSearchCriteria);

    assertTrue(hotelAvailabilitiesListActual.isEmpty());

  }

  @Test
  void getPriceFinderAvailabilitiesByLocation_Success() {

    List<PriceFinderOperaHotelAvailabilities> byLocationAvailabilities = buildListOfOperaHotelAvailabilities();
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        byLocationAvailabilities);

    final PriceFinderHotelAvailabilities actualHotelAvailabilitiesResponse =
        service.getLowestPricesByLocation(buildLocationSearchCriteria());

    assertEquals(byLocationAvailabilities,
        actualHotelAvailabilitiesResponse.getPriceFinderOperaHotelAvailabilitiesDtoList());
  }

  @Test
  void getPriceFinderAvailabilitiesByLocation_noResults() {

    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        Collections.emptyList());

    final PriceFinderHotelAvailabilities actualHotelAvailabilitiesResponse =
        service.getLowestPricesByLocation(buildLocationSearchCriteria());

    assertTrue(actualHotelAvailabilitiesResponse.getPriceFinderOperaHotelAvailabilitiesDtoList().isEmpty());
    assertEquals(1, actualHotelAvailabilitiesResponse.getPage());
    assertEquals(0, actualHotelAvailabilitiesResponse.getPageSize());
    assertEquals(0, actualHotelAvailabilitiesResponse.getTotal());
  }

  @Test
  void getPriceFinderAvailabilitiesByLocation_returnsSecondPageHotels() {

    List<PriceFinderOperaHotelAvailabilities> byLocationAvailabilities = buildListOfOperaHotelAvailabilities();
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        byLocationAvailabilities);

    PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    criteria.setPage(2);
    criteria.setInitialPageSize(4);
    criteria.setLazyLoadPageSize(3);
    final PriceFinderHotelAvailabilities actualHotelAvailabilitiesResponse =
        service.getLowestPricesByLocation(criteria);

    assertEquals(2, actualHotelAvailabilitiesResponse.getPage());
    assertEquals(actualHotelAvailabilitiesResponse.getPriceFinderOperaHotelAvailabilitiesDtoList().size(),
        actualHotelAvailabilitiesResponse.getPageSize());
    assertEquals(byLocationAvailabilities.size(), actualHotelAvailabilitiesResponse.getTotal());
  }

  @Test
  void getPriceFinderAvailabilitiesByLocation_sortsByPriceWhenSortingOptionIsPrice() {
    // Arrange
    List<PriceFinderOperaHotelAvailabilities> byLocationAvailabilities = getHotelAvailabilities();
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocation(any(), any())).thenReturn(
        byLocationAvailabilities);

    PriceFinderLocationSearchCriteria criteria = buildLocationSearchCriteria();
    criteria.setSortBy(SortingOption.PRICE);

    // Act
    PriceFinderHotelAvailabilities response = service.getLowestPricesByLocation(criteria);
    List<PriceFinderOperaHotelAvailabilities> sortedHotels = response.getPriceFinderOperaHotelAvailabilitiesDtoList();

    // Assert
    assertEquals("HOTEL2", sortedHotels.get(0).getHotelCode());
    assertEquals("HOTEL1", sortedHotels.get(1).getHotelCode());
    assertEquals("HOTEL3", sortedHotels.get(2).getHotelCode());
  }

  @Test
  void getPriceFinderByLocationForCalendar_Success() {
    CalendarPriceFinderOperaHotelAvailabilities calendarPriceFinderOperaHotelAvailabilities =
        buildCalendarPriceFinderOperaHotelAvailabilities();
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocationForCalendar(any())).thenReturn(
        calendarPriceFinderOperaHotelAvailabilities);

    final CalendarPriceFinderHotelAvailabilities lowestPricesByLocationForCalendar =
        service.getLowestPricesByLocationForCalendar(buildLocationSearchCriteriaForCalendar());

    CalendarPriceFinderHotelAvailabilities expected = CalendarPriceFinderHotelAvailabilities.builder()
        .calendarPriceFinderOperaHotelAvailabilitiesDtoList(calendarPriceFinderOperaHotelAvailabilities).build();

    assertEquals(lowestPricesByLocationForCalendar, expected);
  }

  @Test
  void getPriceFinderByLocationForCalendar_noResult() {
    when(priceFinderLocationAvailabilititesOutPort.getAvailabilitiesByLocationForCalendar(any())).thenReturn(
        new CalendarPriceFinderOperaHotelAvailabilities());

    final CalendarPriceFinderHotelAvailabilities lowestPricesByLocationForCalendar =
        service.getLowestPricesByLocationForCalendar(buildLocationSearchCriteriaForCalendar());

    assertNotNull(lowestPricesByLocationForCalendar.getCalendarPriceFinderOperaHotelAvailabilitiesDtoList());
  }

  private CalendarPriceFinderLocationSearchCriteria buildLocationSearchCriteriaForCalendar() {
    return CalendarPriceFinderLocationSearchCriteria.builder().locationId("locationId").month(10).milesRadius(50)
        .build();
  }

  private CalendarPriceFinderOperaHotelAvailabilities buildCalendarPriceFinderOperaHotelAvailabilities() {
    final CalendarPriceFinderOperaHotelAvailabilities operaHotelAvailabilities =
        new CalendarPriceFinderOperaHotelAvailabilities();
    SortedSet<LowestRate> lowestRates = new TreeSet<>();
    LowestRate lowestRate = new LowestRate();

    lowestRate.setAvailableDate(LocalDate.parse("2025-08-12"));
    lowestRate.setMinimumRate(new BigDecimal("40.00"));

    lowestRates.add(lowestRate);

    lowestRates.add(new LowestRate() {{
      setAvailableDate(LocalDate.parse("2025-08-23"));
      setMinimumRate(new BigDecimal("50.00"));
    }});

    lowestRates.forEach(System.out::println);

    operaHotelAvailabilities.setLocationId("ChIJ6W3FzTRydkgRZ0H2Q1VT548");
    operaHotelAvailabilities.setMilesRadius(50);
    operaHotelAvailabilities.setMonth(10);
    operaHotelAvailabilities.setCurrency("GBP");
    operaHotelAvailabilities.setLowestRates(lowestRates);

    return operaHotelAvailabilities;
  }

  // --- Helper Methods ---

  private PriceFinderOperaHotelAvailabilities createHotelAvailability(String hotelCode, String hotelName,
                                                                      String roomTypes) {
    PriceFinderOperaHotelAvailabilities hotel = new PriceFinderOperaHotelAvailabilities();
    hotel.setHotelCode(hotelCode);
    hotel.setHotelName(hotelName);
    hotel.setDistanceFromSearchLocation(1.0);
    Availabilities avail = new Availabilities();
    avail.setRoomType(roomTypes);
    hotel.setAvailabilities(new HashSet<>(List.of(avail)));
    return hotel;
  }

  private static @NotNull List<PriceFinderOperaHotelAvailabilities> getHotelAvailabilities() {
    PriceFinderOperaHotelAvailabilities hotel1 = new PriceFinderOperaHotelAvailabilities();
    hotel1.setHotelCode("HOTEL1");
    Availabilities avail1 = new Availabilities();
    avail1.setAvailableDate("2025-08-12");
    avail1.setCurrency("GBP");
    avail1.setMinimumRate(new BigDecimal("120.00"));
    hotel1.setAvailabilities(new HashSet<>(List.of(avail1)));

    PriceFinderOperaHotelAvailabilities hotel2 = new PriceFinderOperaHotelAvailabilities();
    hotel2.setHotelCode("HOTEL2");
    Availabilities avail2 = new Availabilities();
    avail2.setAvailableDate("2025-08-12");
    avail2.setCurrency("GBP");
    avail2.setMinimumRate(new BigDecimal("90.00"));
    hotel2.setAvailabilities(new HashSet<>(List.of(avail2)));

    PriceFinderOperaHotelAvailabilities hotel3 = new PriceFinderOperaHotelAvailabilities();
    hotel3.setHotelCode("HOTEL3");
    Availabilities avail3 = new Availabilities();
    avail3.setAvailableDate("2025-08-12");
    avail3.setCurrency("GBP");
    avail3.setMinimumRate(new BigDecimal("150.00"));
    hotel3.setAvailabilities(new HashSet<>(List.of(avail3)));

    return List.of(hotel1, hotel2, hotel3);
  }

  private List<PriceFinderOperaHotelAvailabilities> buildListOfOperaHotelAvailabilities() {

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilitiesList = new ArrayList<>();
    final PriceFinderOperaHotelAvailabilities priceFinderOperaHotelAvailabilities =
        new PriceFinderOperaHotelAvailabilities();
    final Set<Availabilities> availabilitiesList = new HashSet<>();

    Availabilities availabilities = new Availabilities();
    availabilities.setAvailableDate(String.valueOf(LocalDate.now()));
    availabilities.setCurrency("GBP");
    availabilities.setMinimumRate(new BigDecimal("40.00"));

    availabilitiesList.add(availabilities);
    priceFinderOperaHotelAvailabilities.setHotelCode("LONKIN");
    priceFinderOperaHotelAvailabilities.setAvailabilities(availabilitiesList);

    priceFinderOperaHotelAvailabilitiesList.add(priceFinderOperaHotelAvailabilities);

    return priceFinderOperaHotelAvailabilitiesList;
  }


  private PriceFinderSearchCriteria buildSearchCriteria() {

    return PriceFinderSearchCriteria.builder().hotelCodes(Arrays.asList("LONKIN", "LONEUS"))
        .arrival(String.valueOf(LocalDate.now())).country("GB").language("EN").build();
  }

  private PriceFinderLocationSearchCriteria buildLocationSearchCriteria() {

    return PriceFinderLocationSearchCriteria.builder().daysRange(7).locationId("locationId").arrival("2025-08-12")
        .page(1).initialPageSize(15).lazyLoadPageSize(10).build();
  }
}
