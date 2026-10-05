package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomsSelectionsDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsPackagesResponseDto {

  private List<RoomsSelectionsDto> roomsSelections;
  private String ratePlanCode;
}
