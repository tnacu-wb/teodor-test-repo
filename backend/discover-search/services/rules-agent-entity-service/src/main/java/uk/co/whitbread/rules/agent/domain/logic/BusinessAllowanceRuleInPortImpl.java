package uk.co.whitbread.rules.agent.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.BusinessAllowanceRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BusinessAllowanceRuleRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class BusinessAllowanceRuleInPortImpl implements BusinessAllowanceRuleInPort {

  private final BusinessAllowanceRuleRepositoryOutPort businessAllowanceRuleRepository;

  @Override
  public BusinessAllowanceRuleResponse getBusinessAllowanceRules() {
    return BusinessAllowanceRuleResponse.builder()
        .businessAllowances(businessAllowanceRuleRepository.findRules()).build();
  }
}
