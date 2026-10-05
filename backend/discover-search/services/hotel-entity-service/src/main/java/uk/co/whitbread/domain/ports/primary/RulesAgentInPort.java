package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;

public interface RulesAgentInPort {

  MaxNightsRuleResponse getMaxNightsRule(String channelId);

  MaxRoomsRuleResponse getMaxRoomsRule(String channelId);

  MaxRoomOccupancyResponse getMaxRoomOccupancyRule(
      String channelId);

  RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);

  RateSuppressionRuleResponse getRateSuppressionRule();

  RoomSubstitutionRuleResponse createRoomSubstitutionRule(
      String roomType, Integer adults, Integer children, String pmsRoomType, String channelId);
}
