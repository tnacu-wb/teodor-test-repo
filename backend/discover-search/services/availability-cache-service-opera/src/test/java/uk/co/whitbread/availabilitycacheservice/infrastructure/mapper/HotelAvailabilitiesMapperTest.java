package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.HotelAvailabilitiesMapper.mapHotelToHotelDto;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.HotelAvailabilitiesMapper.mapHotelToOperaHotelDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelDto;


public class HotelAvailabilitiesMapperTest {

  private static final String PMS_SOURCE_OPERA = "OPERA";

  @Test
  public void mapHotelToHotelDtoTest() {
    Room room = Room.builder()
        .adults(2)
        .children(1)
        .type("DB")
        .totalPrice(new Price(new BigDecimal(100.00), "GBP"))
        .build();

    RatePlan ratePlan = RatePlan.builder()
        .classification("A")
        .code("1")
        .description("dummy rate plan")
        .rooms(Arrays.asList(room))
        .totalPrice(new Price(new BigDecimal(100.00), "GBP"))
        .build();

    Hotel hotel = Hotel.builder()
        .hotelCode("1")
        .rates(Arrays.asList(ratePlan))
        .available(true)
        .hotelName("Dummy hotel")
        .build();

    List<HotelDto> availableHotels = mapHotelToHotelDto(Arrays.asList(hotel));

    assertThat(availableHotels).isNotEmpty().extracting(HotelDto::getHotelCode)
        .containsExactly("1");
    assertThat(availableHotels).extracting(HotelDto::getRates).isNotEmpty();
  }

  @Test
  public void mapHotelToHotelDtoEmptyTest() {
    List<HotelDto> availableHotels = mapHotelToHotelDto(Collections.emptyList());
    assertThat(availableHotels).isEmpty();
  }

  @Test
  public void mapHotelToOperaHotelDtoEmptyTest() {
    MultiValueMap<String, String> emptyRoomTypes = new LinkedMultiValueMap<>();
    List<OperaHotelDto> availableHotels =
        mapHotelToOperaHotelDto(Collections.emptyList(), emptyRoomTypes);
    assertThat(availableHotels).isEmpty();
  }

  @Test
  public void mapHotelToOperaHotelDtoTest() {
    Room room = Room.builder()
        .adults(2)
        .children(1)
        .type("DB")
        .totalPrice(new Price(new BigDecimal(100.00), "GBP"))
        .qtyRequested(new Long(1))
        .quantityAvailable(1)
        .build();

    RatePlan ratePlan = RatePlan.builder()
        .classification("A")
        .code("1")
        .description("dummy rate plan")
        .rooms(Arrays.asList(room))
        .build();

    Hotel hotel = Hotel.builder()
        .hotelCode("1")
        .rates(Arrays.asList(ratePlan))
        .available(true)
        .hotelName("Dummy hotel")
        .pmsSource(PMS_SOURCE_OPERA)
        .build();

    MultiValueMap<String, String> roomTypesMap = new LinkedMultiValueMap<>();
    roomTypesMap.add("roomTypes", "DB");

    List<OperaHotelDto> availableHotels =
        mapHotelToOperaHotelDto(Arrays.asList(hotel), roomTypesMap);

    assertThat(availableHotels).isNotEmpty().extracting(OperaHotelDto::getHotelCode)
        .containsExactly("1");
    assertThat(availableHotels).extracting(OperaHotelDto::getRates).isNotEmpty();
  }

