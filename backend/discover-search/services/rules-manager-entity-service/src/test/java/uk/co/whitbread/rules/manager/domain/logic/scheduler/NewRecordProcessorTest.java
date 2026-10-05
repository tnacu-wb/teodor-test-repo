package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.RbacRule;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class NewRecordProcessorTest {

  private final RecordProcessor newRecordProcessor = new NewRecordProcessor();

  @Mock
  private RuleEngineRepositoryOutPort<Rule> ruleEngineRepository;

  @Test
  void process__shouldDetectException() {
    //Arrange
    var buggyRule = createRbacRule();
    buggyRule.setRuleId(null);
    when(ruleEngineRepository.findNewRecords()).thenReturn(List.of(buggyRule));

    //Act
    newRecordProcessor.process(ruleEngineRepository);

    //Assert
    verify(ruleEngineRepository).getTableName();
    verify(ruleEngineRepository).findNewRecords();
    verify(ruleEngineRepository).persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(buggyRule)
        .recordIdToDelete(null)
        .build());
    verifyNoMoreInteractions(ruleEngineRepository);
  }

  @Test
  void process__shouldDetectNewRuleWithActiveStatusAndNoEnableTimestampSet() {
    //Arrange
    var mockedRule = createRbacRule();
    mockedRule.setRefRuleId(null);
    mockedRule.setEnableTimestamp(null);
    when(ruleEngineRepository.findNewRecords()).thenReturn(List.of(mockedRule));

    //Act
    newRecordProcessor.process(ruleEngineRepository);

    //Assert
    verify(ruleEngineRepository).getTableName();
    verify(ruleEngineRepository).findNewRecords();
    verify(ruleEngineRepository).persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(mockedRule)
        .recordIdToDelete(null)
        .build());
    verifyNoMoreInteractions(ruleEngineRepository);
  }

  @Test
  void process__shouldDetectNewRuleWithActiveStatusAndEnableTimestampSetInPast() {
    //Arrange
    var mockedRule = createRbacRule();
    mockedRule.setRefRuleId(null);
    mockedRule.setEnableTimestamp(LocalDateTime.now(ZoneOffset.UTC).minusDays(1));
    when(ruleEngineRepository.findNewRecords()).thenReturn(List.of(mockedRule));

    //Act
    newRecordProcessor.process(ruleEngineRepository);
    //Assert
    verify(ruleEngineRepository).getTableName();
    verify(ruleEngineRepository).findNewRecords();
    verify(ruleEngineRepository).persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(mockedRule)
        .recordIdToDelete(null)
        .build());
    verifyNoMoreInteractions(ruleEngineRepository);
  }

  @Test
  void process__shouldDetectNewRuleWithFutureStatus() {
    //Arrange
    var mockedRule = createRbacRule();
    mockedRule.setRefRuleId(null);
    mockedRule.setEnableTimestamp(LocalDateTime.now(ZoneOffset.UTC).plusDays(5));
    when(ruleEngineRepository.findNewRecords()).thenReturn(List.of(mockedRule));

    //Act
    newRecordProcessor.process(ruleEngineRepository);

    //Assert
    verify(ruleEngineRepository).getTableName();
    verify(ruleEngineRepository).findNewRecords();
    verify(ruleEngineRepository).persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(mockedRule)
        .recordIdToDelete(null)
        .build());
    verifyNoMoreInteractions(ruleEngineRepository);
  }

  @Test
  void process__shouldDetectUpdateRule() {
    //Arrange
    var mockedRule = createRbacRule();
    mockedRule.setRuleId(123);
    mockedRule.setRefRuleId(321);
    when(ruleEngineRepository.findNewRecords()).thenReturn(List.of(mockedRule));

    //Act
    newRecordProcessor.process(ruleEngineRepository);

    //Assert
    verify(ruleEngineRepository).getTableName();
    verify(ruleEngineRepository).findNewRecords();
    verify(ruleEngineRepository).persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(mockedRule)
        .recordIdToDelete(123)
        .build());
    verifyNoMoreInteractions(ruleEngineRepository);
  }

  private RbacRule createRbacRule() {
    return RbacRule.builder()
        .hasAccess(true)
        .createdAt(LocalDateTime.now())
        .ruleId(1)
        .lastModifiedAt(LocalDateTime.now().minusMinutes(1))
        .resourceId("CCSUI_RES")
        .status(RuleStatus.INACTIVE)
        .roleId("AGENT")
        .build();
  }
}