package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomsSelectionsDto {

  private List<PackagesSelectionDto> packagesSelection;
}
