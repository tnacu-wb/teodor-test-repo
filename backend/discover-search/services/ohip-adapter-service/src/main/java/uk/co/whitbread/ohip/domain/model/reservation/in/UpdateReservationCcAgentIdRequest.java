package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UpdateReservationCcAgentIdRequest implements
    SelfValidation<UpdateReservationCcAgentIdRequest> {

  @NotEmpty
  private Set<String> reservationIds;

  @NotEmpty
  private String hotelId;

  private String ccAgentId;

  private boolean clearFirst;

  public UpdateReservationCcAgentIdRequest(Set<String> reservationIds, String hotelId,
      String ccAgentId, boolean clearFirst) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.ccAgentId = ccAgentId;
    this.clearFirst = clearFirst;
    this.validateSelf();
  }
}
