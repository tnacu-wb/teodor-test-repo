package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.in.OccupancySupplementRequest;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplementResponse;

public interface OccupancySupplementInPort {

  OccupancySupplementResponse getOccupancySupplementPricing(OccupancySupplementRequest occupancySupplementRequest);
}
