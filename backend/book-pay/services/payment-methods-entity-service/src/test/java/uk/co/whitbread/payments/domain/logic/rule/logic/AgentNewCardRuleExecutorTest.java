package uk.co.whitbread.payments.domain.logic.rule.logic;


import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentNewCardRuleExecutorTest {
    @Test
    void givenRequest_PibaEuro_enabled() {
        // Arrange
        AgentNewCardRuleExecutor  agentNewCardRuleExecutor =
                new AgentNewCardRuleExecutor(
                        RuleDataTest.createRequestAgentNewCardPibaEuro(true));

        // Act
        var result = agentNewCardRuleExecutor.execute();

        // Assert
        assertTrue(result.isPresent());
        var paymentMethod = result.get().getPaymentMethods().get(0);
        assertTrue(paymentMethod.isEnabled());
    }

    @Test
    void givenRequest_PibaEuro_enabled_false() {
        // Arrange
        AgentNewCardRuleExecutor  agentNewCardRuleExecutor =
                new AgentNewCardRuleExecutor(
                        RuleDataTest.createRequestAgentNewCardPibaEuro(false));

        // Act
        var result = agentNewCardRuleExecutor.execute();

        // Assert
        assertTrue(result.isPresent());
        var paymentMethod = result.get().getPaymentMethods().get(0);
        assertFalse(paymentMethod.isEnabled());
    }

    //
    @Test
    void givenRequest_Piba_enabled() {
        // Arrange
        AgentNewCardRuleExecutor  agentNewCardRuleExecutor =
                new AgentNewCardRuleExecutor(
                        RuleDataTest.createRequestAgentNewCardPibaEuro(true));

        // Act
        var result = agentNewCardRuleExecutor.execute();

        // Assert
        assertTrue(result.isPresent());
        var paymentMethod = result.get().getPaymentMethods().get(0);
        assertTrue(paymentMethod.isEnabled());
    }

    @Test
    void givenRequest_Piba_enabled_false() {
        // Arrange
        AgentNewCardRuleExecutor  agentNewCardRuleExecutor =
                new AgentNewCardRuleExecutor(
                        RuleDataTest.createRequestAgentNewCardPibaEuro(false));

        // Act
        var result = agentNewCardRuleExecutor.execute();

        // Assert
        assertTrue(result.isPresent());
        var paymentMethod = result.get().getPaymentMethods().get(0);
        assertFalse(paymentMethod.isEnabled());
    }
}