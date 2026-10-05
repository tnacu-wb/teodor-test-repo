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
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.HotelPricePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.BestPricedHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendar;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendarResponse;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceResponseDto;

@ExtendWith(MockitoExtension.class)

class HotelPriceControllerTest {

  private static final LocalDate TODAY = LocalDate.now();
  private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

  private static final String ARRIVAL = LocalDate.parse(TODAY.toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(TOMORROW.toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();

  private static final String ROOM_TYPE = "DB";
  private static final String HOTEL_CODE = "PLYPTI";
  @Mock
  HotelPricePort hotelPricePort;
  private HotelPriceController controller;
  private List<HotelDto> hotelDtoList;
  private List<Hotel> hotelList;
  private HotelPriceRequest hotelPriceRequest;
  private HotelPriceCalendarRequest hotelPriceCalendarRequest;

  @BeforeEach
  void setup() {
    controller = new HotelPriceController(hotelPricePort);
    hotelDtoList = HotelDtoMock.buildAllHotelDtos();
    hotelList = HotelMock.buildAllHotels();
  }

  @Test
  void shouldReturnHotelPricesSuccessfully() {

    hotelPriceRequest = buildHotelPriceRequest(ARRIVAL, DEPARTURE, ROOM_TYPE);

    final List<Hotel> hotels = buildHotels();
    when(hotelPricePort.getBestPricedHotels(hotelPriceRequest)).thenReturn(hotels);

    final ResponseEntity<HotelPriceResponseDto> response = controller.getHotelsWithPrices(hotelPriceRequest);

    HotelPriceResponseDto hotelPriceResponseDto = response.getBody();
    assertThat(hotelPriceResponseDto.getBestPricedHotels()).isNotEmpty();
    assertThat(hotelPriceResponseDto.getBestPricedHotels())
        .extracting(BestPricedHotelDto::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void shouldThrowErrorHotelPricePortThrowsError() {

    hotelPriceRequest = buildHotelPriceRequest(ARRIVAL, DEPARTURE, ROOM_TYPE);

    when(hotelPricePort.getBestPricedHotels(hotelPriceRequest)).thenThrow(new RuntimeException("Some error!"));

    assertThrows(RuntimeException.class, () -> controller.getHotelsWithPrices(hotelPriceRequest));
  }

  @Test
  void shouldReturnHotelPricesForCalendarSuccessfully() {

    hotelPriceCalendarRequest = buildHotelPriceCalendarRequest(ARRIVAL, DEPARTURE, ROOM_TYPE);

    final List<Hotel> hotels = buildHotelsForCalendar(HOTEL_CODE);
    when(hotelPricePort
        .getBestPricedHotelsForGivenHotelCode(HOTEL_CODE, hotelPriceCalendarRequest))
        .thenReturn(hotels);

    final ResponseEntity<HotelPriceCalendarResponse> response =
        controller.getHotelPriceDetails(HOTEL_CODE, hotelPriceCalendarRequest);

    HotelPriceCalendarResponse hotelPriceCalendarResponse = response.getBody();
    assertThat(hotelPriceCalendarResponse.getBestPricedHotels()).isNotEmpty();
    assertThat(hotelPriceCalendarResponse.getBestPricedHotels())
        .extracting(HotelPriceCalendar::getPrice)
        .containsExactly(new BigDecimal("50"), new BigDecimal("32"));

    assertThat(hotelPriceCalendarResponse.getBestPricedHotels())
        .extracting(HotelPriceCalendar::getDate)
        .containsExactly(ARRIVAL, DEPARTURE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void shouldThrowErrorHotelPricePortThrowsErrorForCalendarPrice() {

    hotelPriceCalendarRequest = buildHotelPriceCalendarRequest(ARRIVAL, DEPARTURE, ROOM_TYPE);

    when(hotelPricePort
        .getBestPricedHotelsForGivenHotelCode(HOTEL_CODE, hotelPriceCalendarRequest))
        .thenThrow(new RuntimeException("Some error!"));

    assertThrows(RuntimeException.class, () -> controller.getHotelPriceDetails(HOTEL_CODE, hotelPriceCalendarRequest));
  }

  @Test
  void emptyResponseWith200ResponseCodeForHotelPriceTest() {
    hotelPriceRequest = buildHotelPriceRequest(ARRIVAL, DEPARTURE, ROOM_TYPE);

    final List<Hotel> hotels = buildHotels();
    when(hotelPricePort.getBestPricedHotels(hotelPriceRequest)).thenReturn(Collections.emptyList());

    final ResponseEntity<HotelPriceResponseDto> response = controller.getHotelsWithPrices(hotelPriceRequest);

    HotelPriceResponseDto hotelPriceResponseDto = response.getBody();
    assertThat(hotelPriceResponseDto.getBestPricedHotels()).isEmpty();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void shouldReturnHotelPricesForCalendarSuccessfullyWithEmptyResponse() {

    hotelPriceCalendarRequest = buildHotelPriceCalendarRequest(ARRIVAL, DEPARTURE, ROOM_TYPE);

    when(hotelPricePort
        .getBestPricedHotelsForGivenHotelCode(HOTEL_CODE, hotelPriceCalendarRequest))
        .thenReturn(Collections.emptyList());

    final ResponseEntity<HotelPriceCalendarResponse> response =
        controller.getHotelPriceDetails(HOTEL_CODE, hotelPriceCalendarRequest);

    HotelPriceCalendarResponse hotelPriceCalendarResponse = response.getBody();
    assertThat(hotelPriceCalendarResponse.getBestPricedHotels()).isEmpty();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private HotelPriceRequest buildHotelPriceRequest(
      final String arrival, final String departure, final String roomType) {

    return HotelPriceRequest.builder()
        .arrival(arrival)
        .departure(departure)
        .roomType(roomType)
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR"))
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

  private List<Hotel> buildHotelsForCalendar(final String hotelCode) {

    Hotel hotel1 = Hotel.builder().hotelCode(hotelCode).hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(50.0), "G")))
        .date(ARRIVAL)
        .build();
    Hotel hotel2 = Hotel.builder().hotelCode(hotelCode).hotelBrand("PI").
        bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(32.0), "G")))
        .date(DEPARTURE)
        .build();
    return Arrays.asList(hotel1, hotel2);
  }

  private HotelPriceCalendarRequest buildHotelPriceCalendarRequest(
      final String arrival, final String departure, final String roomType) {

    return HotelPriceCalendarRequest.builder()
        .arrival(arrival)
        .departure(departure)
        .roomType(roomType)
        .build();
  }


}
