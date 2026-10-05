package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsPackagesResponse {

  private List<RoomsSelectionsByReservation> roomsSelections;
  private List<RoomsSelectionsByReservation> roomsSelectionsAmendExtras;
  private String ratePlanCode;
}
