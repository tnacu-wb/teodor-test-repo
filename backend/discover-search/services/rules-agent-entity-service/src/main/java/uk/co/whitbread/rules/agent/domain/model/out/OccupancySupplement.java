package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OccupancySupplement extends Rule {

  @NotEmpty
  String hotelId;
  @NotNull
  BigDecimal pricing;

  private OccupancySupplement(final OccupancySupplement.OccupancySupplementBuilder<?, ?> b) {
    super(b);
    this.hotelId = b.hotelId;
    this.pricing = b.pricing;
    this.validateSelf();
  }
}
