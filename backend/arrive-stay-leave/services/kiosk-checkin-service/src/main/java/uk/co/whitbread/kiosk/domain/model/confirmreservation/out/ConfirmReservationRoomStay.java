package uk.co.whitbread.kiosk.domain.model.confirmreservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservationRoomStay {

  private String arrivalDate;
  private String departureDate;

}
