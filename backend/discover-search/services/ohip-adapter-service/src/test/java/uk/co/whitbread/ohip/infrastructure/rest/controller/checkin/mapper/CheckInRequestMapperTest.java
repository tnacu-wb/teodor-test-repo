package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CheckInDetailsDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CheckInRequestMapperImpl.class)
class CheckInRequestMapperTest {

  @Autowired
  CheckInRequestMapper checkInRequestMapper;

  @Test
  void toCheckInInputRequestModel__ShouldReturnOK() {
    //Act
    var checkInRequest = checkInRequestMapper.toCheckInInputRequestModel(createCheckInDetailsDto());

    //Assert
    assertEquals("ReservationDetail", checkInRequest.getFetchReservationInstruction().get(0));
    assertEquals(true, checkInRequest.getReservation().isIgnoreWarnings());
    assertEquals(true, checkInRequest.getReservation().isOverrideAdvancePaymentValidation());
    assertEquals("ROOM_ID", checkInRequest.getReservation().getRoomId());

  }

  private CheckInDetailsDto createCheckInDetailsDto() {
    var requestDto = new CheckInDetailsDto();
    requestDto.setHotelId("HOTEL_ID");
    requestDto.setReservationNumber("RESERVATION_NUMBER");
    requestDto.setRoomId("ROOM_ID");
    return requestDto;
  }
}
