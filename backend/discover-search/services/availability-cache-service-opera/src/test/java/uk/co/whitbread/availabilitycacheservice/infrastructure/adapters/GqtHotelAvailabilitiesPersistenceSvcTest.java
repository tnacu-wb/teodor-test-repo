package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Rate;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.GqtHotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelMigrationStatusReaderRepository;

@ExtendWith(MockitoExtension.class)

class GqtHotelAvailabilitiesPersistenceSvcTest {

  private static final String PMS_SOURCE_OPERA = "OPERA";
  @Mock
  private GqtHotelAvailabilitiesJpaRepository gqtHotelAvailabilitiesJpaRepository;
  @Mock
  private HotelMigrationStatusReaderRepository hotelMigStatusReaderRepository;
  @Captor
  private ArgumentCaptor<List<String>> hotelCodesCaptor;
  @Captor
  private ArgumentCaptor<LocalDate> arrivalCaptor;
  @Captor
  private ArgumentCaptor<LocalDate> departureCaptor;
  private GqtHotelAvailabilitiesPersistencePort gqtHotelAvailabilitiesPersistenceSvc;
  @Mock
  private GqtHotelAvailPostProcessorPort gqtHotelAvailPostProcessorPort;

  @BeforeEach
  void setup() {
    gqtHotelAvailabilitiesPersistenceSvc = new GqtHotelAvailabilitiesPersistenceSvc(
        gqtHotelAvailabilitiesJpaRepository, hotelMigStatusReaderRepository, gqtHotelAvailPostProcessorPort);
  }

  @Test
  void oneHotelWithOnSaleFalseShouldReturnEmptyListTest() {
    final boolean onSaleFalse = false;
    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("asdfgh");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    List<HotelMigrationStatusEntity> hotelMigrationStatusEntities = new ArrayList<>();
    hotelMigrationStatusEntities.add(
        buildHotelMigrationStatus("asdfgh", onSaleFalse, PMS_SOURCE_OPERA));
    Mockito.when(hotelMigStatusReaderRepository.getMigrationStatusForHotels(hotelCodes))
        .thenReturn(hotelMigrationStatusEntities);

    final List<GqtOperaHotelAvailabilities> hotelsActual =
        gqtHotelAvailabilitiesPersistenceSvc.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload);

