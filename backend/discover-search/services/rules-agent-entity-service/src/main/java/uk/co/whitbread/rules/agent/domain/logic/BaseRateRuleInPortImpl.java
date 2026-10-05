package uk.co.whitbread.rules.agent.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.BaseRateRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BaseRateRuleRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class BaseRateRuleInPortImpl implements BaseRateRuleInPort {
  
  private final BaseRateRuleRepositoryOutPort baseRateRuleRepositoryOutPort;
  
  @Override
  public BaseRateRuleResponse getBaseRate(String ratePlanCode) {
    var baseRateRule = baseRateRuleRepositoryOutPort.getBaseRate(ratePlanCode);
    
    if (baseRateRule.isEmpty()) {
      var message = "Error while trying to get base rate rule.";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_NO_BASE_RATE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return BaseRateRuleResponse.builder()
        .promoCode(baseRateRule.get().getPromoCode())
        .ratePlanCode(baseRateRule.get().getRatePlanCode())
        .baseRate(baseRateRule.get().getBaseRate())
        .build();
  }
}
