package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class OccupancySupplementResponse extends ModelValidator<OccupancySupplementResponse> {

  @NotEmpty
  String hotelId;
  @NotNull
  BigDecimal pricing;

  public OccupancySupplementResponse(String hotelId, BigDecimal pricing) {
    this.hotelId = hotelId;
    this.pricing = pricing;
    this.validateSelf();
  }
}
