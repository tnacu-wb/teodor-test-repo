package uk.co.whitbread.availabilitycacheservice.domain.logic.hotelprice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
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
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.LocationPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.LocationPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;

@ExtendWith(MockitoExtension.class)

class LocationPriceServiceTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();

  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();

  @Mock
  private LocationPricePersistencePort locationPricePersistencePort;

  private LocationPricePort locationPricePort;

  private List<Hotel> expectedBestPricedHotels;

  @BeforeEach
  void setup() {
    locationPricePort = new LocationPriceService(locationPricePersistencePort);

    Hotel hotel1 = getBuildHotel("PLYPTI", 50.0);
    Hotel hotel2 = getBuildHotel("PLYLOC", 32.0);
    Hotel hotel3 = getBuildHotel("PLYMAR", 87.0);
    Hotel hotel4 = getBuildHotel("LISBAR", 45.0);

    expectedBestPricedHotels = Arrays.asList(hotel1, hotel2, hotel3, hotel4);
  }

  @Test
  void shouldReturnBestPricedHotels() {

    LocationPriceRequest hotelPriceRequest = buildLocationPriceRequest();

    when(locationPricePersistencePort.findBestPricePerLocation(hotelPriceRequest))
        .thenReturn(expectedBestPricedHotels);

    List<Hotel> actualBestPricedHotels = locationPricePort.getBestPricedHotels(hotelPriceRequest);

    verify(locationPricePersistencePort).findBestPricePerLocation(hotelPriceRequest);
    assertThat(actualBestPricedHotels).hasSameElementsAs(expectedBestPricedHotels);
  }

  @Test
  void shouldThrowErrorForBestPricedHotels() {

    LocationPriceRequest hotelPriceRequest = buildLocationPriceRequest();

    doThrow(new RuntimeException()).when(locationPricePersistencePort).findBestPricePerLocation(hotelPriceRequest);

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
        locationPricePort.getBestPricedHotels(hotelPriceRequest));

    verify(locationPricePersistencePort).findBestPricePerLocation(hotelPriceRequest);
  }


  private LocationPriceRequest buildLocationPriceRequest() {
    return LocationPriceRequest.builder()
        .startDate(ARRIVAL)
        .endDate(DEPARTURE)
        .altPriceThreshold(new BigDecimal("87.0"))
        .priceThreshold(new BigDecimal("87.0"))
        .build();
  }

  private Hotel getBuildHotel(final String plypti, final double v) {
    return Hotel.builder().hotelCode(plypti).hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(v), "G"))).build();
  }

}
