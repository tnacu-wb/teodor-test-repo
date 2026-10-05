package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import mocks.HotelMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelDataProcessPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@ExtendWith(MockitoExtension.class)

class HotelAvailabilitiesSvcTest {

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

  private HotelAvailabilitiesPort hotelAvailabilities;

  private List<Hotel> hotelList;

  private List<Hotel> hotelListWithoutRates;

  @BeforeEach
  void setup() {
    hotelAvailabilities = new HotelAvailabilitiesService(hotelAvailabilitiesPersistencePort, hotelDataProcessPort,
        paginationAndSortingByPriceService, contentClientLookUpService, cityTaxFeatureUtil);
    hotelList = HotelMock.buildAllHotels();
    hotelListWithoutRates = HotelMock.buildAllHotelsWithoutRates();
  }

  @Test
  void shouldReturnHotelAvailabilitiesSuccess() {
    SearchCriteria searchCriteria = buildSearchCriteria();
    when(hotelAvailabilitiesPersistencePort.getHotelsByCodeAndAvailDateBetween(searchCriteria)).thenReturn(hotelList);

    List<Hotel> hotelAvailabilitiesResponse = hotelAvailabilities.getHotelAvailabilities(searchCriteria);

    assertThat(hotelAvailabilitiesResponse).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI");
    assertThat(hotelAvailabilitiesResponse).hasSize(5);
    //validation added to check that arrivalDateToday is true
    assertThat(hotelAvailabilitiesResponse.get(0).getArrivalDateToday()).isTrue();
  }


  @Test
  void shouldReturnEmptyHotelAvailabilities() {
    SearchCriteria searchCriteria = buildSearchCriteria();

    List<Hotel> hotelAvailabilitiesResponse = hotelAvailabilities.getHotelAvailabilities(searchCriteria);
    assertThat(hotelAvailabilitiesResponse).isEmpty();
  }


  private SearchCriteria buildSearchCriteria() {
    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI
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
