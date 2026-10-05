package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCancellationPoliciesRequest implements
    SelfValidation<UpdateCancellationPoliciesRequest> {

  @NotNull
  private String hotelId;

  @NotNull
  private List<String> reservationIds;

  @NotNull
  private String absoluteDeadline;
}
