package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CopyReservationsRequest implements SelfValidation<CopyReservationsRequest> {

  @NotNull
  private String externalReferenceId;
  @NotNull
  private String hotelId;
  @NotEmpty
  private Set<String> reservationIds;
}
