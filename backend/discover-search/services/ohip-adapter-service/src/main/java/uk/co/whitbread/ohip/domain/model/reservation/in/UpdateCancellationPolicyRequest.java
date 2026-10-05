package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class UpdateCancellationPolicyRequest implements
    SelfValidation<UpdateCancellationPolicyRequest> {

  private String hotelId;
  private String reservationId;
  private Date absoluteDeadline;

  public UpdateCancellationPolicyRequest(String hotelId, String reservationId,
      Date absoluteDeadline) {
    this.hotelId = hotelId;
    this.reservationId = reservationId;
    this.absoluteDeadline = absoluteDeadline;
    this.validateSelf();
  }
}
