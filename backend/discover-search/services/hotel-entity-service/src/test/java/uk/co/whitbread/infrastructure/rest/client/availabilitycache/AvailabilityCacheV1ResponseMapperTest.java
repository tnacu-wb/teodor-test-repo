package uk.co.whitbread.infrastructure.rest.client.availabilitycache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1ResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1ResponseMapperImpl;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RatePlanOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RoomOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.TotalCostDto;

@ExtendWith(MockitoExtension.class)
public class AvailabilityCacheV1ResponseMapperTest {
  @InjectMocks
  AvailabilityCacheV1ResponseMapperImpl availabilityCacheV1ResponseMapperImpl;

  public AvailabilityCacheV1ResponseMapper availabilityCacheV1ResponseMapper;

  @Test
  public final void testCalculateLowestRate_Ok() {
    BigDecimal lowestRoomRateForOpera = availabilityCacheV1ResponseMapperImpl.getLowestRoomRateForOpera(
        createOperaHotel());

    assertThat(lowestRoomRateForOpera.intValue(), is(150));
  }

  @Test
  public final void testCalculateLowestRate_emptyRates() {
    BigDecimal lowestRoomRateForOpera = availabilityCacheV1ResponseMapperImpl.getLowestRoomRateForOpera(
        createEmptyRatesOperaHotel());

    assertNull(lowestRoomRateForOpera);
  }

  private HotelOperaDto createEmptyRatesOperaHotel(){
    return new HotelOperaDto("LONLEI", "Test Hotel Name", "pi",
        true, false, false, false, "OPERA", createEmptyRates(), "EMP01", false, 1);
  }

  private HotelOperaDto createOperaHotel(){
    return new HotelOperaDto("LONLEI", "Test Hotel Name", "pi",
        true, false, false, false, "OPERA", createRatesList(), "EMP01", false, 1);
  }

  private List<RatePlanOperaDto> createEmptyRates() {
    List<RatePlanOperaDto> ratePlanDtoList = Collections.emptyList();
    return ratePlanDtoList;
  }

  private List<RatePlanOperaDto> createRatesList() {
    List<RatePlanOperaDto> ratePlanDtoList = new LinkedList<>();
    List<List<RoomOperaDto>> rooms1 = List.of(createRoomsDB(new LinkedList()),createRoomsFAM(new LinkedList<>()));
    List<List<RoomOperaDto>> rooms2 = List.of(createRoomsDB(new LinkedList()),createRoomsFAM(new LinkedList<>()));
    List<List<RoomOperaDto>> rooms3 = List.of(createRoomsDB(new LinkedList()),createRoomsFAM(new LinkedList<>()));
    ratePlanDtoList.add(new RatePlanOperaDto("A",
        "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
        "FLEXRATE", "1","aaa", rooms1));
    ratePlanDtoList.add(new RatePlanOperaDto("C",
        "Bla bla semiflex",
        "SEMIFLEX", "1", "bbbb", rooms2));
    ratePlanDtoList.add(new RatePlanOperaDto("S",
        "Bla bla standard",
        "Standard", "1", "cccc", rooms3));
    return ratePlanDtoList;
  }

  private List<RoomOperaDto> createRoomsDB(LinkedList  roomDtoList) {
    roomDtoList.add(new RoomOperaDto("DOUBLE", false, 1, 1,
        new TotalCostDto(new BigDecimal(60.00),"USD")));
    roomDtoList.add(new RoomOperaDto("WETDBL", false, 1, 2,
        new TotalCostDto(new BigDecimal(50.00),"USD")));
    return roomDtoList;
  }

  private List<RoomOperaDto> createRoomsFAM(LinkedList  roomDtoList) {
    roomDtoList.add(new RoomOperaDto("FMTRPL", false, 1, 1,
        new TotalCostDto(new BigDecimal(40.00),"USD")));
    return roomDtoList;
  }

}
