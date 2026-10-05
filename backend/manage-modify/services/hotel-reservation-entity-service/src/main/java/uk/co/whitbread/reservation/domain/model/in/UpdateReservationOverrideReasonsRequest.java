package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class UpdateReservationOverrideReasonsRequest implements
    SelfValidation<UpdateReservationOverrideReasonsRequest> {

  @NotEmpty
  private String basketReference;

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String reasonCode;

  @NotEmpty
  private String reasonName;

  @NotEmpty
  private String callerName;

  private String managerName;

  private Set<String> reservationIds;

}
