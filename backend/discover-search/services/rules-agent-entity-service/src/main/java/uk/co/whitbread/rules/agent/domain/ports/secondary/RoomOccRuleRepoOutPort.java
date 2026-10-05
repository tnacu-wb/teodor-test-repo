package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;

public interface RoomOccRuleRepoOutPort extends RuleEngineRepositoryOutPort<MaxRoomOccupancyRule> {

  List<MaxRoomOccupancyRule> findRules(String channelId, String brand);

}
