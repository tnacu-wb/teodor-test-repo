package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelMigrationStatusReaderRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OperaHotelAvailPersistenceSvcTest {

  private static final String ARRIVAL = LocalDate
      .parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate
      .parse(LocalDate.now().plusDays(2).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  @Mock
  HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;
  @Mock
  OperaHotelsPostProcessorPort operaHotelsPostProcessorPort;
  @Mock
  HotelMigrationStatusReaderRepository hotelMigStatusReaderRepository;
  @Mock
  HotelPriceProperties hotelPriceProperties;

  @InjectMocks
  private OperaHotelAvailPersistenceSvc operaHotelAvailPersistenceSvc;

  private OperaHotelPersistenceAdapter operaHotelPersistenceAdapter;
  private OperaHotelsSearchCriteria operaHotelsSearchCriteria;
  private List<String> validOperaRoomTypes;

  @BeforeEach
  void setup() {
    operaHotelPersistenceAdapter = new OperaHotelAvailPersistenceSvc(
        hotelAvailabilitiesJpaRepository,
        operaHotelsPostProcessorPort, hotelMigStatusReaderRepository, hotelPriceProperties);

    validOperaRoomTypes = Arrays.asList("DB", "SB", "DIS", "PRE", "FAM");

    operaHotelAvailPersistenceSvc = new OperaHotelAvailPersistenceSvc(
        hotelAvailabilitiesJpaRepository,
        operaHotelsPostProcessorPort,
        hotelMigStatusReaderRepository,
        hotelPriceProperties
    );
  }

  @Test
  void returnsHotelsWhenNoOnSaleTrue() {
    List<String> hotelCodes = Arrays.asList("LONEUS", "HEAPTI");
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1},
        new boolean[]{false, false, false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"},
            {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("LONEUS", "HEAPTI"));
    HotelMigrationStatusEntity e1 = mock(HotelMigrationStatusEntity.class);
    when(e1.isOnSale()).thenReturn(true);
    when(e1.getHotelCode()).thenReturn("LONEUS");
    when(e1.getPmsSource()).thenReturn("OPERA");
    HotelMigrationStatusEntity e2 = mock(HotelMigrationStatusEntity.class);
    when(e2.isOnSale()).thenReturn(true);
    when(e2.getHotelCode()).thenReturn("HEAPTI");
    when(e2.getPmsSource()).thenReturn("OPERA");
    when(hotelMigStatusReaderRepository.getMigrationStatusForHotels(hotelCodes))
        .thenReturn(Arrays.asList(e1, e2));
    List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet = Collections.singletonList(mock(HotelAvailabilitiesResultSet.class));
    List<Hotel> hotels = Collections.singletonList(mock(Hotel.class));
    doReturn(hotelAvailabilitiesResultSet).when(hotelAvailabilitiesJpaRepository).findAvailabilitiesForOpera(any(), any(), any(), any());
    doReturn(hotels).when(operaHotelsPostProcessorPort).performOperaHotelsPostProcess(any(), any(), any());

    List<Hotel> result = operaHotelAvailPersistenceSvc.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria);

    assertThat(result).isEqualTo(hotels);
  }

  @Test
  void returnsHotelsWhenNoOnSaleFalse() {
    List<String> hotelCodes = Arrays.asList("LONEUS", "HEAPTI");
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1},
        new boolean[]{false, false, false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"},
            {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("LONEUS", "HEAPTI"));
    HotelMigrationStatusEntity e1 = mock(HotelMigrationStatusEntity.class);
    when(e1.isOnSale()).thenReturn(false);
    when(e1.getHotelCode()).thenReturn("LONEUS");
    when(e1.getPmsSource()).thenReturn("OPERA");
    HotelMigrationStatusEntity e2 = mock(HotelMigrationStatusEntity.class);
    when(e2.isOnSale()).thenReturn(false);
    when(e2.getHotelCode()).thenReturn("HEAPTI");
    when(e2.getPmsSource()).thenReturn("OPERA");
    when(hotelMigStatusReaderRepository.getMigrationStatusForHotels(hotelCodes))
        .thenReturn(Arrays.asList(e1, e2));
    List<Hotel> result = operaHotelAvailPersistenceSvc.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria);

    assertThat(result.size()).isEqualTo(0);
  }

  @Test
  void getOperaRoomTypesTest() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("DB");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{2},
        new int[]{0}, new int[]{1},
        new boolean[]{false}, 1, new String[][]{{"DB", "EXTDB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertArrayEquals(new String[]{"DB"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest1() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("DB");
    operaRoomTypesFromSearchCriteria.add("SB");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{2, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"DB", "EXTDB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(2, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"DB", "SB"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest2() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("SB");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1},
        new int[]{0, 0}, new int[]{1, 1},
        new boolean[]{false, false}, 2,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(2, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"SB", "SB"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest3() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("SB");
    operaRoomTypesFromSearchCriteria.add("DB");
    operaRoomTypesFromSearchCriteria.add("FAM");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1},
        new boolean[]{false, false, false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"},
            {"FAM", "EXTSB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(4, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"SB", "SB", "DB", "FAM"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest4() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("SB");
    operaRoomTypesFromSearchCriteria.add("DB");
    operaRoomTypesFromSearchCriteria.add("FAM");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1},
        new boolean[]{false, false, false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"},
            {"SB", "EXTSB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(4, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"SB", "SB", "DB", "SB"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest5() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("SB");
    operaRoomTypesFromSearchCriteria.add("DB");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1},
        new int[]{0, 0, 0}, new int[]{1, 1, 1},
        new boolean[]{false, false, false, false}, 3,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(3, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"SB", "SB", "DB"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest6() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("SB");
    operaRoomTypesFromSearchCriteria.add("DB");
    //operaRoomTypesFromSearchCriteria.add("FAM");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1},
        new boolean[]{false, false, false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"},
            {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(4, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"SB", "SB", "DB", "DB"}, operaRoomTypes);
  }

  @Test
  void getOperaRoomTypesTest7() {
    final Set<String> operaRoomTypesFromSearchCriteria = new HashSet<>();
    operaRoomTypesFromSearchCriteria.add("SB");
    operaRoomTypesFromSearchCriteria.add("DB");

    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new int[]{1, 1, 1, 1},
        new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1},
        new boolean[]{false, false, false, false}, 4,
        new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}, {"DB", "EXTDB", "SBDB"},
            {"DB", "EXTSB", "SBDB"}},
        Arrays.asList("LONSLA", "OXFORD"));
    String[] operaRoomTypes = operaHotelPersistenceAdapter
        .getOperaRoomTypes(operaHotelsSearchCriteria,
            operaRoomTypesFromSearchCriteria, validOperaRoomTypes);

    assertEquals(true, Arrays.stream(operaRoomTypes).findAny().isPresent());
    assertEquals(4, Arrays.stream(operaRoomTypes).count());
    assertArrayEquals(new String[]{"SB", "SB", "DB", "DB"}, operaRoomTypes);
  }

  private OperaHotelsSearchCriteria buildOperaHotelsSearchCriteria(int[] adults,
                                                                   int[] children,
                                                                   int[] roomQty, boolean[] cot,
                                                                   int rooms, String[][] roomTypes,
                                                                   List<String> hotelCodes) {
    return OperaHotelsSearchCriteria.builder()
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .country(COUNTRY)
        .language(LANGUAGE)
        .adults(adults)
        .children(children)
        .roomQty(roomQty)
        .cot(cot)
        .rooms(rooms)
        .roomTypes(roomTypes)
        .hotelCodes(hotelCodes)
        .build();
  }

}
