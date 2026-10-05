package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.logic.rule.logic.AgentNewCardRuleExecutor;
import uk.co.whitbread.payments.domain.logic.rule.logic.BusinessBookerSavedCardRuleExecutor;
import uk.co.whitbread.payments.domain.logic.rule.logic.LeisureNewCardRuleExecutor;
import uk.co.whitbread.payments.domain.logic.rule.logic.LeisureSavedCardRuleExecutor;
import uk.co.whitbread.payments.domain.logic.rule.logic.RuleExecutor;
import uk.co.whitbread.payments.domain.model.in.UserType;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class RuleEngineSelector {

  private RuleEngineSelector() {
  }

  public static RuleExecutor selectCcuiExecutor(RuleData data) {
    return new AgentNewCardRuleExecutor(data);
  }

  public static RuleExecutor selectExecutor(RuleData data) {
    if (UserType.BUSINESS.equals(data.getUserType())) {
      return new BusinessBookerSavedCardRuleExecutor(data);
    } else if (isUserHavingSavedCards(data)) {
      return new LeisureSavedCardRuleExecutor(data);
    } else {
      return new LeisureNewCardRuleExecutor(data);
    }
  }

  private static boolean isUserHavingSavedCards(RuleData request) {
    return request.getPaymentMethods()
        .stream()
        .anyMatch(pm -> SAVED_CARD.name().equals(pm.getType()));
  }
}
