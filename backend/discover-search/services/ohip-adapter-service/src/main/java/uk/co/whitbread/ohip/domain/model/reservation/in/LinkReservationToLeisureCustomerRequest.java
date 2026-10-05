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
public class LinkReservationToLeisureCustomerRequest
    implements SelfValidation<LinkReservationToLeisureCustomerRequest> {

  @NotEmpty
  private Set<String> reservationIds;

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String customerAccountId;

  public LinkReservationToLeisureCustomerRequest(
      Set<String> reservationIds, String hotelId, String customerAccountId) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.customerAccountId = customerAccountId;
    this.validateSelf();
  }
}
