package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmReservationResponseDto {

  private List<UniqueIdTypeDto> reservationIdList;
  private ConfirmationRoomStayDto roomStay;
  private ConfirmationCustomerDto reservationGuest;
  private String hotelId;
  private String reservationStatus;
}
