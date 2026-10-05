package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservationResponse {

  private List<UniqueIDType> reservationIdList;
  private ConfirmationRoomStay roomStay;
  private ConfirmationCustomer reservationGuest;
  private String hotelId;
  private String reservationStatus;
  private boolean isPartialPaid;
}
