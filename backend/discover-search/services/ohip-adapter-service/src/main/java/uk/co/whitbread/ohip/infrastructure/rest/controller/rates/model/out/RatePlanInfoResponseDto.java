package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlans;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RatePlanInfoResponseDto {

  private List<RatePlans> ratePlanInfo;
}

