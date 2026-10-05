package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class OccupancySupplementRequest extends ModelValidator<OccupancySupplementRequest> {

  @NotEmpty
  String hotelId;

  public OccupancySupplementRequest(String hotelId) {
    this.hotelId = hotelId;
    this.validateSelf();
  }
}
