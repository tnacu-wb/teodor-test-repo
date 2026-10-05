package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomsScheduledSelectionsDto {

  private List<PackagesScheduledSelectionDto> packagesSelection;

}
