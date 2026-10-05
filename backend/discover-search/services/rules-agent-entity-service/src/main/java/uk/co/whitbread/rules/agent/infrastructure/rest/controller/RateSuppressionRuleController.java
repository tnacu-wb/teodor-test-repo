package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.RateSuppressionRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.RateSuppressionRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.RateSuppressionRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RateSuppressionRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class RateSuppressionRuleController implements RateSuppressionRuleApiDocumentation {

  private final RateSuppressionRuleDtoMapper rateSuppressionRuleDtoMapper;
  private final RateSuppressionRuleInPort rateSuppressionRuleInPort;

  @GetMapping(value = "/rate-suppressions", produces = MediaType.APPLICATION_JSON_VALUE)
  public RateSuppressionRuleResponseDto getRateSuppressionRule() {
    log.debug("Request to get Rate Suppression Rules");

    var domainRateSuppressionRuleResponse = rateSuppressionRuleInPort.getRateSuppressionRule();
    return rateSuppressionRuleDtoMapper.toDto(domainRateSuppressionRuleResponse);
  }
}
