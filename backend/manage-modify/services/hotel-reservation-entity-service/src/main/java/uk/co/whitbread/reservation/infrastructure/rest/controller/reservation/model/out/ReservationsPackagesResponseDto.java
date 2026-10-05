package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationsPackagesResponseDto {

  private List<RoomsSelectionsDto> roomsSelections;
  private List<RoomsSelectionsDto> roomsSelectionsAmendExtras;
  private String ratePlanCode;
}
