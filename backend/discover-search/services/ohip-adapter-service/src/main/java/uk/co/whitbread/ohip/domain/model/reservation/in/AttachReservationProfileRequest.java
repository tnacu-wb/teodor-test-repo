package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class AttachReservationProfileRequest implements SelfValidation<AttachReservationProfileRequest> {

  @NotEmpty
  private Set<String> reservationIds;

  @NotEmpty
  private String hotelId;
  @NotNull
  private String profileId;

  public AttachReservationProfileRequest(Set<String> reservationIds, String hotelId,
      String profileId) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.profileId = profileId;
    this.validateSelf();
  }
}
