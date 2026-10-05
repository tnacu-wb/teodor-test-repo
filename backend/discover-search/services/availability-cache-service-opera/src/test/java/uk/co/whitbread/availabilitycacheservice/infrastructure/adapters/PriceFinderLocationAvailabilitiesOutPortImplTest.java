package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderRate;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.PriceFinderProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;

@ExtendWith(MockitoExtension.class)
class PriceFinderLocationAvailabilitiesOutPortImplTest {

  @Mock
  private PriceFinderAvailabilitiesPersistencePort persistencePort;
  @Mock
  private SnowdropHotelLookupPort snowdropHotelLookupPort;
  @Mock
  private PriceFinderAvailabilitiesPostProcessorPort postProcessorPort;
  @Mock
  private PriceFinderProperties priceFinderProperties;
  @Mock
  private RulesAgentInPort rulesAgentInPort;

  private PriceFinderLocationAvailabilitiesOutPortImpl priceFinderLocationPort;

  @BeforeEach
  void setup() {
    priceFinderLocationPort =
        new PriceFinderLocationAvailabilitiesOutPortImpl(persistencePort, snowdropHotelLookupPort,
            postProcessorPort, priceFinderProperties, rulesAgentInPort);
  }

  @Test
  void getAvailabilitiesByLocation() {
    // Given
    final List<HotelDetails> hotels = getSnowdropHotels();
    // Mock the location ID and criteria
    final String locationId = "ChIJdd4hrwug2EcRmSrV3Vo6llI";
    final PriceFinderLocationSearchCriteria criteria = PriceFinderLocationSearchCriteria.builder()
        .locationId(locationId)
        .arrival("2025-10-01")
        .daysRange(7)
        .build();
    final List<PriceFinderOperaHotelAvailabilities> expectedAvails = getPriceFinderHotelAvailabilities();
    final List<PriceFinderResultSet> priceFinderResultSets = getPriceFinderResultSet();
    // When
    when(priceFinderProperties.getLocationRadiusInMiles()).thenReturn(30);
    when(snowdropHotelLookupPort.getHotelsFromLocation(locationId,
        30))
        .thenReturn(hotels);
    when(persistencePort.getLowestPricesByHotels(any())).thenReturn(priceFinderResultSets);
    when(postProcessorPort
        .processHotelResultSet(eq(priceFinderResultSets), any())).thenReturn(expectedAvails);

    // Then
    List<PriceFinderOperaHotelAvailabilities> result =
        priceFinderLocationPort.getAvailabilitiesByLocation(criteria,
            LocalDate.parse("2023-10-08"));

    assertEquals(expectedAvails.size(), result.size());
  }

  @Test
  void getAvailabilitiesByLocationForCalendar() {
    // Given
    final List<HotelDetails> hotels = getSnowdropHotels();
    // Mock the location ID and criteria
    final String locationId = "ChIJdd4hrwug2EcRmSrV3Vo6llI";
    final CalendarPriceFinderLocationSearchCriteria criteria = CalendarPriceFinderLocationSearchCriteria.builder()
        .locationId(locationId)
        .month(10)
        .milesRadius(50)
        .build();
    final List<PriceFinderResultSet> priceFinderResultSets = getPriceFinderResultSet();
    // When
    when(snowdropHotelLookupPort.getHotelsFromLocation(locationId, 50)).thenReturn(hotels);
    when(persistencePort.getLowestPricesByHotels(any())).thenReturn(priceFinderResultSets);

    RoomSubstitutionRuleResponse roomSubstitutionRuleResponse = getSubstitutionResponse();
    when(rulesAgentInPort.getRoomSubstitutionRule(any())).thenReturn(roomSubstitutionRuleResponse);


    when(postProcessorPort.processHotelResultSetForCalendar(any(), any()))
        .thenReturn(new CalendarPriceFinderOperaHotelAvailabilities());

    // Then
    CalendarPriceFinderOperaHotelAvailabilities result =
        priceFinderLocationPort.getAvailabilitiesByLocationForCalendar(criteria);

    assertEquals(criteria.getLocationId(), result.getLocationId());
    assertEquals(criteria.getMonth(), result.getMonth().intValue());
    assertEquals(criteria.getMilesRadius(), result.getMilesRadius().intValue());
  }

