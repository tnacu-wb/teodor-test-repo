package uk.co.whitbread.domain.logic;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@RequiredArgsConstructor
public class RulesAgentInPortImpl implements RulesAgentInPort {

  private final RulesAgentOutPort rulesAgentOutPort;

  @Override
  public MaxNightsRuleResponse getMaxNightsRule(String channelId) {
    return rulesAgentOutPort.getMaxNightsRule(channelId);
  }

  @Override
  public MaxRoomsRuleResponse getMaxRoomsRule(String channelId) {
    return rulesAgentOutPort.getMaxRoomsRule(channelId);
  }

  @Override
  public MaxRoomOccupancyResponse getMaxRoomOccupancyRule(
      String channelId) {
    return rulesAgentOutPort.getMaxRoomOccupancyRule(channelId);
  }

  @Override
  public RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest) {
    return rulesAgentOutPort.getRoomSubstitutionRule(roomSubstitutionRuleRequest);
  }

  @Override
  public RateSuppressionRuleResponse getRateSuppressionRule() {
    return rulesAgentOutPort.getRateSuppressionRule();
  }

  @Override
  public RoomSubstitutionRuleResponse createRoomSubstitutionRule(
      String roomType, Integer adults, Integer children, String pmsRoomType, String channelId) {
    return rulesAgentOutPort.createRoomSubstitutionRule(
        roomType, adults, children, pmsRoomType, channelId);
  }
}
