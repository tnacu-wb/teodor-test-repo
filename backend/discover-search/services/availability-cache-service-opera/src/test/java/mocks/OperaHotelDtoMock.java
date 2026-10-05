package mocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaRatePlanDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaRoomDto;


public class OperaHotelDtoMock {

  public static List<OperaHotelDto> buildAllOperaHotelDtos() {
    List<OperaHotelDto> operaHotelDtoList = new ArrayList<>();
    List<OperaRoomDto> operaRoomDtoList = new ArrayList<>();
    List<OperaRatePlanDto> operaRatePlanDtoList = new ArrayList<>();
    List<OperaRatePlanDto> emptyRatePlans = Collections.emptyList();

    operaRoomDtoList.add(mockOperaRoomDto("DB"));
    operaRoomDtoList.add(mockOperaRoomDto("S"));

    operaRatePlanDtoList.add(mockRatePlanDto("A", operaRoomDtoList));
    operaRatePlanDtoList.add(mockRatePlanDto("F", operaRoomDtoList));
    operaRatePlanDtoList.add(mockRatePlanDto("S", operaRoomDtoList));
    operaRatePlanDtoList.add(mockRatePlanDto("A", operaRoomDtoList));
    operaRatePlanDtoList.add(mockRatePlanDto("A", operaRoomDtoList));

    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI mocked hotel Codes
    operaHotelDtoList.add(mockHotelDto("OXFORD", operaRatePlanDtoList));
    operaHotelDtoList.add(mockHotelDto("LONSLA", emptyRatePlans));
    return operaHotelDtoList;
  }

  private static OperaHotelDto mockHotelDto(String hotelCode, List<OperaRatePlanDto> rates) {
    OperaHotelDto hotelDto = new OperaHotelDto();
    hotelDto.setHotelCode(hotelCode);
    hotelDto.setRates(rates);
    return hotelDto;
  }

  private static OperaRatePlanDto mockRatePlanDto(String classification, List<OperaRoomDto> rooms) {
    OperaRatePlanDto ratePlanDto = new OperaRatePlanDto();
    ratePlanDto.setClassification(classification);
    //ratePlanDto.setRooms(rooms);
    return ratePlanDto;
  }

  private static OperaRoomDto mockOperaRoomDto(String type) {
    OperaRoomDto roomDto = new OperaRoomDto();
    roomDto.setType(type);
    return roomDto;
  }
}
