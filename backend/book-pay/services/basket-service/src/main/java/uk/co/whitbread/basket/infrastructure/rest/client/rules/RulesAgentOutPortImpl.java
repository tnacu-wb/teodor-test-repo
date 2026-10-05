package uk.co.whitbread.basket.infrastructure.rest.client.rules;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.domain.model.rules.out.VatRuleResponse;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper.BusinessAllowanceRuleResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper.VatRuleResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.service.RulesAgentClient;

@Slf4j
@RequiredArgsConstructor
public class RulesAgentOutPortImpl implements RulesAgentOutPort {

  private final RulesAgentClient rulesAgentClient;
  private final BusinessAllowanceRuleResponseMapper businessAllowanceRuleResponseMapper;
  private final VatRuleResponseMapper vatRuleResponseMapper;

  @Override
  public BusinessAllowanceRuleResponse getBusinessAllowanceRules() {
    log.info("Entered getBusinessAllowanceRules");
    var businessAllowanceRulesResponse = rulesAgentClient.getBusinessAllowances();
    return businessAllowanceRuleResponseMapper.toModel(businessAllowanceRulesResponse);
  }

  @Override
  public VatRuleResponse getVatCodes(String vatRegion, List<String> pkgCodeArr) {
    log.info("Entered getVatCodes for vatRegion={} pkgCodeArr={}", vatRegion, pkgCodeArr);
    var vatRuleResponseDto = rulesAgentClient.getVatCodes(vatRegion, pkgCodeArr);
    return vatRuleResponseMapper.toModel(vatRuleResponseDto);
  }
}
