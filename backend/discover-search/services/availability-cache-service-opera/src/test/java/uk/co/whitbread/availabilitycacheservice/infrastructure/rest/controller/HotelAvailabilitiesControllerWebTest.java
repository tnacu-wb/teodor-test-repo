package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mocks.HotelDtoMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.DistributionHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.GqtHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.OperaHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.HotelPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.LocationPricePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.HotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.dsitribution.DistributionHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.gqt.GqtHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaRoomTypesValidator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionOperaRoomTypesValidator;

/**
 * Ignoring for time being as Tests fails to run mockMvc along with powerMock. In the meantime, replacing with
 * HotelAvailabilitiesControllerHttpTest which calls the actual static mapper instead of the mock
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest
@Disabled
class HotelAvailabilitiesControllerWebTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private HotelAvailabilitiesPort hotelAvailabilitiesPort;

  @MockitoBean
  private DistributionHotelAvailabilitiesPort distributionHotelAvailabilitiesPort;

  @MockitoBean
  private DistributionOperaRoomTypesValidator distributionOperaRoomTypesValidator;

  @MockitoBean
  private DistributionHotelAvailabilitiesMapper distributionHotelAvailabilitiesMapper;

  @MockitoBean
  private GqtHotelAvailabilitiesPort gqtHotelAvailabilitiesPort;

  @MockitoBean
  private GqtHotelAvailabilitiesMapper gqtHotelAvailabilitiesMapper;

  @MockitoBean
  private HotelPricePort hotelPricePort;

  @MockitoBean
  private LocationPricePort locationPricePort;

  @MockitoBean
  private OperaHotelAvailabilitiesPort operaHotelAvailabilitiesPort;

  @MockitoBean
  private OperaRoomTypesValidator operaRoomTypesValidator;

  @Test
  void shouldReturn200Response() throws Exception {

    List<Hotel> hotels = new ArrayList<>();

    Hotel hotel = Hotel.builder().hotelCode("LONLEI")
        .available(true)
        .arrivalDateToday(false)
        .limitedAvailability(false)
        .hotelBrand("pi").build();

    hotels.add(hotel);

    when(hotelAvailabilitiesPort.getHotelAvailabilities(any(SearchCriteria.class))).thenReturn(hotels);

    List<HotelDto> hotelDtoList = HotelDtoMock.buildAllHotelDtos();
    mockStatic(HotelAvailabilitiesMapper.class);
    when(HotelAvailabilitiesMapper.mapHotelToHotelDto(hotels)).thenReturn(hotelDtoList);

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("arrival", "2021-08-20");
    requestParams.add("departure", "2021-08-22");
    requestParams.add("country", "gb");
    requestParams.add("language", "en");
    requestParams.add("hotelCodes", "LONLEI,BASQUA,COVCRO");
    requestParams.add("adults", "2");
    requestParams.add("children", "1");
    requestParams.add("cot", "false");
    requestParams.add("type", "DB");
    requestParams.add("rooms", "1");
    requestParams.add("sort", "DISTANCE");

    this.mockMvc.perform(get("/search/hotels/availabilities").params(requestParams)).andExpect(status().isOk());
  }


  @Test
  void shouldReturn400BadRequest() throws Exception {

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("arrival", "2021-08-20");
    requestParams.add("departure", "2021-08-22");
    requestParams.add("country", "gb");
    requestParams.add("language", "en");
    requestParams.add("hotelCodes", "LONLEI,BASQUA,COVCRO");
    requestParams.add("adults", "2");
    requestParams.add("children", "1");
    requestParams.add("cot", "false");
    requestParams.add("type", "DB");
    requestParams.add("rooms", "1");

    this.mockMvc.perform(get("/search/hotels/availabilities")
        .params(requestParams)).andExpect(status().isBadRequest());
  }


  @Test
  void shouldReturn404NotFound() throws Exception {

    when(hotelAvailabilitiesPort.getHotelAvailabilities(buildSearchCriteria())).thenReturn(Collections.emptyList());

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("arrival", "2021-08-20");
    requestParams.add("departure", "2021-08-22");
    requestParams.add("country", "gb");
    requestParams.add("language", "en");
    requestParams.add("hotelCodes", "LONLEI,BASQUA,COVCRO");
    requestParams.add("adults", "2");
    requestParams.add("children", "1");
    requestParams.add("cot", "false");
    requestParams.add("type", "DB");
    requestParams.add("rooms", "1");
    requestParams.add("sort", "DISTANCE");

    this.mockMvc.perform(get("/search/hotels/availabilities")
        .params(requestParams)).andExpect(status().isNotFound());
  }

  private SearchCriteria buildSearchCriteria() {
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("LONLEI,BASQUA,COVCRO"))
        .arrival("2021-08-20")
        .departure("2021-08-22")
        .adults(new int[]{2})
        .children(new int[]{1})
        .cot(false)
        .rooms(1)
        .type(new String[]{RoomType.DB.name()})
        .language("en")
        .country("gb")
        .sort(SortType.DISTANCE)
        .page(0)
        .size(0)
        .build();
  }

}
