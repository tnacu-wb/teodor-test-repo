package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomsSelectionsDto {

  private String reservationId;

  private List<PackagesSelectionDto> packagesSelection;
}
