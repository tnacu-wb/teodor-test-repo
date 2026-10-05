package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.OperaHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaRoomTypesValidator;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

class OperaHotelAvailabilitiesControllerTest {

  @Mock
  private OperaHotelAvailabilitiesPort operaHotelAvailabilitiesPort;
  @Mock
  private OperaRoomTypesValidator roomTypeValidator;

  @InjectMocks
  private OperaHotelAvailabilitiesController controller;

  private static final String ARRIVAL = LocalDate
      .parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate
      .parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private final List<String> hotelCodes = new ArrayList<>();

  @BeforeEach
  void setUp() {
    hotelCodes.add("LONEUS");
    hotelCodes.add("MANXOF");
    hotelCodes.add("EDIPAR");
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnOkWhenHotelsAvailable() {
    OperaSearchCriteria criteria = buildOperaSearchCriteria(hotelCodes, ARRIVAL, DEPARTURE, "GB", "EN");
    MultiValueMap<String, String> roomTypeMap = new LinkedMultiValueMap<>();
    roomTypeMap.add("roomTypes", "DB");
    when(roomTypeValidator.isValidRoomTypes(any())).thenReturn(true);
    Hotel hotel = buildHotel("LONEUS");
    when(operaHotelAvailabilitiesPort.getOperaHotelAvailabilities(any())).thenReturn(List.of(hotel));

    ResponseEntity<OperaHotelAvailabilitiesDto> response = controller.getOperaHotelAvailabilities(criteria, roomTypeMap, false);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getOperaHotelAvailabilities()).hasSize(1);
  }

  @Test
  void shouldReturnOkWithEmptyListWhenNoHotelsAvailable() {
    OperaSearchCriteria criteria = buildOperaSearchCriteria(hotelCodes, ARRIVAL, DEPARTURE, "GB", "EN");
    MultiValueMap<String, String> roomTypeMap = new LinkedMultiValueMap<>();
    roomTypeMap.add("roomTypes", "DB");
    when(roomTypeValidator.isValidRoomTypes(any())).thenReturn(true);
    when(operaHotelAvailabilitiesPort.getOperaHotelAvailabilities(any())).thenReturn(Collections.emptyList());

    ResponseEntity<OperaHotelAvailabilitiesDto> response = controller.getOperaHotelAvailabilities(criteria, roomTypeMap, false);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getOperaHotelAvailabilities()).isEmpty();
  }

  @Test
  void shouldThrowRoomTypesExceptionWhenRoomTypeValidationFails() {
    OperaSearchCriteria criteria = buildOperaSearchCriteria(hotelCodes, ARRIVAL, DEPARTURE, "GB", "EN");
    MultiValueMap<String, String> roomTypeMap = new LinkedMultiValueMap<>();
    roomTypeMap.add("roomTypes", "DB");
    when(roomTypeValidator.isValidRoomTypes(any())).thenReturn(false);

    assertThatThrownBy(() -> controller.getOperaHotelAvailabilities(criteria, roomTypeMap, false))
        .isInstanceOf(HotelAvailabilitiesException.class)
        .hasMessageContaining("Invalid roomType or roomType & roomQty values");
  }

  @Test
  void shouldThrowHotelAvailabilitiesExceptionOnOtherException() {
    OperaSearchCriteria criteria = buildOperaSearchCriteria(hotelCodes, ARRIVAL, DEPARTURE, "GB", "EN");
    MultiValueMap<String, String> roomTypeMap = new LinkedMultiValueMap<>();
    roomTypeMap.add("roomTypes", "DB");
    when(roomTypeValidator.isValidRoomTypes(any())).thenReturn(true);
    when(operaHotelAvailabilitiesPort.getOperaHotelAvailabilities(any()))
        .thenThrow(new RuntimeException("Internal Error"));

    assertThatThrownBy(() -> controller.getOperaHotelAvailabilities(criteria, roomTypeMap, false))
        .isInstanceOf(HotelAvailabilitiesException.class)
        .hasMessageContaining("Internal Error");
  }

  private OperaSearchCriteria buildOperaSearchCriteria(final List<String> hotelCodes, final String arrival, final String departure,
                                            final String country, final String language) {
    return OperaSearchCriteria.builder()
        .hotelCodes(hotelCodes)
        .arrival(arrival)
        .departure(departure)
        .country(country)
        .language(language)
        .adults(new int[1])
        .children(new int[0])
        .cot(new boolean[]{false})
        .roomQty(new int[1])
    .build();
  }

  private Hotel buildHotel(final String hotelCode) {
    return Hotel.builder()
        .hotelCode(hotelCode)
        .pmsSource("OPERA")
        .build();
  }
}
