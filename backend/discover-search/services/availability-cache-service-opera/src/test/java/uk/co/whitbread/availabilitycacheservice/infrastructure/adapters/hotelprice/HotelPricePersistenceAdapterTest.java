package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelToHotelMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.BestPricedHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.CalendarBestPriceHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelPricePersistenceAdapterTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static MockedStatic<BestPricedHotelToHotelMapper> mockedStatic;
  @Mock
  private HotelJpaRepository hotelJpaRepository;
  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;
  @Mock
  private ContentClientLookUpService contentClientLookUpService;
  private HotelPricePersistencePort hotelPricePersistencePort;
  private List<BestPricedHotel> expectedBestPricedHotels;
  //private List<BestPricedHotel> expectedBestPricedHotelsForCalendar;
  private List<CalendarBestPriceHotel> expectedBestPricedHotelsForCalendar;

  @BeforeAll
  static void createStaticMock() {
    mockedStatic = mockStatic(BestPricedHotelToHotelMapper.class);
  }

  @AfterAll
  static void closeStaticMock() {
    mockedStatic.close();
  }

  @BeforeEach
  void setup() {
    hotelPricePersistencePort = new HotelPricePersistenceAdapter(hotelJpaRepository, cityTaxFeatureUtil,
        contentClientLookUpService);
    BestPricedHotel hotel1 = BestPricedHotel.builder().hotelCode("PLYPTI").bestPrice(new BigDecimal(50.0)).currency("G")
        .priceWithCityTax(new BigDecimal(60.0))
        .build();
    BestPricedHotel hotel2 = BestPricedHotel.builder().hotelCode("PLYLOC").bestPrice(new BigDecimal(32.0)).currency("G")
        .priceWithCityTax(new BigDecimal(42.0))
        .build();
    BestPricedHotel hotel3 = BestPricedHotel.builder().hotelCode("PLYMAR").bestPrice(new BigDecimal(87.0)).currency("G")
        .priceWithCityTax(new BigDecimal(97.0))
        .build();
    BestPricedHotel hotel4 = BestPricedHotel.builder().hotelCode("LISBAR").bestPrice(new BigDecimal(45.0)).currency("G")
        .priceWithCityTax(new BigDecimal(55.0))
        .build();
    expectedBestPricedHotels = Arrays.asList(hotel1, hotel2, hotel3, hotel4);

    CalendarBestPriceHotel chotel1 = CalendarBestPriceHotel.builder().hotelCode("PLYPTI")
        .bestPrice(new BigDecimal(50.0)).currency("G").date(LocalDate.now().toString()).build();
    CalendarBestPriceHotel chotel2 = CalendarBestPriceHotel.builder().hotelCode("PLYLOC")
        .bestPrice(new BigDecimal(32.0)).currency("G").date(LocalDate.now().toString()).build();
    expectedBestPricedHotelsForCalendar = Arrays.asList(chotel1, chotel2);
  }

  @Test
  void shouldGetBestPricedHotels() {

    var expectedHotels = mockExpectedHotels();
    when(BestPricedHotelToHotelMapper.mapBestPricedHotelToHotel("DB", expectedBestPricedHotels)).thenReturn(
        expectedHotels);

    HotelPriceRequest hotelPriceRequest = buildHotelPriceRequest();
    when(hotelJpaRepository.findBestPriceForHotel(hotelPriceRequest.getHotelCodes(),
        LocalDate.parse(hotelPriceRequest.getArrival()),
        LocalDate.parse(hotelPriceRequest.getDeparture()), hotelPriceRequest.getRoomType())).thenReturn(
        expectedBestPricedHotels);

    List<Hotel> actualBestPricedHotels = hotelPricePersistencePort.getBestPriceForHotels(hotelPriceRequest);

    verify(hotelJpaRepository).findBestPriceForHotel(hotelPriceRequest.getHotelCodes(),
        LocalDate.parse(hotelPriceRequest.getArrival()),
        LocalDate.parse(hotelPriceRequest.getDeparture()), hotelPriceRequest.getRoomType());
    assertThat(actualBestPricedHotels).hasSameElementsAs(expectedHotels);
  }

  @Test
  void shouldThrowErrorWhenRetrievingBestPrices() {
    HotelPriceRequest hotelPriceRequest = buildHotelPriceRequest();
    doThrow(new RuntimeException()).when(hotelJpaRepository)
        .findBestPriceForHotel(hotelPriceRequest.getHotelCodes(), LocalDate.parse(hotelPriceRequest.getArrival()),
            LocalDate.parse(hotelPriceRequest.getDeparture()), hotelPriceRequest.getRoomType());

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(
        () -> hotelPricePersistencePort.getBestPriceForHotels(hotelPriceRequest));
    verify(hotelJpaRepository).findBestPriceForHotel(hotelPriceRequest.getHotelCodes(),
        LocalDate.parse(hotelPriceRequest.getArrival()),
        LocalDate.parse(hotelPriceRequest.getDeparture()), hotelPriceRequest.getRoomType());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void shouldGetBestPricedHotels_CityTaxEnabled(boolean isFallbackEnabled) {
    var expectedHotels = mockExpectedHotels();
    when(BestPricedHotelToHotelMapper.mapBestPricedHotelToHotel("DB", expectedBestPricedHotels))
        .thenReturn(expectedHotels);

    HotelPriceRequest hotelPriceRequest = buildHotelPriceRequest();
    when(hotelJpaRepository.findBestPriceForHotel(hotelPriceRequest.getHotelCodes(),
        LocalDate.parse(hotelPriceRequest.getArrival()),
        LocalDate.parse(hotelPriceRequest.getDeparture()), hotelPriceRequest.getRoomType()))
        .thenReturn(expectedBestPricedHotels);

    var hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);
    var hotelCityTax = mock(HotelCityTax.class);
    when(hotelsCityTaxInfo.getHotelsCityTaxes()).thenReturn(Map.of("PLYPTI", hotelCityTax, "PLYLOC", hotelCityTax));
    when(contentClientLookUpService.getHotelsCityTaxInfo("gb", "en", hotelPriceRequest.getHotelCodes()))
        .thenReturn(hotelsCityTaxInfo);

    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(isFallbackEnabled);

    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any()))
          .thenReturn(true);
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldCalculateCityTax(eq(isFallbackEnabled), any()))
          .thenReturn(!isFallbackEnabled);
      cityTaxUtilMock.when(() -> CityTaxUtil.getAmountWithCityTax(any(), any(), any(), anyInt(), anyInt()))
          .thenReturn(new BigDecimal("60.00"));

     var actualBestPricedHotels = hotelPricePersistencePort.getBestPriceForHotels(hotelPriceRequest);

      verify(hotelJpaRepository).findBestPriceForHotel(hotelPriceRequest.getHotelCodes(),
          LocalDate.parse(hotelPriceRequest.getArrival()),
          LocalDate.parse(hotelPriceRequest.getDeparture()), hotelPriceRequest.getRoomType());
      verify(contentClientLookUpService).getHotelsCityTaxInfo("gb", "en", hotelPriceRequest.getHotelCodes());
      assertThat(actualBestPricedHotels).hasSameElementsAs(expectedHotels);
    }
  }

  @Test
  void shouldReturnEmptyBestPricedHotels() {

    when(hotelJpaRepository.findBestPriceForHotel(null, null, null, null)).thenReturn(Collections.emptyList());

    when(BestPricedHotelToHotelMapper.mapBestPricedHotelToHotel("DB", Collections.emptyList())).thenReturn(
        Collections.emptyList());

    List<Hotel> actualBestPricedHotels = hotelPricePersistencePort.getBestPriceForHotels(null);

    verify(hotelJpaRepository).findBestPriceForHotel(null, null, null, null);
    assertThat(actualBestPricedHotels).hasSameElementsAs(Collections.emptyList());
  }

  //Start calendar

  @Test
  void shouldGetBestPricedHotel() {
    final String hotelCode = "PLYPTI";
    Hotel hotel1 = Hotel.builder().hotelCode(hotelCode).hotelBrand("PI").date(ARRIVAL)
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(50.0), "G"))).build();
    Hotel hotel2 = Hotel.builder().hotelCode(hotelCode).hotelBrand("PI").date(DEPARTURE).
        bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(32.0), "G"))).build();

    List<Hotel> expectedHotels = Arrays.asList(hotel1, hotel2);

    when(BestPricedHotelToHotelMapper.mapCalendarBestPricedHotelsToHotels("DB",
        expectedBestPricedHotelsForCalendar)).thenReturn(expectedHotels);

    final HotelPriceCalendarRequest hotelPriceCalendarRequest = buildHotelPriceCalendarRequest();
    when(hotelJpaRepository.findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
        LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType()))
        .thenReturn(expectedBestPricedHotelsForCalendar);

    List<Hotel> actualBestPricedHotels = hotelPricePersistencePort
        .getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);

    verify(hotelJpaRepository).findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
        LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType());
    assertThat(actualBestPricedHotels).hasSameElementsAs(expectedHotels);
  }

  @Test
  void shouldGetBestPricedHotel_WithCalculatedCityTax() {
    final String hotelCode = "PLYPTI";
    Hotel hotel1 = Hotel.builder().hotelCode(hotelCode).hotelBrand("PI").date(ARRIVAL)
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("50.0"), "G"))).build();

    List<Hotel> expectedHotels = Collections.singletonList(hotel1);
    var expectedAmountWithCityTax = new BigDecimal("55.00");

    when(BestPricedHotelToHotelMapper.mapCalendarBestPricedHotelsToHotels("DB",
        expectedBestPricedHotelsForCalendar)).thenReturn(expectedHotels);


    HotelCityTax hotelCityTax = mock(HotelCityTax.class);
    HotelsCityTaxInfo hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);
    when(hotelsCityTaxInfo.getHotelsCityTaxes()).thenReturn(Map.of(hotelCode, hotelCityTax));

    final HotelPriceCalendarRequest hotelPriceCalendarRequest = buildHotelPriceCalendarRequest();
    hotelPriceCalendarRequest.setHotelCityTaxInfo(hotelsCityTaxInfo);
    when(hotelJpaRepository.findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
        LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType()))
        .thenReturn(expectedBestPricedHotelsForCalendar);
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(false);


    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any()))
          .thenReturn(true);
      cityTaxUtilMock.when(() ->
          CityTaxUtil.shouldCalculateCityTax(eq(false), eq(null))).thenReturn(true);
      cityTaxUtilMock.when(() -> CityTaxUtil.getAmountWithCityTax(any(), any(), any(), anyInt(), anyInt()))
          .thenReturn(expectedAmountWithCityTax);

      List<Hotel> actualBestPricedHotels = hotelPricePersistencePort
          .getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);

      verify(hotelJpaRepository).findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
          LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType());
      assertThat(actualBestPricedHotels).hasSameElementsAs(expectedHotels);
    }
  }

  @Test
  void shouldGetBestPricedHotel_WithDBCityTax() {
    final String hotelCode = "PLYPTI";
    Hotel hotel1 = Hotel.builder().hotelCode(hotelCode).hotelBrand("PI").date(ARRIVAL)
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal("50.0"), "G"))).build();

    List<Hotel> expectedHotels = Collections.singletonList(hotel1);

    when(BestPricedHotelToHotelMapper.mapCalendarBestPricedHotelsToHotels("DB",
        expectedBestPricedHotelsForCalendar)).thenReturn(expectedHotels);


    HotelCityTax hotelCityTax = mock(HotelCityTax.class);
    HotelsCityTaxInfo hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);
    when(hotelsCityTaxInfo.getHotelsCityTaxes()).thenReturn(Map.of(hotelCode, hotelCityTax));

    expectedBestPricedHotelsForCalendar.get(0).setBestPriceWithCityTax(new BigDecimal("55.00"));
    final HotelPriceCalendarRequest hotelPriceCalendarRequest = buildHotelPriceCalendarRequest();
    hotelPriceCalendarRequest.setHotelCityTaxInfo(hotelsCityTaxInfo);
    when(hotelJpaRepository.findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
        LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType()))
        .thenReturn(expectedBestPricedHotelsForCalendar);
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(false);

    try (MockedStatic<CityTaxUtil> cityTaxUtilMock = Mockito.mockStatic(CityTaxUtil.class)) {
      cityTaxUtilMock.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any()))
          .thenReturn(true);
      cityTaxUtilMock.when(() ->
          CityTaxUtil.shouldCalculateCityTax(eq(false), eq(null))).thenReturn(false);

      List<Hotel> actualBestPricedHotels = hotelPricePersistencePort
          .getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);

      verify(hotelJpaRepository).findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
          LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType());
      assertThat(actualBestPricedHotels).hasSameElementsAs(expectedHotels);
    }
  }

  @Test
  void shouldThrowErrorWhenRetrievingBestPriceHotel() {
    final String hotelCode = "PLYPTI";
    final HotelPriceCalendarRequest hotelPriceCalendarRequest = buildHotelPriceCalendarRequest();
    doThrow(new RuntimeException()).when(hotelJpaRepository)
        .findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
            LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType());

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> hotelPricePersistencePort
        .getBestPricesForGivenHotelCode(hotelCode, hotelPriceCalendarRequest));
    verify(hotelJpaRepository).findBestPricesForGivenHotelCode(hotelCode, LocalDate.parse(ARRIVAL),
        LocalDate.parse(DEPARTURE), hotelPriceCalendarRequest.getRoomType());
  }


  @Test
  void shouldReturnEmptyBestPricedHotel() {

    when(hotelJpaRepository.findBestPricesForGivenHotelCode(null, null, null, null))
        .thenReturn(Collections.emptyList());

    when(BestPricedHotelToHotelMapper.mapBestPricedHotelToHotel("DB", Collections.emptyList()))
        .thenReturn(Collections.emptyList());

    assertThrows(ResponseStatusException.class,
        () -> hotelPricePersistencePort.getBestPricesForGivenHotelCode(null, null));

  }

  //End calendar


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

  private List<Hotel> mockExpectedHotels() {
    return Arrays.asList(
        createHotel("PLYPTI", "PI", "DB", new BigDecimal(50.0), "G"),
        createHotel("PLYLOC", "PI", "DB", new BigDecimal(32.0), "G"),
        createHotel("PLYMAR", "PI", "DB", new BigDecimal(87.0), "G"),
        createHotel("LISBAR", "PI", "DB", new BigDecimal(45.0), "G")
    );
  }

  private Hotel createHotel(String hotelCode, String hotelBrand, String roomType, BigDecimal price, String currency) {
    return Hotel.builder()
        .hotelCode(hotelCode)
        .hotelBrand(hotelBrand)
        .bestRoomPrice(new BestRoomPrice(roomType, new Price(price, currency)))
        .build();
  }
}
