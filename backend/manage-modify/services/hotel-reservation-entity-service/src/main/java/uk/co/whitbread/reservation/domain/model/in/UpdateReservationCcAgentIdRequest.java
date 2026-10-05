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
public class UpdateReservationCcAgentIdRequest implements
    SelfValidation<UpdateReservationCcAgentIdRequest> {

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String ccAgentId;

  private Set<String> reservationIds;

  private boolean clearFirst;
}
