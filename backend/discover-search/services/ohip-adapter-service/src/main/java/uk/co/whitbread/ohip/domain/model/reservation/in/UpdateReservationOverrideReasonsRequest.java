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
public class UpdateReservationOverrideReasonsRequest implements
    SelfValidation<UpdateReservationOverrideReasonsRequest> {

  @NotEmpty
  private Set<String> reservationIds;

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String reasonCode;

  @NotEmpty
  private String reasonName;

  @NotEmpty
  private String callerName;

  private String managerName;

  public UpdateReservationOverrideReasonsRequest(Set<String> reservationIds, String hotelId,
      String reasonCode, String reasonName,
      String callerName, String managerName) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.reasonCode = reasonCode;
    this.reasonName = reasonName;
    this.callerName = callerName;
    this.managerName = managerName;
    this.validateSelf();
  }

}
