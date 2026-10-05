package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ConfirmReservationResponse {

  private List<UniqueIdType> reservationIdList;
  private ConfirmationRoomStay roomStay;
  private ConfirmationCustomer reservationGuest;
  private String hotelId;
  private String reservationStatus;
}
