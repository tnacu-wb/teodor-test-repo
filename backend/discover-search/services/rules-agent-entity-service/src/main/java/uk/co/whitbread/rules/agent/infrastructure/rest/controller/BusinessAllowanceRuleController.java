package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.BusinessAllowanceRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.BusinessAllowanceRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.BusinessAllowanceRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BusinessAllowanceRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class BusinessAllowanceRuleController implements BusinessAllowanceRuleApiDocumentation {

  private final BusinessAllowanceRuleDtoMapper businessAllowanceRuleDtoMapper;

  private final BusinessAllowanceRuleInPort businessAllowanceRuleInPort;

  @GetMapping(value = "/allowances", produces = MediaType.APPLICATION_JSON_VALUE)
  public BusinessAllowanceRuleResponseDto getBusinessAllowanceRules() {
    log.debug("Request to get the business allowances");

    var response = businessAllowanceRuleInPort.getBusinessAllowanceRules();

    return businessAllowanceRuleDtoMapper.toDto(response);
  }
}
