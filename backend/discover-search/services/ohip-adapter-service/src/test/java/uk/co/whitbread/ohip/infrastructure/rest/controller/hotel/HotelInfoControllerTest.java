package uk.co.whitbread.ohip.infrastructure.rest.controller.hotel;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypeInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.mapper.HotelInfoDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.mapper.RoomTypesInfoDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.HotelInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.RoomTypeInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.RoomTypesInfoDto;

@ExtendWith(MockitoExtension.class)
class HotelInfoControllerTest {

  @InjectMocks
  HotelInfoController hotelInfoController;

  @Mock
  private HotelInfoInPort hotelInfoInPort;

  @Mock
  private HotelInfoDtoMapper hotelInfoDtoMapper;

  @Mock
  private RoomTypesInfoDtoMapper roomTypesInfoDtoMapper;

  @Test
  void getHotelInfo_ShouldReturnOk() {
    //Arrange
    String hotelId = "FRAMTI";

    Mockito.when(hotelInfoInPort.getHotelInfo(hotelId))
        .thenReturn(getHotelInfo());
    Mockito.when(hotelInfoDtoMapper.toDto(getHotelInfo()))
        .thenReturn(getHotelInfoDto());

    //act
    final HotelInfoDto response = hotelInfoController.getHotelInfo(hotelId);

    //Assert
    Assertions.assertNotNull(response);
  }

  @Test
  void getRoomTypesInfo_ShouldReturnOk() {
    //Arrange
    String hotelId = "FRAMTI";

    Mockito.when(hotelInfoInPort.getRoomTypesInfo(hotelId))
        .thenReturn(getRoomTypesInfo());
    Mockito.when(roomTypesInfoDtoMapper.toDto(getRoomTypesInfo()))
        .thenReturn(getRoomTypesInfoDto());

    //act
    final RoomTypesInfoDto response = hotelInfoController.getRoomTypes(hotelId);

    //Assert
    Assertions.assertNotNull(response);
  }

  private RoomTypesInfoDto getRoomTypesInfoDto() {
    return RoomTypesInfoDto.builder()
        .hotelId("FRAMTI")
        .roomType(List.of(RoomTypeInfoDto.builder()
            .roomClass("ST")
            .accessible(false)
            .roomType("DOUBLE")
            .build()))
        .build();
  }

  private RoomTypesInfo getRoomTypesInfo() {
    return RoomTypesInfo.builder()
        .hotelId("FRAMTI")
        .roomType(List.of(RoomTypeInfo.builder()
            .roomClass("ST")
            .accessible(false)
            .roomType("DOUBLE")
            .build()))
        .build();
  }

  private HotelInfoDto getHotelInfoDto() {
    return HotelInfoDto.builder()
        .threeLetterId("threeLetterId")
        .hotelTimeZone("Europe/London")
        .hotelCountryCode("GB")
        .currencyCode("GBP")
        .languageCode("E")
        .checkInTime("01/01/1970, 15:00")
        .checkOutTime("01/01/1970, 12:00")
        .build();
  }

  private HotelInfo getHotelInfo() {
    return HotelInfo.builder()
        .threeLetterId("threeLetterId")
        .hotelTimeZone("Europe/London")
        .hotelCountryCode("GB")
        .currencyCode("GBP")
        .languageCode("E")
        .checkInTime("01/01/1970, 15:00")
        .checkOutTime("01/01/1970, 12:00")
        .build();
  }


}
