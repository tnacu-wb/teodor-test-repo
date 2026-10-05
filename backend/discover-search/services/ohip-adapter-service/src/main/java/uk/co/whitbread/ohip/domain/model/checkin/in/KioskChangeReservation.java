package uk.co.whitbread.ohip.domain.model.checkin.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class KioskChangeReservation {

  private List<KioskReservation> reservations;

}
