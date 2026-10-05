package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplement;


public interface OccupancySupplementRepositoryOutPort extends RuleEngineRepositoryOutPort<OccupancySupplement> {

  Optional<OccupancySupplement> findRule(String hotelId);
}
