package uk.co.whitbread.rules.agent.domain.logic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.Rule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RuleEngineRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class RuleEngineCacheManagerInPortImplTest {

  @InjectMocks
  private RuleEngineCacheManagerInPortImpl ruleEngineCacheManagerInPort;

  @Spy
  private List<RuleEngineRepositoryOutPort<Rule>> ruleEngineRepositories = new ArrayList<>();

  @Test
  void loadRulesInMemory__shouldTriggerAllTheWay() {
    //Arrange
    var repositoryMock = Mockito.mock(RuleEngineRepositoryOutPort.class);
    Mockito.doReturn(Stream.of(repositoryMock)).when(ruleEngineRepositories).parallelStream();

    //Act
    ruleEngineCacheManagerInPort.loadRulesInMemory();

    //Assert
    verify(repositoryMock).cacheRules();
    verifyNoMoreInteractions(repositoryMock);
    verifyNoMoreInteractions(ruleEngineRepositories);
  }

  @Test
  void syncRulesInMemory__shouldTriggerAllTheWay() {
    //Arrange
    var repositoryMock = Mockito.mock(RuleEngineRepositoryOutPort.class);
    Mockito.doReturn(Stream.of(repositoryMock)).when(ruleEngineRepositories).parallelStream();

    //Act
    ruleEngineCacheManagerInPort.syncRulesInMemory();

    //Assert
    verify(repositoryMock).updateCache();
    verifyNoMoreInteractions(repositoryMock);
    verifyNoMoreInteractions(ruleEngineRepositories);
  }
}
