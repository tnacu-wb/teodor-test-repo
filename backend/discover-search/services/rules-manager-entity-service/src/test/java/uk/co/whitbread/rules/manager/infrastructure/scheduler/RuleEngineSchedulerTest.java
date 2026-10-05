package uk.co.whitbread.rules.manager.infrastructure.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.ports.primary.RuleEngineSweeperInPort;

@ExtendWith(MockitoExtension.class)
class RuleEngineSchedulerTest {

  @InjectMocks
  private RuleEngineScheduler ruleEngineScheduler;

  @Mock
  private RuleEngineSweeperInPort ruleEngineSweeperInPort;

  @Test
  void tidyRules__shouldDelegateWorkToInPort() {
    //Act
    ruleEngineScheduler.tidyRules();

    //Assert
    verify(ruleEngineSweeperInPort).sweepTables();
    verifyNoMoreInteractions(ruleEngineSweeperInPort);
  }
}