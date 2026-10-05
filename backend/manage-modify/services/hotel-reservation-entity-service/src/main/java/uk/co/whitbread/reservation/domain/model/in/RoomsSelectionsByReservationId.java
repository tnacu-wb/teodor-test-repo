package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomsSelectionsByReservationId {

  private String reservationId;
  private List<PackagesSelection> packagesSelection;
}
