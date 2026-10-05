package uk.co.whitbread.promo.domain.model.promobatch.in;

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
public class BatchEligibility {

  private Region region;
  private Channel channel;
  private List<Platform> platforms;
}
