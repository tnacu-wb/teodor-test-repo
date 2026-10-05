package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.RulesAgentClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleRequestMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleResponseMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class RulesAgentOutPortImpl implements RulesAgentOutPort {

  private final RulesAgentClient rulesAgentClient;
  private final RoomSubstitutionRuleRequestMapper roomSubstitutionRuleRequestMapper;
  private final RoomSubstitutionRuleResponseMapper roomSubstitutionRuleResponseMapper;

  @Override
  public RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest) {
    log.debug(
        "Entered getRoomSubstitutionRule with adults={}, children={}, roomType={}, pms={}, channel={}",
        roomSubstitutionRuleRequest.getAdults(), roomSubstitutionRuleRequest.getChildren(),
        roomSubstitutionRuleRequest.getRoomType(), roomSubstitutionRuleRequest.getPms(),
        roomSubstitutionRuleRequest.getChannel());
    var roomSubstitutionRuleRequestDto = roomSubstitutionRuleRequestMapper.toDto(
        roomSubstitutionRuleRequest);
    var roomSubstitutionRuleResponseDto = rulesAgentClient.getSubstitutionRoomRules(
        roomSubstitutionRuleRequestDto);
    return roomSubstitutionRuleResponseMapper.toModel(roomSubstitutionRuleResponseDto);
  }

}
