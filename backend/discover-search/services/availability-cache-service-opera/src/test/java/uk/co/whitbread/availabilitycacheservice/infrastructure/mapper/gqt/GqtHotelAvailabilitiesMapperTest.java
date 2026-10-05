package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.gqt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Rate;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt.GqtOperaHotelAvailabilitiesDto;


public class GqtHotelAvailabilitiesMapperTest {

  private LocalDate tomorrow = LocalDate.now().plusDays(1);
  private LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);


  @Test
  public void shouldMapRoomToRoomDtoTest() {

    //given
    List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList = new ArrayList<>();
    GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities1 = buildGqtOperaHotelAvailabilities();
    gqtOperaHotelAvailabilitiesList.add(gqtOperaHotelAvailabilities1);

    //when
    GqtHotelAvailabilitiesMapper gqtHotelAvailabilitiesMapper =
        Mappers.getMapper(GqtHotelAvailabilitiesMapper.class);

    List<GqtOperaHotelAvailabilitiesDto> gqtOperaHotelAvailabilitiesDtoList
        = gqtHotelAvailabilitiesMapper.
        toGqtOperaHotelAvailabilitiesDtoList(gqtOperaHotelAvailabilitiesList);

    /*List<GqtOperaHotelAvailabilitiesDto> gqtOperaHotelAvailabilitiesDtoList
        = GqtHotelAvailabilitiesMapper.INSTANCE.
        toGqtOperaHotelAvailabilitiesDtoList(gqtOperaHotelAvailabilitiesList);*/

    //then
    assertNotNull(gqtOperaHotelAvailabilitiesDtoList);
    assertFalse(gqtOperaHotelAvailabilitiesDtoList.isEmpty());
    assertEquals(
        gqtOperaHotelAvailabilitiesList.size(), gqtOperaHotelAvailabilitiesDtoList.size());
    assertEquals(buildGqtOperaHotelAvailabilities(), gqtOperaHotelAvailabilitiesList.get(0));

  }

  private GqtOperaHotelAvailabilities buildGqtOperaHotelAvailabilities() {

    final Availabilities availabilities1 = getAvailabilities(getRates1(), tomorrow);

    final Availabilities availabilities2 = getAvailabilities(getRates2(), dayAfterTomorrow);

    final Set<Availabilities> availabilitiesSet = getAvailabilitiesSet(
        availabilities1, availabilities2);

    return GqtOperaHotelAvailabilities.builder()
        .hotelCode("oxford")
        .availabilities(availabilitiesSet)
        .build();
  }

  private Set<Availabilities> getAvailabilitiesSet(Availabilities availabilities1,
      Availabilities availabilities2) {
    final Set<Availabilities> availabilitiesSet = new HashSet<>();
    availabilitiesSet.add(availabilities1);
    availabilitiesSet.add(availabilities2);
    return availabilitiesSet;
  }

  private Availabilities getAvailabilities(Set<Rate> rates12, LocalDate tomorrow) {
    final Set<Rate> rates1 = rates12;

    return buildAvailabilities(tomorrow, rates1);
  }

  private Set<Rate> getRates2() {
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
    return rates2;
  }

  private Set<Rate> getRates1() {
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
    return rates1;
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
        .amount(new BigDecimal("50.45"))
        .minNights(1)
        .maxNights(7)
        .cta(true)
        .build();

  }

}
