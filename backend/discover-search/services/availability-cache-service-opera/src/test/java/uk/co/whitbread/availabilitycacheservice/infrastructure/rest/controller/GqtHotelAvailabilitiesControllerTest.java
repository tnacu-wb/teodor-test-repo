package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Rate;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.GqtHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.gqt.GqtHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.GqtSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt.GqtHotelAvailabilitiesDto;

@ExtendWith(MockitoExtension.class)
class GqtHotelAvailabilitiesControllerTest {

  private static final String ARRIVAL = LocalDate
      .parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate
      .parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();

  @InjectMocks
  private GqtHotelAvailabilitiesController gqtHotelAvailabilitiesController;

  @Mock
  private GqtHotelAvailabilitiesPort gqtHotelAvailabilitiesService;
  @Mock
  private GqtHotelAvailabilitiesMapper gqtHotelAvailabilitiesMapper;

  private final List<String> hotelCodes = new ArrayList<>();
  private GqtSearchCriteria gqtSearchCriteria;

  @BeforeEach
  void setUp() {
    hotelCodes.add("LONLET");
    hotelCodes.add("BASQUA");
    hotelCodes.add("COVCRO");
    gqtSearchCriteria = buildGqtSearchCriteria(hotelCodes, ARRIVAL, DEPARTURE, "GB", "EN");
  }
  @Test
  void testGetGqtHotelAvailabilities() {

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, ARRIVAL, DEPARTURE, "GB", "EN");

    final List<GqtOperaHotelAvailabilities> hotelAvailabilitiesListExpected = new ArrayList<>();
    final GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities =
        buildGqtOperaHotelAvailabilities();
    hotelAvailabilitiesListExpected.add(gqtOperaHotelAvailabilities);
    Mockito.when(
            gqtHotelAvailabilitiesService.getGqtHotelAvailabilities(gqtSearchPayload))
        .thenReturn(hotelAvailabilitiesListExpected);

    ResponseEntity<GqtHotelAvailabilitiesDto> responseEntity =
        gqtHotelAvailabilitiesController.getGqtHotelAvailabilities(gqtSearchCriteria);

    assertThat(responseEntity).isNotNull();
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

  }

  @Test
  void testGetGqtHotelAvailabilities_NoAvailability() {

    ResponseEntity<GqtHotelAvailabilitiesDto> responseEntity =
        gqtHotelAvailabilitiesController.getGqtHotelAvailabilities(gqtSearchCriteria);

    assertThat(responseEntity).isNotNull();
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

  }
  private GqtSearchCriteria buildGqtSearchCriteria(
      final List<String> hotelCodes, final String arrival, final String departure,
      final String country, final String language
  ) {
    return GqtSearchCriteria.builder()
        .hotelCodes(hotelCodes)
        .arrival(arrival)
        .departure(departure)
        .country(country)
        .language(language)
        .build();
  }

  private GqtSearchPayload buildGqtSearchPayload(
      final List<String> hotelCodes, final String arrival, final String departure,
      final String country, final String language
  ) {
    return GqtSearchPayload.builder()
        .hotelCodes(hotelCodes)
        .arrival(arrival)
        .departure(departure)
        .country(country)
        .language(language)
        .build();
  }

  private GqtOperaHotelAvailabilities buildGqtOperaHotelAvailabilities() {

    final Set<Rate> rates1 = new HashSet<>();
    final Rate rate1 =
        buildRate("S", "advance", "G");
    final Rate rate2 =
        buildRate("F", "Flex", "G");
    final Rate rate3 =
        buildRate("U", "Standard", "G");

    rates1.add(rate1);
    rates1.add(rate2);
    rates1.add(rate3);

    final Availabilities availabilities1 = buildAvailabilities(LocalDate.now().plusDays(1), rates1);

    final Set<Rate> rates2 = new HashSet<>();
    final Rate rate21 =
        buildRate("S", "advance", "G");
    final Rate rate22 =
        buildRate("F", "Flex", "G");
    final Rate rate23 =
        buildRate("U", "Standard", "G");

    rates2.add(rate21);
    rates2.add(rate22);
    rates2.add(rate23);

    final Availabilities availabilities2 = buildAvailabilities(LocalDate.now().plusDays(2), rates2);

    final Set<Availabilities> availabilitiesSet = new HashSet<>();
    availabilitiesSet.add(availabilities1);
    availabilitiesSet.add(availabilities2);

    return GqtOperaHotelAvailabilities.builder()
        .hotelCode("LONLET")
        .availabilities(availabilitiesSet)
        .build();
  }

  private Availabilities buildAvailabilities(
      final LocalDate availableDate, final Set<Rate> rates) {

    return Availabilities.builder()
        .availableDate(availableDate)
        .rates(rates)
        .build();
  }

  private Rate buildRate(final String classification, final String code, final String currency) {

    final Set<Room> rooms = new HashSet<>();
    final Room room1 = buildRoom("db", 3);
    final Room room2 = buildRoom("tp", 6);
    rooms.add(room1);
    rooms.add(room2);

    return Rate.builder()
        .available(true)
        .classification(classification)
        .code(code)
        .currency(currency)
        .rooms(rooms)
        .build();
  }

  private Room buildRoom(final String type, final int qun) {
    return Room.builder()
        .roomType(type)
        .quantity(qun)
        .build();

  }
}