  @Test
  public void mapHotelToOperaHotelDtoGroupedUsingInputRoomTypesTest() {

    final Price price1 = new Price(new BigDecimal(100.00), "GBP");
    Room room1 = buildRoom(2, 1, "DB", price1, 1l);
    room1.setQuantityAvailable(1);

    final Price price2 = new Price(new BigDecimal(80.00), "GBP");
    Room room2 = buildRoom(2, 1, "DBLDBL", price2, 1l);
    room2.setQuantityAvailable(1);

    final Price price3 = new Price(new BigDecimal(70.00), "GBP");
    Room room3 = buildRoom(2, 1, "SB", price3, 1l);
    room3.setQuantityAvailable(1);

    final Price price4 = new Price(new BigDecimal(60.00), "GBP");
    Room room4 = buildRoom(2, 1, "ZIPSB", price4, 1l);
    room4.setQuantityAvailable(1);

    List<Room> rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    rooms.add(room3);
    rooms.add(room4);

    RatePlan ratePlan = buildRatePlan("A", "1", rooms);
    List<RatePlan> ratePlans = new ArrayList<>();
    ratePlans.add(ratePlan);

    final Hotel hotel =
        buildGivenHotel(
            "TKINPT", ratePlans, "Dummy-hotel1", PMS_SOURCE_OPERA);

    MultiValueMap<String, String> roomTypesMap = new LinkedMultiValueMap<>();
    roomTypesMap.add("roomTypes", "DB,DBLDBL");
    roomTypesMap.add("roomTypes", "SB,ZIPSB");

    List<OperaHotelDto> availableHotels =
        mapHotelToOperaHotelDto(Arrays.asList(hotel), roomTypesMap);

    assertThat(availableHotels).isNotEmpty().extracting(OperaHotelDto::getHotelCode)
        .containsExactly("TKINPT");
    assertThat(availableHotels).extracting(OperaHotelDto::getRates).isNotEmpty();
    assertThat(availableHotels.get(0).getRates()).hasSize(1);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms()).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(0)).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(1)).hasSize(2);

  }

  @Test
  public void mapHotelToOperaHotelDtoGroupedUsingInputRoomTypesManyRatesTest() {

    final Price price1 = new Price(new BigDecimal(100.00), "GBP");
    Room room1 = buildRoom(2, 1, "DB", price1, 1l);
    room1.setQuantityAvailable(1);

    final Price price2 = new Price(new BigDecimal(80.00), "GBP");
    Room room2 = buildRoom(2, 1, "DBLDBL", price2, 1l);
    room2.setQuantityAvailable(1);

    final Price price3 = new Price(new BigDecimal(70.00), "GBP");
    Room room3 = buildRoom(2, 1, "SB", price3, 1l);
    room3.setQuantityAvailable(1);

    final Price price4 = new Price(new BigDecimal(60.00), "GBP");
    Room room4 = buildRoom(2, 1, "ZIPSB", price4, 1l);
    room4.setQuantityAvailable(1);

    List<Room> rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    rooms.add(room3);
    rooms.add(room4);

    RatePlan ratePlan1 = buildRatePlan("A", "1", rooms);
    RatePlan ratePlan2 = buildRatePlan("B", "2", rooms);
    List<RatePlan> ratePlans = new ArrayList<>();
    ratePlans.add(ratePlan1);
    ratePlans.add(ratePlan2);

    final Hotel hotel =
        buildGivenHotel(
            "TKINPT", ratePlans, "Dummy-hotel1", PMS_SOURCE_OPERA);

    MultiValueMap<String, String> roomTypesMap = new LinkedMultiValueMap<>();
    roomTypesMap.add("roomTypes", "DB,DBLDBL");
    roomTypesMap.add("roomTypes", "SB,ZIPSB");

    List<OperaHotelDto> availableHotels =
        mapHotelToOperaHotelDto(Arrays.asList(hotel), roomTypesMap);

    assertThat(availableHotels).isNotEmpty().extracting(OperaHotelDto::getHotelCode)
        .containsExactly("TKINPT");
    assertThat(availableHotels).extracting(OperaHotelDto::getRates).isNotEmpty();
    assertThat(availableHotels.get(0).getRates()).hasSize(2);

    //ratePlan1
    assertThat(availableHotels.get(0).getRates().get(0).getRooms()).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(0)).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(1)).hasSize(2);

    //ratePlan2
    assertThat(availableHotels.get(0).getRates().get(1).getRooms()).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(0)).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(1)).hasSize(2);

  }

  @Test
  public void qtyRequestedShouldDifferForDifferentCategoryOfRoomTypesTest() {

    final Price price1 = new Price(new BigDecimal(100.00), "GBP");
    Room room1 = buildRoom(2, 1, "DB", price1, 1l);
    room1.setQuantityAvailable(10);

    final Price price2 = new Price(new BigDecimal(80.00), "GBP");
    Room room2 = buildRoom(2, 1, "DBLDBL", price2, 1l);
    room2.setQuantityAvailable(10);

    final Price price3 = new Price(new BigDecimal(70.00), "GBP");
    Room room3 = buildRoom(2, 1, "SB", price3, 1l);
    room3.setQuantityAvailable(10);

    final Price price4 = new Price(new BigDecimal(60.00), "GBP");
    Room room4 = buildRoom(2, 1, "ZIPSB", price4, 1l);
    room4.setQuantityAvailable(10);

    List<Room> rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    rooms.add(room3);
    rooms.add(room4);

    RatePlan ratePlan1 = buildRatePlan("A", "1", rooms);
    RatePlan ratePlan2 = buildRatePlan("B", "2", rooms);
    List<RatePlan> ratePlans = new ArrayList<>();
    ratePlans.add(ratePlan1);
    ratePlans.add(ratePlan2);

    final Hotel hotel =
        buildGivenHotel(
            "TKINPT", ratePlans, "Dummy-hotel1", PMS_SOURCE_OPERA);

    MultiValueMap<String, String> roomTypesMap = new LinkedMultiValueMap<>();
    roomTypesMap.add("roomTypes", "DB,DBLDBL");
    roomTypesMap.add("roomTypes", "SB,ZIPSB");
    roomTypesMap.add("roomQty", "1,2");

    List<OperaHotelDto> availableHotels =
        mapHotelToOperaHotelDto(Arrays.asList(hotel), roomTypesMap);

    assertThat(availableHotels).isNotEmpty().extracting(OperaHotelDto::getHotelCode)
        .containsExactly("TKINPT");
    assertThat(availableHotels).extracting(OperaHotelDto::getRates).isNotEmpty();
    assertThat(availableHotels.get(0).getRates()).hasSize(2);

    //rate1
    assertThat(availableHotels.get(0).getRates().get(0).getRooms()).hasSize(2);

    //1st list of rooms
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(0)).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(0)
        .get(0).getQtyRequested()).isEqualTo(1);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(0)
        .get(1).getQtyRequested()).isEqualTo(1);

    //2nd list of rooms
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(1)).hasSize(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(1)
        .get(0).getQtyRequested()).isEqualTo(2);
    assertThat(availableHotels.get(0).getRates().get(0).getRooms().get(1)
        .get(1).getQtyRequested()).isEqualTo(2);

    //rate2
    assertThat(availableHotels.get(0).getRates().get(1).getRooms()).hasSize(2);

    //1st list of rooms
    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(0)).hasSize(2);

    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(0)
        .get(0).getQtyRequested()).isEqualTo(1);
    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(0)
        .get(1).getQtyRequested()).isEqualTo(1);

    //2nd list of rooms
    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(1)).hasSize(2);

    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(1)
        .get(0).getQtyRequested()).isEqualTo(2);
    assertThat(availableHotels.get(0).getRates().get(1).getRooms().get(1)
        .get(1).getQtyRequested()).isEqualTo(2);

  }

  private Room buildRoom(
      final int adults, final int children, final String type, final Price price,
      final long quantity) {
    return Room.builder()
        .adults(adults)
        .children(children)
        .type(type)
        .totalPrice(price)
        .qtyRequested(quantity)
        .build();
  }

  private RatePlan buildRatePlan(
      final String classification, final String code, final List<Room> rooms) {
    return RatePlan.builder()
        .classification(classification)
        .code(code)
        .description("dummy rate plan")
        .rooms(rooms)
        .build();
  }

  private Hotel buildGivenHotel(
      final String hotelCode,
      final List<RatePlan> ratePlans, final String hotelName, final String pmsSource) {
    return Hotel.builder()
        .hotelCode(hotelCode)
        .rates(ratePlans)
        .available(true)
        .hotelName(hotelName)
        .pmsSource(pmsSource)
        .build();
  }

}
