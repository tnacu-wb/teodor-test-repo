package mocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.RatePlanDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.RoomDto;


public class HotelDtoMock {

  public static List<HotelDto> buildAllHotelDtos() {
    List<HotelDto> hotelDtoList = new ArrayList<>();
    List<RoomDto> roomList = new ArrayList<>();
    List<RatePlanDto> ratePlans = new ArrayList<>();
    List<RatePlanDto> emptyRatePlans = Collections.emptyList();

    roomList.add(mockRoomDto("DB"));
    roomList.add(mockRoomDto("S"));

    ratePlans.add(mockRatePlanDto("A", roomList));
    ratePlans.add(mockRatePlanDto("F", roomList));
    ratePlans.add(mockRatePlanDto("S", roomList));
    ratePlans.add(mockRatePlanDto("A", roomList));
    ratePlans.add(mockRatePlanDto("A", roomList));

    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI mocked hotel Codes
    hotelDtoList.add(mockHotelDto("PLYPTI", ratePlans));
    hotelDtoList.add(mockHotelDto("PLYLOC", ratePlans));
    hotelDtoList.add(mockHotelDto("PLYMAR", emptyRatePlans));
    hotelDtoList.add(mockHotelDto("LISBAR", emptyRatePlans));
    hotelDtoList.add(mockHotelDto("PAIWHI", emptyRatePlans));
    return hotelDtoList;
  }

  private static HotelDto mockHotelDto(String hotelCode, List<RatePlanDto> rates) {
    HotelDto hotelDto = new HotelDto();
    hotelDto.setHotelCode(hotelCode);
    hotelDto.setRates(rates);
    return hotelDto;
  }

  private static RatePlanDto mockRatePlanDto(String classification, List<RoomDto> rooms) {
    RatePlanDto ratePlanDto = new RatePlanDto();
    ratePlanDto.setClassification(classification);
    ratePlanDto.setRooms(rooms);
    return ratePlanDto;
  }

  private static RoomDto mockRoomDto(String type) {
    RoomDto roomDto = new RoomDto();
    roomDto.setType(type);
    return roomDto;
  }
}
