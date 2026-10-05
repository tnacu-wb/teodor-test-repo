package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mocks.HotelMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.OperaHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

@ExtendWith(MockitoExtension.class)

class OperaHotelAvailabilitiesServiceTest {

  private static final String ARRIVAL = LocalDate
      .parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate
      .parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";

  @Mock
  private OperaHotelPersistenceAdapter operaHotelPersistenceAdapter;

  private OperaHotelAvailabilitiesPort operaHotelAvailabilitiesPort;

  private List<Hotel> hotelList;

  @BeforeEach
  void setup() {
    operaHotelAvailabilitiesPort = new OperaHotelAvailabilitiesService(operaHotelPersistenceAdapter);
    hotelList = HotelMock.buildAllHotelsOfOpera();
  }

  @Test
  void getOperaHotelAvailabilitiesTest() {
    OperaHotelsSearchCriteria operaHotelsSearchCriteria =
        buildOperaHotelsSearchCriteria(new int[]{1, 1},
            new int[]{0, 0}, new int[]{1, 1},
            new boolean[]{false, false}, 2,
            new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
            Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));

    when(operaHotelPersistenceAdapter.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria))
        .thenReturn(hotelList);

    List<Hotel> operaHotelAvailabilitiesResponse = operaHotelAvailabilitiesPort.
        getOperaHotelAvailabilities(operaHotelsSearchCriteria);

    assertThat(operaHotelAvailabilitiesResponse).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI");
    assertThat(operaHotelAvailabilitiesResponse).hasSize(5);

    //validation added to check that arrivalDateToday is true
    assertThat(operaHotelAvailabilitiesResponse.get(0).getArrivalDateToday()).isTrue();
  }

  @Test
  void getOperaHotelAvailabilitiesTestWithArrivalDayFalse() {
    OperaHotelsSearchCriteria operaHotelsSearchCriteria =
        buildOperaHotelsSearchCriteria(new int[]{1, 1},
            new int[]{0, 0}, new int[]{1, 1},
            new boolean[]{false, false}, 2,
            new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
            Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));

    operaHotelsSearchCriteria.setArrival(LocalDate
        .parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());

    operaHotelsSearchCriteria.setDeparture(LocalDate
        .parse(LocalDate.now().plusDays(2).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString());
    when(operaHotelPersistenceAdapter.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria))
        .thenReturn(hotelList);

    List<Hotel> operaHotelAvailabilitiesResponse = operaHotelAvailabilitiesPort.
        getOperaHotelAvailabilities(operaHotelsSearchCriteria);

    assertThat(operaHotelAvailabilitiesResponse).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI");
    assertThat(operaHotelAvailabilitiesResponse).hasSize(5);

    //validation added to check that arrivalDateToday is true
    assertThat(operaHotelAvailabilitiesResponse.get(0).getArrivalDateToday()).isFalse();
  }

  @Test
  void getOperaHotelAvailabilitiesTestWithEmptyList() {
    OperaHotelsSearchCriteria operaHotelsSearchCriteria =
        buildOperaHotelsSearchCriteria(new int[]{1, 1},
            new int[]{0, 0}, new int[]{1, 1},
            new boolean[]{false, false}, 2,
            new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
            Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));

    when(operaHotelPersistenceAdapter.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria))
        .thenReturn(Collections.emptyList());

    List<Hotel> operaHotelAvailabilitiesResponse = operaHotelAvailabilitiesPort.
        getOperaHotelAvailabilities(operaHotelsSearchCriteria);

    assertThat(operaHotelAvailabilitiesResponse).isEmpty();
  }

  @Test
  void getOperaHotelAvailabilitiesTestWithException() {
    OperaHotelsSearchCriteria operaHotelsSearchCriteria =
        buildOperaHotelsSearchCriteria(new int[]{1, 1},
            new int[]{0, 0}, new int[]{1, 1},
            new boolean[]{false, false}, 2,
            new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
            Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));

    when(operaHotelPersistenceAdapter.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria))
        .thenThrow(RuntimeException.class);

    assertThatExceptionOfType(Exception.class)
        .isThrownBy(() -> operaHotelAvailabilitiesPort.
            getOperaHotelAvailabilities(operaHotelsSearchCriteria));

  }

  @Test
  void getOperaHotelAvailabilitiesTestWithLimited() {
    OperaHotelsSearchCriteria operaHotelsSearchCriteria =
        buildOperaHotelsSearchCriteria(new int[]{1, 1},
            new int[]{0, 0}, new int[]{1, 1},
            new boolean[]{false, false}, 2,
            new String[][]{{"SB", "EXTSB", "SBDB"}, {"SB", "EXTSB", "SBDB"}},
            Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));

    when(operaHotelPersistenceAdapter.getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria))
        .thenReturn(hotelList);

    List<Hotel> operaHotelAvailabilitiesResponse = operaHotelAvailabilitiesPort.
        getOperaHotelAvailabilities(operaHotelsSearchCriteria);

    List<Room> rooms = operaHotelAvailabilitiesResponse.get(0).getRates().get(0).getRooms();

    long limitedAvailabilityCount = rooms.stream().filter(room -> room.getType().equals("DB"))
        .filter(roomlimit -> roomlimit.getLimitedAvailability()).count();
    assertEquals(1L, limitedAvailabilityCount);

    limitedAvailabilityCount = rooms.stream().filter(room -> room.getType().equals("S"))
        .filter(roomlimit -> roomlimit.getLimitedAvailability()).count();
    assertEquals(0L, limitedAvailabilityCount);

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
