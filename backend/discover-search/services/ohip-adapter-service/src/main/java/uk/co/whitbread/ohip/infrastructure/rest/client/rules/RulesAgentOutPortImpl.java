package uk.co.whitbread.ohip.infrastructure.rest.client.rules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.rules.model.out.BaseRateRuleResponse;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.mapper.BaseRateResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.mapper.RoomSubstitutionResponseMapper;

@RequiredArgsConstructor
@Slf4j
public class RulesAgentOutPortImpl implements RulesAgentOutPort {

  private final RoomSubstitutionResponseMapper roomSubstitutionResponseMapper;
  private final BaseRateResponseMapper baseRateResponseMapper;
  private final RulesAgentClient rulesAgentClient;

  @Override
  public RoomSubstitutionRuleResponse getRoomSubstitution(String roomType, Integer adultsNumber,
                                                          Integer childrenNumber, String channel) {
    log.debug("Entered getRoomSubstitution with roomType={}, adultsNumber={}, childrenNumber={}, channel={}",
        roomType, adultsNumber, childrenNumber, channel);
    return roomSubstitutionResponseMapper.toRoomSobstitutionModel(rulesAgentClient.getRoomSubstitution(roomType,
            adultsNumber, childrenNumber, channel));
  }
  
  public BaseRateRuleResponse getBaseRate(String ratePlanCode) {
    log.debug("Entered getBaseRate with ratePlanCode={}", ratePlanCode);
    return baseRateResponseMapper.toModel(rulesAgentClient.getBaseRate(ratePlanCode));
  }
}
