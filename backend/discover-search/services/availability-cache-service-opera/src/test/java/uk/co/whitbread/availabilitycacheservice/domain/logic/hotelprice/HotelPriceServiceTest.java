package uk.co.whitbread.availabilitycacheservice.domain.logic.hotelprice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.HotelPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;

@ExtendWith(MockitoExtension.class)

class HotelPriceServiceTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();

  @Mock
  private HotelPricePersistencePort hotelPricePersistencePort;
  @Mock
  private ContentClientLookUpService contentClientLookUpService;

  private HotelPricePort hotelPricePort;

  private List<Hotel> expectedBestPricedHotels;
  private List<Hotel> expectedBestPricedHotelsForCalendar;

  @BeforeEach
  void setup() {
    hotelPricePort = new HotelPriceService(hotelPricePersistencePort, contentClientLookUpService);
    Hotel hotel1 = Hotel.builder().hotelCode("PLYPTI").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("50.0"), "G"))).build();
    Hotel hotel2 = Hotel.builder().hotelCode("PLYLOC").hotelBrand("PI").
        bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("32.0"), "G"))).build();
    Hotel hotel3 = Hotel.builder().hotelCode("PLYMAR").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("87.0"), "G"))).build();
    Hotel hotel4 = Hotel.builder().hotelCode("LISBAR").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("45.0"), "G"))).build();

    Hotel hotel5 = Hotel.builder().hotelCode("PLYPTI").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("32.0"), "G"))).build();
    expectedBestPricedHotels = Arrays.asList(hotel1, hotel2, hotel3, hotel4);
    expectedBestPricedHotelsForCalendar = Arrays.asList(hotel1, hotel5);
  }

  @Test
  void shouldReturnBestPricedHotels() {
    HotelPriceRequest hotelPriceRequest = buildHotelPriceRequest();
    when(hotelPricePersistencePort.getBestPriceForHotels(hotelPriceRequest)).thenReturn(expectedBestPricedHotels);
    List<Hotel> actualBestPricedHotels = hotelPricePort.getBestPricedHotels(hotelPriceRequest);

    verify(hotelPricePersistencePort).getBestPriceForHotels(hotelPriceRequest);
    assertThat(actualBestPricedHotels).hasSameElementsAs(expectedBestPricedHotels);
  }

  @Test
  void shouldThrowErrorForBestPricedHotels() {
    HotelPriceRequest hotelPriceRequest = buildHotelPriceRequest();

    doThrow(new RuntimeException()).when(hotelPricePersistencePort).getBestPriceForHotels(hotelPriceRequest);

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(
        () -> hotelPricePort.getBestPricedHotels(hotelPriceRequest));
    verify(hotelPricePersistencePort).getBestPriceForHotels(hotelPriceRequest);
  }

  //start calendar
  @Test
  void shouldReturnBestPricedHotelsForGivenHotelCode() {
    final String hotelCode = "PLYPTI";
    final HotelPriceCalendarRequest hotelPriceCalendarRequest = buildHotelPriceCalendarRequest();
    when(hotelPricePersistencePort.getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest))
        .thenReturn(expectedBestPricedHotelsForCalendar);
    List<Hotel> actualBestPricedHotels = hotelPricePort.getBestPricedHotelsForGivenHotelCode(hotelCode,
        hotelPriceCalendarRequest);

    verify(hotelPricePersistencePort).getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);
    assertThat(actualBestPricedHotels).hasSameElementsAs(expectedBestPricedHotelsForCalendar);
  }

  @Test
  void shouldThrowErrorForBestPricedHotelsForGivenHotelCode() {
    final String hotelCode = "PLYPTI";
    final HotelPriceCalendarRequest hotelPriceCalendarRequest = buildHotelPriceCalendarRequest();
    doThrow(new RuntimeException()).when(hotelPricePersistencePort)
        .getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> hotelPricePort
        .getBestPricedHotelsForGivenHotelCode(hotelCode, hotelPriceCalendarRequest));

    verify(hotelPricePersistencePort)
        .getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> hotelPricePort
        .getBestPricedHotelsForGivenHotelCode(hotelCode, hotelPriceCalendarRequest));
  }
  //end calendar


  private HotelPriceRequest buildHotelPriceRequest() {
    return HotelPriceRequest.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR"))
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .roomType("DB")
        .build();
  }

  private HotelPriceCalendarRequest buildHotelPriceCalendarRequest() {
    return HotelPriceCalendarRequest.builder()
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .roomType("DB")
        .build();
  }

}
