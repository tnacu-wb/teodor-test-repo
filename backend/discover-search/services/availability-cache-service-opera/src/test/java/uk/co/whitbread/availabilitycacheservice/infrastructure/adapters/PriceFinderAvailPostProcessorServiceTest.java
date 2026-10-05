package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.PriceFinderAvailPostProcessorService.CLOSED_TO_ARRIVAL;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.PriceFinderAvailPostProcessorService.CLOSED_TO_BOOK;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.LowestRate;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.AvailabilityProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PriceFinderAvailPostProcessorServiceTest {

  @Mock
  LosRestrictionPort losRestrictionPort;
  private PriceFinderAvailabilitiesPostProcessorPort port;
  @Mock
  private AvailabilityProperties properties;
  @Mock
  private ContentClientLookUpService contentClientLookUpService;
  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;

  @BeforeEach
  void setup() {
    port = new PriceFinderAvailPostProcessorService(losRestrictionPort, properties, contentClientLookUpService,
        cityTaxFeatureUtil);
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(true);
    when(contentClientLookUpService.getHotelsCityTaxInfo(any(), any(), any()))
        .thenReturn(
            HotelsCityTaxInfo.builder().hotelsCityTaxes(Map.of("LONKIN", HotelCityTax.builder().build())).build());
  }

  @Test
  void shouldReturnHotelsWithEmptyAvailability_whenResultSetIsEmpty() {

    final PriceFinderSearchCriteria priceFinderSearchCriteria =
        buildSearchCriteria();

    List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(Collections.emptyList(), priceFinderSearchCriteria);

    assertEquals(2, priceFinderOperaHotelAvailabilities.size());
    assertEquals(priceFinderSearchCriteria.getHotelCodes().get(0),
        priceFinderOperaHotelAvailabilities.get(0).getHotelCode());
    assertEquals(priceFinderSearchCriteria.getHotelCodes().get(1),
        priceFinderOperaHotelAvailabilities.get(1).getHotelCode());
  }

  @Test
  void performPriceFinderPostProcess_success() {

    final List<PriceFinderResultSet> resultSets = getPriceFinderResultSetsWithClosedRestrictions();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    assertNotNull(priceFinderOperaHotelAvailabilities);
    assertFalse(priceFinderOperaHotelAvailabilities.isEmpty());
    assertTrue(priceFinderOperaHotelAvailabilities.stream()
        .flatMap(result -> result.getAvailabilities().stream())
        .anyMatch(Availabilities::isHasClosedRestriction));
    assertFalse(priceFinderOperaHotelAvailabilities.stream()
        .flatMap(result -> result.getAvailabilities().stream())
        .allMatch(Availabilities::isHasClosedRestriction));
  }

  @Test
  void shouldReturnEmptyWhenLosRestrictionPresent() {
    final List<PriceFinderResultSet>
        resultSets = getPriceFinderResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    when(properties.getRatePlanClasses()).thenReturn(rates);
    when(losRestrictionPort.isLosApplicable(any(RatePlan.class), any(SearchCriteria.class))).thenReturn(true);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();
    assertTrue(availabilities.stream().anyMatch(Availabilities::isHasMlosRestriction));
    assertTrue(availabilities.stream()
        .allMatch(availability -> availability.getMinimumRate().equals(BigDecimal.valueOf(0.00))));
  }

  @Test
  void shouldReturnLowestPriceWhenMinNightsRequested() {
    final List<PriceFinderResultSet>
        resultSets = getPriceFinderResultSets();
    resultSets.removeIf(resultSet ->
        resultSet.getAvailableDate().equals( LocalDate.parse("2025-12-13")));

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    when(properties.getRatePlanClasses()).thenReturn(rates);
    when(losRestrictionPort.isLosApplicable(any(RatePlan.class), any(SearchCriteria.class))).thenReturn(true);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    priceFinderSearchCriteria.setShowMinimumNights(true);

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream()).toList();
    assertFalse(availabilities.stream().anyMatch(Availabilities::isHasMlosRestriction));
    assertTrue(availabilities.stream().anyMatch( availability -> availability.getMinimumNights() == 2));
    assertTrue(availabilities.stream()
        .anyMatch( availability -> availability.getMinimumRate().equals(BigDecimal.valueOf(0.00))));
  }

  @Test
  void shouldProcessMultiRoomSearchSuccessfully() {
    final List<PriceFinderResultSet> resultSets = getMultiRoomPriceFinderResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildMultiRoomSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    assertNotNull(priceFinderOperaHotelAvailabilities);
    assertFalse(priceFinderOperaHotelAvailabilities.isEmpty());

    // Verify that multi-room logic is applied
    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();

    // Should find rates that can satisfy the room requirement
    assertTrue(availabilities.stream().anyMatch(availability ->
        availability.getQuantity() >= priceFinderSearchCriteria.getRooms()));
  }

  @Test
  void shouldReturnEmptyAvailabilityWhenMultiRoomRequirementNotMet() {
    final List<PriceFinderResultSet> resultSets = getInsufficientQuantityResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildMultiRoomSearchCriteria();
    priceFinderSearchCriteria.setRooms(10); // Request more rooms than available

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();

    // Should return empty availability when room requirement cannot be met
    assertTrue(availabilities.stream().anyMatch(availability ->
        availability.getMinimumRate().equals(BigDecimal.valueOf(0.00))));
  }

  @Test
  void shouldFilterEmployeeRatePlansWhenEmployeeRateAvailable() {
    final List<PriceFinderResultSet> resultSets = getEmployeeRateResultSets();

    List<String> regularRates = Arrays.asList("A", "C", "S", "U", "W", "O");
    List<String> employeeRates = Arrays.asList("E", "F");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(regularRates);
    Mockito.when(properties.getEmployeeRatePlanClasses()).thenReturn(employeeRates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();

    // Should filter out employee rates when employee rate plan is available
    assertTrue(availabilities.stream().noneMatch(availability ->
        availability.getRateClassification().contains("E")));
  }

  @Test
  void shouldHandleEurCurrencyCorrectly() {
    final List<PriceFinderResultSet> resultSets = getEurCurrencyResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();

    // Should convert "E" currency to "EUR"
    assertTrue(availabilities.stream().anyMatch(availability ->
        "EUR".equals(availability.getCurrency())));
  }

  @Test
  void shouldAggregateRoomTypesAndQuantitiesCorrectly() {
    final List<PriceFinderResultSet> resultSets = getMultipleRoomTypesResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();

    // Should aggregate room types and quantities
    assertTrue(availabilities.stream().anyMatch(availability ->
        availability.getRoomType().contains(",") && availability.getQuantity() > 1));
  }

  @Test
  void shouldHandleEmptyRateClassifications() {
    final List<PriceFinderResultSet> resultSets = getEmptyRateClassificationResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    assertNotNull(priceFinderOperaHotelAvailabilities);
    assertFalse(priceFinderOperaHotelAvailabilities.isEmpty());
  }

  @Test
  void shouldProcessSingleRoomSearchWithMultipleRates() {
    final List<PriceFinderResultSet> resultSets = getMultipleRatesForSingleRoomResultSets();

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    priceFinderSearchCriteria.setRooms(1); // Single room search

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    List<Availabilities> availabilities = priceFinderOperaHotelAvailabilities.stream()
        .flatMap(availabilitiesSet -> availabilitiesSet.getAvailabilities().stream())
        .toList();

    // Should find the minimum rate among all available rates
    assertTrue(availabilities.stream().anyMatch(availability ->
        availability.getMinimumRate().compareTo(BigDecimal.valueOf(30.00)) <= 0));
  }

  private PriceFinderSearchCriteria buildSearchCriteria() {

    return PriceFinderSearchCriteria.builder()
        .hotelCodes(Arrays.asList("LONKIN", "LONEUS"))
        .arrival("2025-12-12")
        .departure("2025-12-16")
        .country("GB")
        .language("EN")
        .build();
  }

  private PriceFinderSearchCriteria buildMultiRoomSearchCriteria() {
    return PriceFinderSearchCriteria.builder()
        .hotelCodes(Arrays.asList("LONKIN", "LONEUS"))
        .arrival("2025-12-12")
        .departure("2025-12-16")
        .country("GB")
        .language("EN")
        .rooms(3)
        .build();
  }

  private PriceFinderResultSet buildPriceFinderResultSet(String hotelCode, LocalDate availableDate,
      String currency, BigDecimal amount, int minNight, int maxNight, String rateClass, int quantity) {

    return PriceFinderResultSet.builder()
        .rateCode("FLEXRATE")
        .hotelCode(hotelCode)
        .availableDate(availableDate)
        .currency(currency)
        .minimumRate(amount)
        .minNights(minNight)
        .maxNights(maxNight)
        .quantity(quantity)
        .rateClassification(rateClass)
        .build();
  }

  private PriceFinderResultSet buildPriceFinderResultSetWithRoomType(String hotelCode, LocalDate availableDate,
      String currency, BigDecimal amount, int minNight, int maxNight, String rateClass, int quantity, String roomType) {

    return PriceFinderResultSet.builder()
        .rateCode("FLEXRATE")
        .hotelCode(hotelCode)
        .availableDate(availableDate)
        .currency(currency)
        .minimumRate(amount)
        .minNights(minNight)
        .maxNights(maxNight)
        .quantity(quantity)
        .rateClassification(rateClass)
        .roomType(roomType)
        .build();
  }

  @Test
  void performPriceFinderPostProcess_withCityTaxFromOcd() {

    final List<PriceFinderResultSet> resultSets = getPriceFinderResultSetsForCityTax();
    resultSets.forEach(resultSet -> resultSet.setMinimumRateWithCityTax(BigDecimal.valueOf(42.00)));

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    priceFinderSearchCriteria.setArrival(LocalDate.now().plusDays(1).toString());
    priceFinderSearchCriteria.setDeparture(LocalDate.now().plusDays(5).toString());

    // Set effectiveFrom and bookingDateFrom to ensure city tax is effective
    String effectiveFrom = LocalDate.now().minusDays(2).toString();
    String bookingDateFrom = LocalDate.now().minusDays(1).toString();
    HotelCityTax cityTax = HotelCityTax.builder()
        .effectiveFrom(effectiveFrom)
        .bookingDateFrom(bookingDateFrom)
        .build();

    when(contentClientLookUpService.getHotelsCityTaxInfo(any(), any(), any()))
        .thenReturn(
            HotelsCityTaxInfo.builder().hotelsWithCityTax(List.of("LONKIN"))
                .hotelsCityTaxes(Map.of("LONKIN", cityTax)).build());
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(false);

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    assertNotNull(priceFinderOperaHotelAvailabilities);
    assertFalse(priceFinderOperaHotelAvailabilities.isEmpty());
    assertFalse(priceFinderOperaHotelAvailabilities.get(0).getAvailabilities().isEmpty());
    assertEquals(BigDecimal.valueOf(42.00),
        priceFinderOperaHotelAvailabilities.get(0).getAvailabilities().stream().findFirst().get().getMinimumRate());
  }

  @Test
  void performPriceFinderPostProcess_withCityTaxCalculation() {

    final List<PriceFinderResultSet> resultSets = getPriceFinderResultSetsForCityTax();
    resultSets.forEach(resultSet -> resultSet.setMinimumRateWithCityTax(BigDecimal.valueOf(42.00)));

    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    when(properties.getRatePlanClasses()).thenReturn(rates);

    final PriceFinderSearchCriteria priceFinderSearchCriteria = buildSearchCriteria();
    priceFinderSearchCriteria.setArrival(LocalDate.now().plusDays(1).toString());
    priceFinderSearchCriteria.setDeparture(LocalDate.now().plusDays(5).toString());

    String effectiveFrom = LocalDate.now().minusDays(2).toString();
    String bookingDateFrom = LocalDate.now().minusDays(1).toString();
    HotelCityTax cityTax = HotelCityTax.builder()
        .amount(BigDecimal.valueOf(2.00))
        .vat(BigDecimal.valueOf(10.00))
        .effectiveFrom(effectiveFrom)
        .bookingDateFrom(bookingDateFrom)
        .build();

    when(contentClientLookUpService.getHotelsCityTaxInfo(any(), any(), any()))
        .thenReturn(
            HotelsCityTaxInfo.builder().hotelsWithCityTax(List.of("LONKIN"))
                .hotelsCityTaxes(Map.of("LONKIN", cityTax)).build());

    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities =
        port.processHotelResultSet(resultSets, priceFinderSearchCriteria);

    assertNotNull(priceFinderOperaHotelAvailabilities);
    assertFalse(priceFinderOperaHotelAvailabilities.isEmpty());
    assertFalse(priceFinderOperaHotelAvailabilities.get(0).getAvailabilities().isEmpty());
    assertEquals(BigDecimal.valueOf(42.2).setScale(2, RoundingMode.HALF_EVEN),
        priceFinderOperaHotelAvailabilities.get(0).getAvailabilities().stream().findFirst().get().getMinimumRate());
  }

  @Test
  void processHotelResultSetForCalendar_ok() {
    List<String> rates = Arrays.asList("A");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    List<PriceFinderResultSet> priceFinderResultSets = buildPriceFinderResultSetList();
    PriceFinderSearchCriteria priceFinderSearchCriteria = new PriceFinderSearchCriteria();
    List<String> hotelCodes = new ArrayList<>() {{
      add("LONKIN");
      add("GATGAT");
    }};
    priceFinderSearchCriteria.setHotelCodes(hotelCodes);
    priceFinderSearchCriteria.setArrival("2025-10-10");
    priceFinderSearchCriteria.setDeparture("2025-10-11");

    CalendarPriceFinderOperaHotelAvailabilities expected =
        buildExpectedCalendarPriceFinderOperaHotelAvailabilities();

    CalendarPriceFinderOperaHotelAvailabilities result = port.processHotelResultSetForCalendar(
        priceFinderResultSets, priceFinderSearchCriteria
    );

    assertEquals(result, expected);
  }

  @Test
  void processHotelResultSetForCalendar_notOk() {
    List<String> rates = Arrays.asList("U");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    List<PriceFinderResultSet> priceFinderResultSets = buildPriceFinderResultSetList();
    PriceFinderSearchCriteria priceFinderSearchCriteria = new PriceFinderSearchCriteria();
    List<String> hotelCodes = new ArrayList<>() {{
      add("LONKIN");
      add("GATGAT");
    }};
    priceFinderSearchCriteria.setHotelCodes(hotelCodes);
    priceFinderSearchCriteria.setArrival("2025-10-10");
    priceFinderSearchCriteria.setDeparture("2025-10-11");

    CalendarPriceFinderOperaHotelAvailabilities expected =
        buildExpectedCalendarPriceFinderOperaHotelAvailabilities();

    CalendarPriceFinderOperaHotelAvailabilities result = port.processHotelResultSetForCalendar(
        priceFinderResultSets, priceFinderSearchCriteria
    );

    assertEquals(result, expected);
  }

  @Test
  void finalPrice_isAlwaysCorrectlyCalculated() {
    // Arrange
    PriceFinderSearchCriteria criteria = buildMultiRoomSearchCriteria();
    int roomsRequested = criteria.getRooms();
    List<PriceFinderResultSet> resultSets = getMultiRoomPriceFinderResultSets();
    List<String> rates = Arrays.asList("A", "C", "S", "U", "W", "O");
    Mockito.when(properties.getRatePlanClasses()).thenReturn(rates);

    // Act
    List<PriceFinderOperaHotelAvailabilities> hotels = port.processHotelResultSet(resultSets, criteria);

    // Assert
    for (PriceFinderOperaHotelAvailabilities hotel : hotels) {
      for (Availabilities avail : hotel.getAvailabilities()) {
        if (avail.getMinimumRate().compareTo(BigDecimal.ZERO) > 0) {
          assertEquals(avail.getMinimumRate().multiply(BigDecimal.valueOf(roomsRequested)), avail.getFinalPrice(),
              "finalPrice should be minimumRate * roomsRequested");
        }
      }
    }
  }

  CalendarPriceFinderOperaHotelAvailabilities buildExpectedCalendarPriceFinderOperaHotelAvailabilities() {
    CalendarPriceFinderOperaHotelAvailabilities calendar =
        new CalendarPriceFinderOperaHotelAvailabilities();
    calendar.setCurrency("GBP");

    LowestRate lr1 = new LowestRate();
    lr1.setAvailableDate(LocalDate.parse("2025-10-10"));
    lr1.setMinimumRate(new BigDecimal(79));
    LowestRate lr2 = new LowestRate();
    lr2.setAvailableDate(LocalDate.parse("2025-10-11"));
    lr2.setMinimumRate(new BigDecimal(72));
    SortedSet<LowestRate> lowestRates = new TreeSet<>();
    lowestRates.add(lr1);
    lowestRates.add(lr2);
    calendar.setLowestRates(lowestRates);

    return calendar;
  }

  List<PriceFinderResultSet> buildPriceFinderResultSetList() {
    PriceFinderResultSet rs0 = new PriceFinderResultSet();
    rs0.setHotelCode("LONKIN");
    rs0.setAvailableDate(LocalDate.parse("2025-10-12"));
    rs0.setMinimumRate(new BigDecimal(98));
    rs0.setCurrency(null);
    rs0.setRateCode("FLEXRATE");
    rs0.setRateClassification("A");
    rs0.setRoomType("DOUBLE");
    rs0.setQuantity(2);

    PriceFinderResultSet rs1 = new PriceFinderResultSet();
    rs1.setHotelCode("LONKIN");
    rs1.setAvailableDate(LocalDate.parse("2025-10-10"));
    rs1.setMinimumRate(new BigDecimal(98));
    rs1.setCurrency("G");
    rs1.setRateCode("FLEXRATE");
    rs1.setRateClassification("A");
    rs1.setRoomType("DOUBLE");
    rs1.setQuantity(2);

    PriceFinderResultSet rs2 = new PriceFinderResultSet();
    rs2.setHotelCode("GATGAT");
    rs2.setAvailableDate(LocalDate.parse("2025-10-10"));
    rs2.setMinimumRate(new BigDecimal(79));
    rs2.setCurrency("G");
    rs1.setRateCode("FLEXRATE");
    rs2.setRateClassification("A");
    rs2.setRoomType("DOUBLE");
    rs2.setQuantity(2);

    PriceFinderResultSet rs3 = new PriceFinderResultSet();
    rs3.setHotelCode("GATGAT");
    rs3.setAvailableDate(LocalDate.parse("2025-10-11"));
    rs3.setMinimumRate(new BigDecimal(72));
    rs3.setCurrency("G");
    rs1.setRateCode("FLEXRATE");
    rs3.setRateClassification("A");
    rs3.setRoomType("DOUBLE");
    rs3.setQuantity(2);

    PriceFinderResultSet rs4 = new PriceFinderResultSet();
    rs4.setHotelCode("MARIANA");
    rs4.setAvailableDate(LocalDate.parse("2025-10-11"));
    rs4.setMinimumRate(new BigDecimal(84));
    rs4.setCurrency("G");
    rs1.setRateCode("FLEXRATE");
    rs4.setRateClassification("A");
    rs4.setRoomType("DOUBLE");
    rs4.setQuantity(2);

    PriceFinderResultSet rs5 = new PriceFinderResultSet();
    rs5.setHotelCode("LONKIN");
    rs5.setAvailableDate(LocalDate.parse("2025-10-10"));
    rs5.setMinimumRate(new BigDecimal(0));

    PriceFinderResultSet rs6 = new PriceFinderResultSet();
    rs6.setHotelCode("LONKIN");
    rs6.setAvailableDate(LocalDate.parse("2025-10-11"));
    rs6.setMinimumRate(new BigDecimal(0));

    PriceFinderResultSet rs7 = new PriceFinderResultSet();
    rs7.setHotelCode("LONKIN");
    rs7.setAvailableDate(LocalDate.parse("2025-10-13"));
    rs7.setMinimumRate(new BigDecimal(0));
    rs4.setCurrency("");

    List<PriceFinderResultSet> priceFinderResultSetList = new ArrayList<>();
    priceFinderResultSetList.add(rs0);
    priceFinderResultSetList.add(rs1);
    priceFinderResultSetList.add(rs2);
    priceFinderResultSetList.add(rs3);
    priceFinderResultSetList.add(rs4);
    priceFinderResultSetList.add(rs5);
    priceFinderResultSetList.add(rs6);
    priceFinderResultSetList.add(rs7);

    return priceFinderResultSetList;
  }

  private @NotNull List<PriceFinderResultSet> getPriceFinderResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("40.00"), 2, 0, "A", 5);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-13"), "GBP",
            new BigDecimal("40.00"), 2, 0, "A", 8);
    final PriceFinderResultSet priceFinderResultSet3 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-14"), "GBP",
            new BigDecimal("40.00"), 2, 0, "A", 10);
    final PriceFinderResultSet priceFinderResultSet4 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-15"), "GBP",
            new BigDecimal("40.00"), 2, 0, "A", 12);
    final PriceFinderResultSet priceFinderResultSet5 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-16"), "GBP",
            new BigDecimal("40.00"), 2, 0, "A", 14);
    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);
    resultSets.add(priceFinderResultSet3);
    resultSets.add(priceFinderResultSet4);
    resultSets.add(priceFinderResultSet5);
    return resultSets;
  }

  private List<PriceFinderResultSet> getPriceFinderResultSetsWithClosedRestrictions() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("40.00"), 0, 0, "B", 4);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-13"), "GBP",
            new BigDecimal("40.00"), 0, 0, "B", 5);
    final PriceFinderResultSet priceFinderResultSet3 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-14"), "GBP",
            new BigDecimal("40.00"), CLOSED_TO_BOOK, 0, "A", 7);
    final PriceFinderResultSet priceFinderResultSet4 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-15"), "GBP",
            new BigDecimal("40.00"), CLOSED_TO_ARRIVAL, 0, "A", 8);
    final PriceFinderResultSet priceFinderResultSet5 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-16"), "GBP",
            new BigDecimal("40.00"), 0, 0, "B", 10);

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);
    resultSets.add(priceFinderResultSet3);
    resultSets.add(priceFinderResultSet4);
    resultSets.add(priceFinderResultSet5);

    return resultSets;
  }

  private List<PriceFinderResultSet> getMultiRoomPriceFinderResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create multiple rates with different quantities for multi-room search
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("50.00"), 1, 0, "A", 2);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("50.00"), 1, 0, "A", 2);
    final PriceFinderResultSet priceFinderResultSet3 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("60.00"), 1, 0, "B", 1);
    final PriceFinderResultSet priceFinderResultSet4 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-13"), "GBP",
            new BigDecimal("45.00"), 1, 0, "A", 3);
    final PriceFinderResultSet priceFinderResultSet5 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-13"), "GBP",
            new BigDecimal("45.00"), 1, 0, "A", 2);

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);
    resultSets.add(priceFinderResultSet3);
    resultSets.add(priceFinderResultSet4);
    resultSets.add(priceFinderResultSet5);

    return resultSets;
  }

  private @NotNull List<PriceFinderResultSet> getPriceFinderResultSetsForCityTax() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.now().plusDays(1), "GBP",
            new BigDecimal("40.00"), 0, 0, "A", 5);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.now().plusDays(2), "GBP",
            new BigDecimal("40.00"), 0, 0, "A", 8);
    final PriceFinderResultSet priceFinderResultSet3 =
        buildPriceFinderResultSet("LONKIN", LocalDate.now().plusDays(3), "GBP",
            new BigDecimal("40.00"), 0, 0, "A", 10);
    final PriceFinderResultSet priceFinderResultSet4 =
        buildPriceFinderResultSet("LONKIN", LocalDate.now().plusDays(4), "GBP",
            new BigDecimal("40.00"), 0, 0, "A", 12);
    final PriceFinderResultSet priceFinderResultSet5 =
        buildPriceFinderResultSet("LONKIN", LocalDate.now().plusDays(5), "GBP",
            new BigDecimal("40.00"), 0, 0, "A", 14);
    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);
    resultSets.add(priceFinderResultSet3);
    resultSets.add(priceFinderResultSet4);
    resultSets.add(priceFinderResultSet5);
    return resultSets;
  }

  private List<PriceFinderResultSet> getInsufficientQuantityResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create rates with insufficient quantity for multi-room search
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("50.00"), 1, 0, "A", 1);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("60.00"), 1, 0, "B", 1);

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);

    return resultSets;
  }

  private List<PriceFinderResultSet> getEmployeeRateResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create rates with employee rate plan code
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("30.00"), 1, 0, "E", 2);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("40.00"), 1, 0, "A", 3);
    final PriceFinderResultSet priceFinderResultSet3 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("35.00"), 1, 0, "F", 1);

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);
    resultSets.add(priceFinderResultSet3);

    return resultSets;
  }

  private List<PriceFinderResultSet> getEurCurrencyResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create rates with EUR currency
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "E",
            new BigDecimal("45.00"), 1, 0, "A", 2);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-13"), "GBP",
            new BigDecimal("40.00"), 1, 0, "A", 3);

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);

    return resultSets;
  }

  private List<PriceFinderResultSet> getMultipleRoomTypesResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create rates with different room types
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSetWithRoomType("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("50.00"), 1, 0, "A", 2, "STANDARD");
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSetWithRoomType("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("50.00"), 1, 0, "A", 1, "DELUXE");

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);

    return resultSets;
  }

  private List<PriceFinderResultSet> getEmptyRateClassificationResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create rates with empty rate classification
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("40.00"), 1, 0, "", 2);

    resultSets.add(priceFinderResultSet1);

    return resultSets;
  }

  private List<PriceFinderResultSet> getMultipleRatesForSingleRoomResultSets() {
    final List<PriceFinderResultSet> resultSets = new ArrayList<>();

    // Create multiple rates with different prices for single room search
    final PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("50.00"), 1, 0, "A", 1);
    final PriceFinderResultSet priceFinderResultSet2 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("30.00"), 1, 0, "B", 1);
    final PriceFinderResultSet priceFinderResultSet3 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP",
            new BigDecimal("45.00"), 1, 0, "C", 1);

    resultSets.add(priceFinderResultSet1);
    resultSets.add(priceFinderResultSet2);
    resultSets.add(priceFinderResultSet3);

    return resultSets;
  }

}
