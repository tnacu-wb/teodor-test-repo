package uk.co.whitbread.domain.ports.secondary;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;

public interface RulesAgentOutPort {

  MaxNightsRuleResponse getMaxNightsRule(String channelId);

  MaxRoomsRuleResponse getMaxRoomsRule(String channelId);

  MaxRoomOccupancyResponse getMaxRoomOccupancyRule(
      String channelId);

  RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest);

  RateSuppressionRuleResponse getRateSuppressionRule();

  Map<String, BigDecimal> getMultiOccupancySupplementPricing(List<String> hotelIds);

  RoomSubstitutionRuleResponse createRoomSubstitutionRule(
      String roomType, Integer adults, Integer children, String pmsRoomType, String channelId);
}
