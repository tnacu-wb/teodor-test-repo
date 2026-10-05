package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomLevelInventory;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.HotelInventoryRequestDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelInventoryMapperImpl.class)
class HotelInventoryMapperTest {

  @Autowired
  HotelInventoryMapper hotelInventoryMapper;

  @Test
  void toDomainModel__ShouldReturnOK() {
    // Arrange
    HotelInventoryRequestDto hotelInventoryRequestDto = HotelInventoryRequestDto.builder()
        .dateRangeStart("2022-10-28").dateRangeEnd("2022-10-30").build();

    // Act
    var hotelInventoryRequest = hotelInventoryMapper.toDomainModel("MANOLD",
        hotelInventoryRequestDto);

    // Assert
    assertThat(hotelInventoryRequest.getHotelId(), is("MANOLD"));
    assertThat(hotelInventoryRequest.getDateRangeStart(), is("2022-10-28"));
    assertThat(hotelInventoryRequest.getDateRangeEnd(), is("2022-10-30"));

  }

  @Test
  void toDto__ShouldReturnOK() {
    // Arrange
    HotelInventoryRoomType hotelInventoryRoomType = createRoomInventories();

    // Act
    var hotelInventoryRoomTypeDto = hotelInventoryMapper.toDto(hotelInventoryRoomType);

    // Assert
    assertThat(hotelInventoryRoomTypeDto.getRoomTypeInventories(), notNullValue());
    assertThat(hotelInventoryRoomTypeDto.getRoomTypeInventories(), hasSize(2));
    assertThat(hotelInventoryRoomTypeDto.getRoomTypeInventories().get(0).getCode(), is("DOUBLE"));
    assertThat(hotelInventoryRoomTypeDto.getRoomTypeInventories().get(0).getAvailableCount(), is(42));
    assertThat(hotelInventoryRoomTypeDto.getRoomTypeInventories().get(1).getCode(), is("FMTRPL"));
    assertThat(hotelInventoryRoomTypeDto.getRoomTypeInventories().get(1).getAvailableCount(), is(10));

  }

  private HotelInventoryRoomType createRoomInventories() {
    RoomLevelInventory roomLevelInventoryDB = RoomLevelInventory.builder()
        .availableCount(42)
        .code("DOUBLE")
        .build();

    RoomLevelInventory roomLevelInventoryFAM = RoomLevelInventory.builder()
        .availableCount(10)
        .code("FMTRPL")
        .build();

    return HotelInventoryRoomType.builder()
        .roomTypeInventories(Arrays.asList(roomLevelInventoryDB, roomLevelInventoryFAM)).build();

  }
}
