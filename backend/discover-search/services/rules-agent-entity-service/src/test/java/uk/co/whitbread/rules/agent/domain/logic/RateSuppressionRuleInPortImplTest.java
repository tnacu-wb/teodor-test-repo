package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RateSuppressionRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class RateSuppressionRuleInPortImplTest {

  @InjectMocks
  private RateSuppressionRuleInPortImpl rateSuppressionRuleInPort;

  @Mock
  private RateSuppressionRuleRepositoryOutPort rateSuppressionRuleRepositoryOutPort;

  @Test
  void getRateSuppressionRule__shouldThrowException() {
    //Arrange
    final String expectedMessage = "Rate Suppression Rules not found.";
    when(rateSuppressionRuleRepositoryOutPort.findRateSuppressionRule()).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_RATE_SUPPRESSION_RULE_EXCEPTION,
              "Rate Suppression Rules not found."));

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> rateSuppressionRuleInPort.getRateSuppressionRule());

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
    verifyNoMoreInteractions(rateSuppressionRuleRepositoryOutPort);
  }


  @Test
  void getRateSuppressionRule__shouldReturnOk() {
    //Arrange
    when(rateSuppressionRuleRepositoryOutPort.findRateSuppressionRule(
    )).thenReturn(createRateSuppressionRule());

    //Act
    var response = rateSuppressionRuleInPort.getRateSuppressionRule();

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getRateSuppressionList().isEmpty(), is(Boolean.FALSE));

    verifyNoMoreInteractions(rateSuppressionRuleRepositoryOutPort);
  }

  private List<RateSuppressionRule> createRateSuppressionRule() {
    var time = LocalDateTime.now();
    return List.of(
        RateSuppressionRule.builder()
            .rateType("FLEX")
            .priority((short) 10)
            .ruleId(1)
            .createdAt(time)
            .lastModifiedAt(time)
            .status(RuleStatus.ACTIVE)
            .build());
  }
}