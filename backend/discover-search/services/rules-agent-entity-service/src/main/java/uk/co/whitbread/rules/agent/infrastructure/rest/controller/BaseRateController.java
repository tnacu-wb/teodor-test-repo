package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.BaseRateRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.BaseRateRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.BaseRateRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BaseRateRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class BaseRateController implements BaseRateRuleApiDocumentation {
  
  private final BaseRateRuleInPort baseRateRuleInPort;
  private final BaseRateRuleDtoMapper baseRateRuleDtoMapper;
  
  @GetMapping(value = "/baseRate", produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseRateRuleResponseDto getBaseRateRule(
      @Valid @ParameterObject String ratePlanCode) {
    
    var response = baseRateRuleInPort.getBaseRate(ratePlanCode);
    
    return baseRateRuleDtoMapper.toDto(response);
  }
}
