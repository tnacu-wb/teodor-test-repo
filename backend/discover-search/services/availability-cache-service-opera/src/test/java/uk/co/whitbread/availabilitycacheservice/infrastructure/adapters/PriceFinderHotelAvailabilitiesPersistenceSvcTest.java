package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.PriceFinderHotelAvailabilitiesRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;

@ExtendWith(MockitoExtension.class)
class PriceFinderHotelAvailabilitiesPersistenceSvcTest {

  @Mock
  private PriceFinderHotelAvailabilitiesRepository priceFinderHotelAvailabilitiesRepository;

  private PriceFinderAvailabilitiesPersistencePort persistencePort;

  @BeforeEach
  void setup() {
    persistencePort = new PriceFinderHotelAvailabilitiesPersistenceSvc(
        priceFinderHotelAvailabilitiesRepository);
  }

  @Test
  void getLowestpriceByHotel_success() {

    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("LONKIN");

    List<PriceFinderResultSet> priceFinderResultSets = new ArrayList<>();

    PriceFinderResultSet priceFinderResultSet1 =
        buildPriceFinderResultSet("LONKIN", LocalDate.parse("2025-12-12"), "GBP", new BigDecimal(40.00));
    priceFinderResultSets.add(priceFinderResultSet1);

    Mockito.when(priceFinderHotelAvailabilitiesRepository
            .findAvailabilitiesForPriceFinder(hotelCodes, LocalDate.parse("2025-12-12"), LocalDate.parse("2025-12-16")))
        .thenReturn(priceFinderResultSets);

    final List<PriceFinderResultSet> hotelsActual =
        persistencePort.getLowestPricesByHotels(buildSearchCriteria());

    assertNotNull(hotelsActual);
    assertFalse(hotelsActual.isEmpty());
  }

  @Test
  void hotelWithOnSaleFalseShouldReturnEmptyListTest() {

    PriceFinderSearchCriteria searchCriteria = buildSearchCriteria();

    final List<PriceFinderResultSet> hotelsActual =
        persistencePort.getLowestPricesByHotels(searchCriteria);

    assertTrue(hotelsActual.isEmpty());
  }

  private PriceFinderSearchCriteria buildSearchCriteria() {

    return PriceFinderSearchCriteria.builder()
        .hotelCodes(List.of("LONKIN"))
        .arrival("2025-12-12")
        .departure("2025-12-16")
        .country("GB")
        .language("EN")
        .build();
  }

  private PriceFinderResultSet buildPriceFinderResultSet(String hotelCode, LocalDate availableDate,
      String currency, BigDecimal amount) {

    return PriceFinderResultSet.builder()
        .hotelCode(hotelCode)
        .availableDate(availableDate)
        .currency(currency)
        .minimumRate(amount)
        .build();
  }

  private static @NotNull Set<Availabilities> getAvailabilities() {
    Set<Availabilities> availabilitiesList = new HashSet<>();
    Availabilities
        availabilities = new Availabilities();
    availabilities.setAvailableDate("2025-03-12");
    availabilities.setCurrency("GBP");
    availabilities.setMinimumRate(new BigDecimal("40.00"));

    availabilitiesList.add(availabilities);
    return availabilitiesList;
  }
}
