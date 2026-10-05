package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCancellationPoliciesRequest {

  @NotNull
  private String hotelId;

  @NotNull
  private List<String> reservationIds;

  @NotNull
  private String absoluteDeadline;
}
