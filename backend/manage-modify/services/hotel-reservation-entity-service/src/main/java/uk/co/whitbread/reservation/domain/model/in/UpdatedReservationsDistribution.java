package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatedReservationsDistribution {

  private List<UpdateReservationsRequest> updatedReservations;
}
