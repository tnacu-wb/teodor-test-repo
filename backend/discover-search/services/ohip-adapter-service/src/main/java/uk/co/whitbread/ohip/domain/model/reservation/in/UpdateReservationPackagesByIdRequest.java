package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateReservationPackagesByIdRequest {

  private String basketReference;

  private String hotelId;

  private String arrival;

  private String departure;

  private List<RoomsSelectionsByReservationId> roomsSelections;

  private List<RoomsSelectionsByReservationId> previousRoomsSelections;

}