  private RoomSubstitutionRuleResponse getSubstitutionResponse() {
    return RoomSubstitutionRuleResponse.builder()
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .cotRequired(false)
            .roomType("DB")
            .adults(1)
            .children(0)
            .build())
        .substitutionList(buildSubstitutionListV2())
        .build();
  }

  private List<RoomSubstitution> buildSubstitutionListV2() {
    return List.of(RoomSubstitution.builder()
        .type("DOUBLE")
        .build());
  }

  @Test
  void getLowestMonthlyRateByLocation_returnsLowestRate() {
    // Given
    final List<HotelDetails> hotels = getSnowdropHotels();
    final String locationId = "ChIJdd4hrwug2EcRmSrV3Vo6llI";
    final PriceFinderLocationSearchCriteria criteria = PriceFinderLocationSearchCriteria.builder()
        .locationId(locationId)
        .arrival("2025-10-01")
        .daysRange(7)
        .build();
    final List<PriceFinderOperaHotelAvailabilities> expectedAvails = getPriceFinderHotelAvailabilities();
    final List<PriceFinderResultSet> priceFinderResultSets = getPriceFinderResultSet();

    when(priceFinderProperties.getLocationRadiusInMiles()).thenReturn(30);
    when(snowdropHotelLookupPort.getHotelsFromLocation(locationId, 30))
        .thenReturn(hotels);
    when(persistencePort.getLowestPricesByHotels(any())).thenReturn(priceFinderResultSets);
    when(postProcessorPort
        .processHotelResultSet(eq(priceFinderResultSets), any())).thenReturn(expectedAvails);

    // When
    PriceFinderRate result = priceFinderLocationPort.getLowestMonthlyRateByLocation(criteria);

    // Then
    assertEquals(new BigDecimal("100.00"), result.getPrice());
    assertEquals("GBP", result.getCurrency());
  }


  private static @NotNull List<PriceFinderOperaHotelAvailabilities> getPriceFinderHotelAvailabilities() {
    return List.of(
        PriceFinderOperaHotelAvailabilities.builder()
            .hotelCode("LONKIN").availabilities(Set.of(
                Availabilities.builder()
                    .availableDate("2025-10-01")
                    .minimumRate(new BigDecimal("100.00"))
                    .currency("GBP")
                    .build(),
                Availabilities.builder()
                    .availableDate("2025-10-02")
                    .minimumRate(new BigDecimal("110.00"))
                    .currency("GBP")
                    .build()))
            .build(),
        PriceFinderOperaHotelAvailabilities.builder()
            .hotelCode("LONEUS").availabilities(Set.of(
                Availabilities.builder()
                    .availableDate("2025-10-01")
                    .minimumRate(new BigDecimal("100.00"))
                    .currency("GBP")
                    .build(),
                Availabilities.builder()
                    .availableDate("2025-10-02")
                    .minimumRate(new BigDecimal("110.00"))
                    .currency("GBP")
                    .build()))
            .build());
  }

  private static @NotNull List<PriceFinderResultSet> getPriceFinderResultSet() {
    return List.of(
        PriceFinderResultSet.builder()
            .hotelCode("LONKIN")
            .availableDate(LocalDate.parse("2025-10-01"))
            .minimumRate(new BigDecimal("100.00"))
            .roomType("DB").build(),
        PriceFinderResultSet.builder()
            .hotelCode("LONKIN")
            .availableDate(LocalDate.parse("2025-10-02"))
            .minimumRate(new BigDecimal("110.00")).build(),
        PriceFinderResultSet.builder()
            .hotelCode("LONEUS")
            .availableDate(LocalDate.parse("2025-10-01"))
            .minimumRate(new BigDecimal("100.00")).build(),
        PriceFinderResultSet.builder()
            .hotelCode("LONEUS")
            .availableDate(LocalDate.parse("2025-10-02"))
            .minimumRate(new BigDecimal("110.00")).build());
  }

  private static @NotNull List<HotelDetails> getSnowdropHotels() {
    return List.of(HotelDetails.builder().code("LONEUS").name("loneus").build(),
        HotelDetails.builder().code("LONKIN").name("lonkin").build(),
        HotelDetails.builder().code("LONNOR").build(),
        HotelDetails.builder().code("LONLEI").name("lonei").build(),
        HotelDetails.builder().code("LONWES").build(),
        HotelDetails.builder().code("LONMAR").build());
  }

}
