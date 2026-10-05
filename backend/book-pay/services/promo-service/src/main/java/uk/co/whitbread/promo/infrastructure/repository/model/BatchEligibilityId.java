package uk.co.whitbread.promo.infrastructure.repository.model;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchEligibilityId implements Serializable {

  private UUID batchId;
  private Region region;
  private Channel channel;
  private Platform platform;
}
