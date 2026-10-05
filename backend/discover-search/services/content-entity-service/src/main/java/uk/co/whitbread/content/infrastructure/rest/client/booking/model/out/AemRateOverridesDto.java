package uk.co.whitbread.content.infrastructure.rest.client.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AemRateOverridesDto {

  private List<RateOverrideDetailsDto> rateOverrides;
}
