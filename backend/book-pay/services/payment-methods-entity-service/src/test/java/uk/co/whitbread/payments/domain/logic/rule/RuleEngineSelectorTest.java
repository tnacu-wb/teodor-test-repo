package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.logic.rule.logic.AgentNewCardRuleExecutor;
import uk.co.whitbread.payments.domain.logic.rule.logic.BusinessBookerSavedCardRuleExecutor;
import uk.co.whitbread.payments.domain.model.in.UserType;
import uk.co.whitbread.payments.domain.model.out.RuleData;


class RuleEngineSelectorTest {

  @Test
  void givenBusinessUser_returnBusinessBookerSavedCardRuleExecutor() {
    //Arrange
    RuleData request = RuleData.builder()
        .userType(UserType.BUSINESS)
        .build();

    //Act
    var ruleSelector = RuleEngineSelector.selectExecutor(request);

    //Assert
    assertEquals(ruleSelector.getClass(), BusinessBookerSavedCardRuleExecutor.class);
  }

  @Test
  void givenAgentUser_returnAgentCardRuleExecutor() {
    //Arrange
    RuleData request = RuleData.builder()
        .userType(UserType.AGENT)
        .build();

    //Act
    var ruleSelector = RuleEngineSelector.selectCcuiExecutor(request);

    //Assert
    assertEquals(ruleSelector.getClass(), AgentNewCardRuleExecutor.class);
  }

}