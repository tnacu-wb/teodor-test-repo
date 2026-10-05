package uk.co.whitbread.basket.domain.model.basket.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ConfirmReservationResponse {

  private String hotelId;
  private String reservationStatus;

}
