package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.AmendmentRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.AmendmentRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.AmendmentRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.AmendmentRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class AmendmentRuleController implements AmendmentRuleApiDocumentation {

  private final AmendmentRuleInPort amendmentRuleInPort;
  private final AmendmentRuleDtoMapper amendmentRuleDtoMapper;

  @GetMapping(value = "/amendments", produces = MediaType.APPLICATION_JSON_VALUE)
  public AmendmentRuleResponseDto getAmendmentRule(
      @Valid @ParameterObject AmendmentRuleRequestDto amendmentRuleRequestDto) {

    var domainAmendmentRuleRequest = amendmentRuleDtoMapper.toModel(
        amendmentRuleRequestDto);
    var domainAmendmentRuleResponse = amendmentRuleInPort.getAmendmentRule(
        domainAmendmentRuleRequest);

    return amendmentRuleDtoMapper.toDto(domainAmendmentRuleResponse);
  }
}
