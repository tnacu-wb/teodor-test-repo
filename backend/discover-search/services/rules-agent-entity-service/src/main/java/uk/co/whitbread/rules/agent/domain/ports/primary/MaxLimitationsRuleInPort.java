package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.in.MaxArrivalDateRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxNightsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomOccupancyRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRuleResponse;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccuRuleResp;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRuleResponse;

public interface MaxLimitationsRuleInPort {

  MaxNightsRuleResponse getMaxNightsRule(MaxNightsRuleRequest
      maxNightsRuleRequest);

  MaxRoomsRuleResponse getMaxRoomsRule(MaxRoomsRuleRequest
      maxRoomsRuleRequest);

  MaxRoomOccuRuleResp getMaxRoomOccupancyRule(
      MaxRoomOccupancyRequest maxRoomOccupancyRequest);

  MaxArrivalDateRuleResponse getMaxArrivalDateRule(
      MaxArrivalDateRuleRequest maxArrivalDateRuleRequest);
}
