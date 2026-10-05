package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomOccupancyDto {
  @NotNull
  private Integer adultsNumber;
  @NotNull
  private Integer childrenNumber;

  private Boolean cotRequired;
}
