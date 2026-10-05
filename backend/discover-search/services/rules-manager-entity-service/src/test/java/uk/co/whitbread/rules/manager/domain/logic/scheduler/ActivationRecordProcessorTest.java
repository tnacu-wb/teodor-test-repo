package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
class ActivationRecordProcessorTest {

  private final RecordProcessor activationRecordProcessor = new ActivationRecordProcessor();

  @Mock
  private RuleEngineRepositoryOutPort<Rule> ruleEngineRepository;

  @Test
  void process__shouldActivateRule() {
    //Arrange
    var readyRule = createRbacRule();
    when(ruleEngineRepository.findReadyRecords()).thenReturn(List.of(readyRule));

    //Act
    activationRecordProcessor.process(ruleEngineRepository);

    //Assert
    verify(ruleEngineRepository).getTableName();
    verify(ruleEngineRepository).findReadyRecords();
    verify(ruleEngineRepository).persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(readyRule)
        .build());
    verifyNoMoreInteractions(ruleEngineRepository);
  }

  private RbacRule createRbacRule() {
    return RbacRule.builder()
        .hasAccess(true)
        .ruleId(1)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(LocalDateTime.now().minusMinutes(1))
        .resourceId("CCSUI_RES")
        .status(RuleStatus.ACTIVE)
        .roleId("AGENT")
        .build();
  }
}