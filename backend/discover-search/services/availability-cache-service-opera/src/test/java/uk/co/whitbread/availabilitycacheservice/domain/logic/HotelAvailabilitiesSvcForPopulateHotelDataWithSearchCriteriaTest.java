package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mocks.HotelMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelDataProcessPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;


public class HotelAvailabilitiesSvcForPopulateHotelDataWithSearchCriteriaTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";

  @Mock
  private HotelAvailabilitiesPersistencePort hotelAvailabilitiesPersistencePort;

  @Mock
  private HotelDataProcessPort hotelDataProcessPort;

  @Mock
  private PaginationAndSortingByPriceService paginationAndSortingByPriceService;

  @Mock
  private ContentClientLookUpService contentClientLookUpService;

  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;

  private HotelAvailabilitiesService hotelAvailabilitiesSvc;

  private List<Hotel> hotelList;

  private List<Hotel> hotelListWithoutRates;

  private SearchCriteria searchCriteria;

  @BeforeEach
  public void setup() {
    searchCriteria = buildSearchCriteria();
    hotelAvailabilitiesSvc = new HotelAvailabilitiesService(hotelAvailabilitiesPersistencePort, hotelDataProcessPort,
        paginationAndSortingByPriceService, contentClientLookUpService, cityTaxFeatureUtil);
    hotelList = HotelMock.buildAllHotels();
    hotelListWithoutRates = HotelMock.buildAllHotelsWithoutRates();
  }

  @Test
  public void testPopulateHotelDataWithSearchCriteriaWithArrivalTodayAsTrue() {

    hotelList = hotelAvailabilitiesSvc.populateHotelDataWithSearchCriteria(hotelList, searchCriteria);

    //Assertion to check that arrivalDateToday is true when arrivalDate is current Date
    assertThat(hotelList.get(0).getArrivalDateToday()).isTrue();
    assertThat(hotelList).isNotEmpty().extracting(Hotel::getArrivalDateToday)
        .containsOnly(true);
  }

  @Test
  public void testPopulateHotelDataWithSearchCriteriaWithArrivalTodayAsFalse() {

    searchCriteria.setArrival(
        LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());
    searchCriteria.setDeparture(
        LocalDate.parse(LocalDate.now().plusDays(2).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());
    hotelList = hotelAvailabilitiesSvc.populateHotelDataWithSearchCriteria(hotelList, searchCriteria);

    //Assertion to check that arrivalDateToday is False when arrivalDate is not current Date
    assertThat(hotelList.get(0).getArrivalDateToday()).isFalse();
    assertThat(hotelList).isNotEmpty().extracting(Hotel::getArrivalDateToday)
        .doesNotContain(true);
  }

  @Test
  public void testPopulateHotelDataWithSearchCriteriaWForEmptyHotels() {

    hotelList = hotelAvailabilitiesSvc.populateHotelDataWithSearchCriteria(Collections.emptyList(), searchCriteria);

    //Assertion to check that arrivalDateToday is False when arrivalDate is not current Date
    assertThat(hotelList).isEmpty();
  }

  private SearchCriteria buildSearchCriteria() {
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(false)
        .rooms(1)
        .type(new String[]{RoomType.SB.name()})
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }
}
