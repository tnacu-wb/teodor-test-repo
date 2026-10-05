package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCancellationPoliciesRequestDto {

  @NotNull
  private String hotelId;

  @NotNull
  private List<String> reservationIds;

  @NotNull
  private String absoluteDeadline;
}
