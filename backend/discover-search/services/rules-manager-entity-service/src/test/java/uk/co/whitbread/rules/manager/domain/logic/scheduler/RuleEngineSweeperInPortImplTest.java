package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class RuleEngineSweeperInPortImplTest {

  private RuleEngineSweeperInPortImpl ruleEngineSweeperInPort;

  @Mock
  private RecordProcessorPipeline processorPipeline;

  @Mock
  private RuleEngineRepositoryOutPort<Rule> repositoryMock;

  @BeforeEach
  void setup() {
    var ruleEngineRepositories = Collections.singletonList(
        repositoryMock);
    ruleEngineSweeperInPort = new RuleEngineSweeperInPortImpl(ruleEngineRepositories,
        processorPipeline);
  }

  @Test
  void sweepTables__shouldTriggerPipelineAllTheWay() {

    //Arrange
    var recordProcessorMock = Mockito.mock(RecordProcessor.class);
    when(processorPipeline.getSteps()).thenReturn(List.of(recordProcessorMock));

    //Act
    ruleEngineSweeperInPort.sweepTables();

    //Assert
    verify(processorPipeline).getSteps();
    verifyNoMoreInteractions(processorPipeline);
    verify(recordProcessorMock).process(repositoryMock);
    verifyNoMoreInteractions(recordProcessorMock);
    verifyNoInteractions(repositoryMock);
  }
}