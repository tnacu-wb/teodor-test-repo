package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.RoomSubstitutionRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.RoomSubstitutionRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.RoomSubstitutionRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class RoomSubstitutionRuleController implements RoomSubstitutionRuleApiDocumentation {

  private final RoomSubstitutionRuleInPort roomSubstitutionRuleInPort;
  private final RoomSubstitutionRuleDtoMapper roomSubstitutionRuleDtoMapper;

  @GetMapping(value = "/room-substitutions", produces = MediaType.APPLICATION_JSON_VALUE)
  public RoomSubstitutionRuleResponseDto getRoomSubstitutionRule(
      @Valid @ParameterObject RoomSubstitutionRuleRequestDto roomSubstitutionRuleRequestDto) {
    var domainRoomSubstitutionRuleRequest = roomSubstitutionRuleDtoMapper.toModel(
        roomSubstitutionRuleRequestDto);
    var domainRoomSubstitutionRuleResponse = roomSubstitutionRuleInPort.getRoomSubstitutionRule(
        domainRoomSubstitutionRuleRequest);

    return roomSubstitutionRuleDtoMapper.toDto(domainRoomSubstitutionRuleResponse);
  }
}
