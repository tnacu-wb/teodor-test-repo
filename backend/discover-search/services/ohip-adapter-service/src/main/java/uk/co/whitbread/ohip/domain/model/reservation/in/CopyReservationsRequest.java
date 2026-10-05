package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CopyReservationsRequest implements SelfValidation<CopyReservationsRequest> {

  @NotNull
  private String externalReferenceId;
  @NotNull
  private String hotelId;
  @NotEmpty
  private Set<String> reservationIds;

  public CopyReservationsRequest(String externalReferenceId, String hotelId,
      Set<String> reservationIds) {
    this.externalReferenceId = externalReferenceId;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
    this.validateSelf();
  }
}
