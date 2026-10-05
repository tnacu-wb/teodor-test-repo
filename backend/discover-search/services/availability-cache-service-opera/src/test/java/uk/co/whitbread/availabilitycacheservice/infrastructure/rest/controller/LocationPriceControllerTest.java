package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mocks.HotelDtoMock;
import mocks.HotelMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.LocationPricePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPriceResponse;

@ExtendWith(MockitoExtension.class)

class LocationPriceControllerTest {

  private static final LocalDate TODAY = LocalDate.now();
  private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

  private static final String ARRIVAL = LocalDate.parse(TODAY.toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(TOMORROW.toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();


  private LocationPriceController controller;
  private List<HotelDto> hotelDtoList;
  private List<Hotel> hotelList;

  private LocationPriceRequest locationPriceRequest;

  @Mock
  private LocationPricePort locationPricePort;

  @BeforeEach
  void setup() {
    controller = new LocationPriceController(locationPricePort);

    hotelDtoList = HotelDtoMock.buildAllHotelDtos();
    hotelList = HotelMock.buildAllHotels();
  }

  @Test
  void shouldReturnHotelPricesSuccessfully() {

    locationPriceRequest = buildLocationPriceRequest(ARRIVAL, DEPARTURE);

    final List<Hotel> hotels = buildHotels();
    when(locationPricePort.getBestPricedHotels(locationPriceRequest)).thenReturn(hotels);

    final ResponseEntity<LocationPriceResponse> response = controller.getBestPricedHotels(locationPriceRequest);

    LocationPriceResponse locationPriceResponse = response.getBody();
    assertThat(locationPriceResponse.getBestPricedHotels()).isNotEmpty();
    assertThat(locationPriceResponse.getBestPricedHotels())
        .extracting(LocationPrice::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void shouldThrowErrorHotelPricePortThrowsError() {

    locationPriceRequest = buildLocationPriceRequest(ARRIVAL, DEPARTURE);

    when(locationPricePort.getBestPricedHotels(locationPriceRequest)).thenThrow(new RuntimeException("Some error!"));

    assertThrows(RuntimeException.class, () -> controller.getBestPricedHotels(locationPriceRequest));
  }

  @Test
  void shouldReturnHotelPricesEmptyWith200Response() {

    locationPriceRequest = buildLocationPriceRequest(ARRIVAL, DEPARTURE);

    when(locationPricePort.getBestPricedHotels(locationPriceRequest)).thenReturn(Collections.emptyList());

    final ResponseEntity<LocationPriceResponse> response = controller.getBestPricedHotels(locationPriceRequest);

    LocationPriceResponse locationPriceResponse = response.getBody();
    assertThat(locationPriceResponse.getBestPricedHotels()).isEmpty();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private LocationPriceRequest buildLocationPriceRequest(
      final String startDate, final String endDate) {

    return LocationPriceRequest.builder()
        .startDate(startDate)
        .endDate(endDate)
        .priceThreshold(new BigDecimal("30.00"))
        .altPriceThreshold(new BigDecimal("25.00"))
        .build();
  }

  private List<Hotel> buildHotels() {

    Hotel hotel1 = Hotel.builder().hotelCode("PLYPTI").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(50.0), "G"))).build();
    Hotel hotel2 = Hotel.builder().hotelCode("PLYLOC").hotelBrand("PI").
        bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(32.0), "G"))).build();
    Hotel hotel3 = Hotel.builder().hotelCode("PLYMAR").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(87.0), "G"))).build();
    Hotel hotel4 = Hotel.builder().hotelCode("LISBAR").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(45.0), "G"))).build();
    return Arrays.asList(hotel1, hotel2, hotel3, hotel4);
  }


}
