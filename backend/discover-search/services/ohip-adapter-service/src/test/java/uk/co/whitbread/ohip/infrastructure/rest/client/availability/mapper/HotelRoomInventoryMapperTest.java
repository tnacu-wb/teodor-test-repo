package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelRoomInventoryMapperImpl.class)
class HotelRoomInventoryMapperTest {

  @Autowired
  private HotelRoomInventoryMapper hotelRoomInventoryMapper;

  @Test
  void toHotelInventoryRequestDto() {
    //Act
    var hotelInventoryRequestDto = hotelRoomInventoryMapper
        .toDto(createHotelInventoryRequest());

    //Assert
    assertThat(hotelInventoryRequestDto.getHotelId(), is("MANOLD"));
    assertThat(hotelInventoryRequestDto.getDateRangeStart(), is("2022-10-28"));
    assertThat(hotelInventoryRequestDto.getDateRangeEnd(), is("2022-10-30"));

  }

  private HotelInventoryRequest createHotelInventoryRequest() {
    return HotelInventoryRequest.builder().hotelId("MANOLD").dateRangeStart("2022-10-28")
        .dateRangeEnd("2022-10-30").build();
  }
}