    assertTrue(hotelsActual.isEmpty());

  }

  @Test
  void shouldReturnEmptyListWhenNoOperaHotelCodesWithOnSaleTrueTest() {
    final boolean onSaleTrue = true;
    final boolean onSaleFalse = false;
    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("asdfgh");
    hotelCodes.add("manold");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    List<HotelMigrationStatusEntity> hotelMigrationStatusEntities = new ArrayList<>();
    hotelMigrationStatusEntities.add(
        buildHotelMigrationStatus("asdfgh", onSaleFalse, PMS_SOURCE_OPERA));
    Mockito.when(hotelMigStatusReaderRepository.getMigrationStatusForHotels(hotelCodes))
        .thenReturn(hotelMigrationStatusEntities);

    final List<GqtOperaHotelAvailabilities> hotelsActual =
        gqtHotelAvailabilitiesPersistenceSvc.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload);

    assertTrue(hotelsActual.isEmpty());

  }

  @Test
  void hotelsShouldNotBeProcessedTest() {
    final boolean onSaleTrue = true;
    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("manold");
    hotelCodes.add("tkinpt");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    List<HotelMigrationStatusEntity> hotelMigrationStatusEntities = new ArrayList<>();
    hotelMigrationStatusEntities.add(
        buildHotelMigrationStatus("manold", onSaleTrue, PMS_SOURCE_OPERA));
    Mockito.when(hotelMigStatusReaderRepository.getMigrationStatusForHotels(hotelCodes))
        .thenReturn(hotelMigrationStatusEntities);

    gqtHotelAvailabilitiesPersistenceSvc.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload);

    Mockito.verify(gqtHotelAvailabilitiesJpaRepository, Mockito.times(1))
        .findAvailabilitiesForGqtOpera(
            hotelCodesCaptor.capture(), arrivalCaptor.capture(), departureCaptor.capture());

    List<String> hotelCodesHavingSingle = new ArrayList<>();
    hotelCodesHavingSingle.add("manold");

    assertEquals(hotelCodesHavingSingle, hotelCodesCaptor.getValue());
    assertEquals(arrival, arrivalCaptor.getValue());
    assertEquals(departure, departureCaptor.getValue());

  }

  @Test
  void oneOperaHotelGetGqtHotelAvailabilitiesForOperaTest() {
    final boolean onSaleTrue = true;
    List<String> hotelCodes = new ArrayList<>();
    hotelCodes.add("manold");
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(2);
    GqtSearchPayload gqtSearchPayload =
        buildGqtSearchPayload(
            hotelCodes, arrival.toString(), departure.toString(), "GB", "EN");

    List<HotelMigrationStatusEntity> hotelMigrationStatusEntities = new ArrayList<>();
    hotelMigrationStatusEntities.add(
        buildHotelMigrationStatus("manold", onSaleTrue, PMS_SOURCE_OPERA));
    Mockito.when(hotelMigStatusReaderRepository.getMigrationStatusForHotels(hotelCodes))
        .thenReturn(hotelMigrationStatusEntities);

    List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSets = new ArrayList<>();

    HotelAvailabilitiesResultSet resultSet1 =
        buildHotelAvailabilitiesResultSet(
            "manold", arrival, "ADVANCE", "S", "SB", 5);
    hotelAvailabilitiesResultSets.add(resultSet1);

    Mockito.when(gqtHotelAvailabilitiesJpaRepository
            .findAvailabilitiesForGqtOpera(hotelCodes, arrival, departure))
        .thenReturn(hotelAvailabilitiesResultSets);

    List<Hotel> hotelsExpected = new ArrayList<>();
    Hotel hotel1 =
        buildHotel("manold", arrival.toString(), "ADVANCE", "S");
    hotelsExpected.add(hotel1);

    final Set<Rate> rates = new HashSet<>();
    final Set<Room> rooms = new HashSet<>();
    final Room room1 = Room.builder().roomType("SB").quantity(5).build();
    rooms.add(room1);
    Rate rate1 = buildRate(rooms, "ADVANCE", "S");
    rates.add(rate1);
    final Set<Availabilities> availabilitiesSet = new HashSet<>();
    final Availabilities availabilities1 = buildAvailabilities(arrival, rates);
    availabilitiesSet.add(availabilities1);
    List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilities = new ArrayList<>();
    GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities1 =
        buildGqtOperaHotelAvailabilities("manold", availabilitiesSet);
    gqtOperaHotelAvailabilities.add(gqtOperaHotelAvailabilities1);

    Mockito.when(
            gqtHotelAvailPostProcessorPort.
                processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSets, gqtSearchPayload))
        .thenReturn(gqtOperaHotelAvailabilities);

    final List<GqtOperaHotelAvailabilities> hotelsActual =
        gqtHotelAvailabilitiesPersistenceSvc.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload);

    assertNotNull(hotelsActual);
    assertFalse(hotelsActual.isEmpty());
    assertEquals(gqtOperaHotelAvailabilities1, hotelsActual.get(0));

  }

  private Rate buildRate(final Set<Room> rooms, final String code, final String classi) {
    return Rate.builder()
        .rooms(rooms)
        .code(code)
        .classification(classi)
        .build();
  }

  private Availabilities buildAvailabilities(
      final LocalDate availableDate, final Set<Rate> rates) {
    return Availabilities.builder()
        .availableDate(availableDate)
        .rates(rates)
        .build();
  }

  private GqtOperaHotelAvailabilities buildGqtOperaHotelAvailabilities(
      final String hotelCode, final Set<Availabilities> availabilitiesSet) {
    return GqtOperaHotelAvailabilities.builder()
        .hotelCode(hotelCode)
        .availabilities(availabilitiesSet)
        .build();
  }


  private Hotel buildHotel(final String hotelCode, final String date,
      final String rateCode, final String classification) {
    final List<RatePlan> rates = new ArrayList<>();
    RatePlan ratePlan1 = RatePlan.builder()
        .code(rateCode)
        .classification(classification)
        .build();
    rates.add(ratePlan1);
    return Hotel.builder()
        .hotelCode(hotelCode)
        .date(date)
        .available(true)
        .rates(rates)
        .build();
  }

  private HotelAvailabilitiesResultSet buildHotelAvailabilitiesResultSet(
      final String hotelCode, final LocalDate date, final String rateCode,
      final String classification, final String roomType, final int quantity) {
    return HotelAvailabilitiesResultSet.builder()
        .hotelCode(hotelCode)
        .availableDate(date)
        .availability(true)
        .rateCode(rateCode)
        .rateClassification(classification)
        .roomType(roomType)
        .quantity(quantity)
        .build();

  }

  private HotelMigrationStatusEntity buildHotelMigrationStatus(
      final String code, final boolean onSale, final String pmsSource) {
    return HotelMigrationStatusEntity.builder()
        .onSale(onSale)
        .pmsSource(pmsSource)
        .hotelCode(code)
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

}
