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
public class BusinessItemsRequest implements SelfValidation<BusinessItemsRequest> {

  @NotEmpty
  private Set<String> reservationIds;

  @NotEmpty
  private String hotelId;

  private String companyId;
  private BusinessItems businessItems;
  private String channel;
  private Boolean pibaCardPresent;

  public BusinessItemsRequest(Set<String> reservationIds, String hotelId, String companyId,
      BusinessItems businessItems, String channel, Boolean pibaCardPresent) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.companyId = companyId;
    this.businessItems = businessItems;
    this.channel = channel;
    this.pibaCardPresent = pibaCardPresent;
    this.validateSelf();
  }
}
