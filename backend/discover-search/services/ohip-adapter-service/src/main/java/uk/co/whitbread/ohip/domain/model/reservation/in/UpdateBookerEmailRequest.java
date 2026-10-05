package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class UpdateBookerEmailRequest implements SelfValidation<UpdateBookerEmailRequest> {
  @NotEmpty
  private Set<String> reservationIds;
  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String emailAddress;

  public UpdateBookerEmailRequest(Set<String> reservationIds, String hotelId, String emailAddress) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.emailAddress = emailAddress;
    this.validateSelf();
  }
}
