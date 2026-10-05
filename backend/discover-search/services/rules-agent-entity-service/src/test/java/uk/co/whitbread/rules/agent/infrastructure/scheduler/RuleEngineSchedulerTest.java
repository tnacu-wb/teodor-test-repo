package uk.co.whitbread.rules.agent.infrastructure.scheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;

@ExtendWith(MockitoExtension.class)
class RuleEngineSchedulerTest {

  @InjectMocks
  private RuleEngineScheduler ruleEngineScheduler;

  @Mock
  private RuleEngineCacheManagerInPort ruleEngineCacheManagerInPort;


  @Test
  void initialDataLoad__shouldDelegateWorkToInPort() throws IllegalAccessException {
    //Act
    ruleEngineScheduler.initialDataLoad();

    //Assert
    var actualIsDataLoaded = (AtomicBoolean) FieldUtils.readField(ruleEngineScheduler,
        "isDataLoaded",
        true);
    assertThat(actualIsDataLoaded.get()).isTrue();
    verify(ruleEngineCacheManagerInPort).loadRulesInMemory();
    verifyNoMoreInteractions(ruleEngineCacheManagerInPort);
  }

  @Test
  void updateData__shouldDoNothingIfIsDataLoadedIsFalse() {
    //Act
    ruleEngineScheduler.updateData();

    //Assert
    verifyNoInteractions(ruleEngineCacheManagerInPort);
  }

  @Test
  void updateData__shouldDelegateWorkToInPort() throws IllegalAccessException {
    //Arrange
    var actualIsDataLoaded = (AtomicBoolean) FieldUtils.readField(ruleEngineScheduler,
        "isDataLoaded",
        true);
    actualIsDataLoaded.set(true);

    //Act
    ruleEngineScheduler.updateData();

    //Assert
    verify(ruleEngineCacheManagerInPort).syncRulesInMemory();
    verifyNoMoreInteractions(ruleEngineCacheManagerInPort);
  }
}
