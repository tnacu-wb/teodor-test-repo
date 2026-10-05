package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.DatabaseProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.LocationBestPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelLocationJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;

@ExtendWith(MockitoExtension.class)
class LocationPricePersistenceAdapterTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  @Mock
  private JdbcTemplate jdbcTemplate;
  @Mock
  private DatabaseProperties databaseProperties;
  @Mock
  private HotelLocationJpaRepository hotelLocationJpaRepository;
  private LocationPricePersistenceAdapter locationPricePersistenceAdapter;
  private List<LocationBestPrice> expectedBestPricedHotels;

  @BeforeEach
  void setup() {

    locationPricePersistenceAdapter =
        new LocationPricePersistenceAdapter(jdbcTemplate, databaseProperties, hotelLocationJpaRepository);

    LocationBestPrice hotel1 = buildLocationBestPrice("PLYPTI", "placeId1", new BigDecimal(50.0), "G");
    LocationBestPrice hotel2 = buildLocationBestPrice("PLYLOC", "placeId2", new BigDecimal(32.0), "G");
    LocationBestPrice hotel3 = buildLocationBestPrice("PLYMAR", "placeId3", new BigDecimal(87.0), "G");
    LocationBestPrice hotel4 = buildLocationBestPrice("LISBAR", "placeId4", new BigDecimal(45.0), "G");
    expectedBestPricedHotels = Arrays.asList(hotel1, hotel2, hotel3, hotel4);

  }

  @Test
  void shouldGetBestPricedHotels() {
    Hotel hotel1 = buildHotel("PLYPTI", "placeId1", 50.0);
    Hotel hotel2 = buildHotel("PLYLOC", "placeId2", 32.0);
    Hotel hotel3 = buildHotel("PLYMAR", "placeId3", 87.0);
    Hotel hotel4 = buildHotel("LISBAR", "placeId4", 45.0);

    List<Hotel> expectedHotels = Arrays.asList(hotel1, hotel2, hotel3, hotel4);

    LocationPriceRequest request = buildLocationPriceRequest(new BigDecimal("87.0"), new BigDecimal("87.0"));
    when(hotelLocationJpaRepository.findBestPricedHotelForLocations(
        toLocalDate(request.getStartDate()), toLocalDate(request.getEndDate()), request.getAltPriceThreshold()))
        .thenReturn(expectedBestPricedHotels);

    List<Hotel> actualBestPricedHotels = locationPricePersistenceAdapter.findBestPricePerLocation(request);

    verify(hotelLocationJpaRepository)
        .findBestPricedHotelForLocations(
            toLocalDate(request.getStartDate()), toLocalDate(request.getEndDate()), request.getAltPriceThreshold()
        );
    assertThat(actualBestPricedHotels).hasSameElementsAs(expectedHotels);
  }

  private Hotel buildHotel(String plypti, String placeId1, double v) {
    return Hotel.builder().hotelCode(plypti).placeId(placeId1)
        .bestRoomPrice(new BestRoomPrice("", new Price(new BigDecimal(v), "G"))).build();
  }

  @Test
  void shouldThrowErrorWhenRetrievingBestPrices() {
    LocationPriceRequest request = buildLocationPriceRequest(new BigDecimal("87.0"), new BigDecimal("87.0"));

    doThrow(new RuntimeException()).when(hotelLocationJpaRepository).findBestPricedHotelForLocations(
        toLocalDate(request.getStartDate()), toLocalDate(request.getEndDate()), request.getAltPriceThreshold());

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
        locationPricePersistenceAdapter.findBestPricePerLocation(request));

    verify(hotelLocationJpaRepository)
        .findBestPricedHotelForLocations(
            toLocalDate(request.getStartDate()), toLocalDate(request.getEndDate()), request.getAltPriceThreshold()
        );
  }

  @Test
  void errorIfDateFormatIsNotCorrect() {

    assertThrows(ResponseStatusException.class, () -> locationPricePersistenceAdapter.findBestPricePerLocation(null));

    verify(hotelLocationJpaRepository, never()).findBestPricedHotelForLocations(null, null, null);
  }


  private LocationPriceRequest buildLocationPriceRequest(final BigDecimal altPriceThreshold,
      final BigDecimal priceThreshold) {
    return LocationPriceRequest.builder()
        .startDate(ARRIVAL)
        .endDate(DEPARTURE)
        .altPriceThreshold(altPriceThreshold)
        .priceThreshold(priceThreshold)
        .build();
  }

  private LocalDate toLocalDate(String date) {
    return LocalDate.parse(date);
  }

  private LocationBestPrice buildLocationBestPrice(final String hotelCode, final String placeId,
      final BigDecimal price, String currency) {
    return LocationBestPrice.builder()
        .hotelCode(hotelCode)
        .placeId(placeId)
        .price(price)
        .currency(currency)
        .build();
  }
}
