package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Rate;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.GqtHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@ExtendWith(MockitoExtension.class)
class GqtHotelAvailabilitiesServiceTest {

  @Mock
  private GqtHotelAvailabilitiesPersistencePort gqtHotelAvailabilitiesPersistencePort;

  @Mock
  private ContentClientLookUpService contentClientLookUpService;

  private GqtHotelAvailabilitiesPort gqtHotelAvailabilitiesService;

  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;

  @BeforeEach
  void setup() {
    gqtHotelAvailabilitiesService =
        new GqtHotelAvailabilitiesService(gqtHotelAvailabilitiesPersistencePort,
            contentClientLookUpService, cityTaxFeatureUtil);
  }

  @Test
  void getGqtHotelAvailabilitiesSuccessTest() {

    final List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("oxford");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    final List<GqtOperaHotelAvailabilities> hotelAvailabilitiesListExpected = new ArrayList<>();
    final GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities =
        buildGqtOperaHotelAvailabilities();
    hotelAvailabilitiesListExpected.add(gqtOperaHotelAvailabilities);

    Mockito.when(
            gqtHotelAvailabilitiesPersistencePort.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload))
        .thenReturn(hotelAvailabilitiesListExpected);

    final List<GqtOperaHotelAvailabilities> hotelAvailabilitiesListActual =
        gqtHotelAvailabilitiesService.getGqtHotelAvailabilities(gqtSearchPayload);

    assertEquals(hotelAvailabilitiesListExpected, hotelAvailabilitiesListActual);

  }


  @Test
  void shouldReturnEmptyIfPersistencePortIsReturningEmptyTest() {

    final List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("oxford");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);

    final GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    Mockito.when(
            gqtHotelAvailabilitiesPersistencePort.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload))
        .thenReturn(Collections.emptyList());

    final List<GqtOperaHotelAvailabilities> hotelAvailabilitiesListActual =
        gqtHotelAvailabilitiesService.getGqtHotelAvailabilities(gqtSearchPayload);

    assertTrue(hotelAvailabilitiesListActual.isEmpty());

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
        buildRate(
            new BigDecimal("30.00"),
            "S", "advance", "G", 5, 1);
    final Rate rate2 =
        buildRate(
            new BigDecimal("40.00"),
            "F", "Flex", "G", 6, 2);
    final Rate rate3 =
        buildRate(
            new BigDecimal("50.00"),
            "U", "Standard", "G", 7, 3);

    rates1.add(rate1);
    rates1.add(rate2);
    rates1.add(rate3);

    final Availabilities availabilities1 = buildAvailabilities(LocalDate.now().plusDays(1), rates1);

    final Set<Rate> rates2 = new HashSet<>();
    final Rate rate21 =
        buildRate(
            new BigDecimal("60.00"),
            "S", "advance", "G", 5, 1);
    final Rate rate22 =
        buildRate(
            new BigDecimal("70.00"),
            "F", "Flex", "G", 6, 2);
    final Rate rate23 =
        buildRate(
            new BigDecimal("80.00"),
            "U", "Standard", "G", 7, 3);

    rates2.add(rate21);
    rates2.add(rate22);
    rates2.add(rate23);

    final Availabilities availabilities2 = buildAvailabilities(LocalDate.now().plusDays(2), rates2);

    final Set<Availabilities> availabilitiesSet = new HashSet<>();
    availabilitiesSet.add(availabilities1);
    availabilitiesSet.add(availabilities2);

    return GqtOperaHotelAvailabilities.builder()
        .hotelCode("oxford")
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

  private Rate buildRate(final BigDecimal amt, final String classification, final String code,
      final String currency, final int maxNights, final int minNights) {

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
