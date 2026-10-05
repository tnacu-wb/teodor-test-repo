package uk.co.whitbread.availabilitycacheservice.domain.model.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.availabilitycacheservice.domain.model.common.CommonRatePlan;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class DistributionRatePlan extends CommonRatePlan {

  private List<DistributionRoom> rooms;

}
