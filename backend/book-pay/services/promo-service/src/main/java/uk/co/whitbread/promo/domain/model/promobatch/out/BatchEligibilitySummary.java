package uk.co.whitbread.promo.domain.model.promobatch.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEligibilitySummary {

  private Region region;
  private Channel channel;
  private List<Platform> platforms;
}
