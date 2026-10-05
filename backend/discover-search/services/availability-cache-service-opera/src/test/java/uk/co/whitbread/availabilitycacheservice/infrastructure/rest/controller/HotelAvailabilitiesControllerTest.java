package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static mocks.HotelMock.buildAllHotels;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;

@ExtendWith(MockitoExtension.class)
public class HotelAvailabilitiesControllerTest {

  private static final String ARRIVAL = "2021-06-14";
  private static final String DEPARTURE = "2021-06-16";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private static final int ROOMS = 1;
  private static final int[] ADULTS = new int[]{1};
  private static final int[] CHILDREN = new int[0];
  @Mock
  HotelAvailabilitiesPort hotelAvailabilitiesPort;
  private HotelAvailabilitiesController controller;
  private SearchCriteria searchCriteria;

  @BeforeEach
  public void setup() {
    searchCriteria = buildSearchCriteria();
    controller = new HotelAvailabilitiesController(hotelAvailabilitiesPort);
  }

  @Test
  public void shouldReturnHotelAvailabilities() {
    List<Hotel> hotelList = buildAllHotels();
    when(hotelAvailabilitiesPort.getHotelAvailabilities(searchCriteria)).thenReturn(hotelList);

    ResponseEntity<HotelAvailabilitiesDto> response = controller.getHotelAvailabilities(searchCriteria);

    HotelAvailabilitiesDto hotelAvailabilitiesDtoResponse = response.getBody();
    assertThat(hotelAvailabilitiesDtoResponse.getHotelAvailabilities()).isNotEmpty();
    assertThat(hotelAvailabilitiesDtoResponse.getHotelAvailabilities())
        .extracting(HotelDto::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void shouldReturnEmptyHotelAvailabilitiesWith200Response() {
    when(hotelAvailabilitiesPort.getHotelAvailabilities(searchCriteria)).thenReturn(Collections.emptyList());

    ResponseEntity<HotelAvailabilitiesDto> response = controller.getHotelAvailabilities(searchCriteria);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertTrue(response.getBody().getHotelAvailabilities().isEmpty());
  }

  @Test
  public void shouldThrowInternalServerError() {
    when(hotelAvailabilitiesPort.getHotelAvailabilities(searchCriteria))
        .thenThrow(new RuntimeException("Error occurred while searching hotel availabilities"));

    assertThatExceptionOfType(HotelAvailabilitiesException.class)
        .isThrownBy(() -> controller.getHotelAvailabilities(searchCriteria))
        .withMessageContaining("Error occurred while searching hotel availabilities");
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
