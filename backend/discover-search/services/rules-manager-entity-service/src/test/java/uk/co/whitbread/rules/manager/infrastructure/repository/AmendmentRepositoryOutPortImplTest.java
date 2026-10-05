package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.AmendmentRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.AmendmentRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.AmendmentRuleEntity;

@ExtendWith(MockitoExtension.class)
class AmendmentRepositoryOutPortImplTest {

  @InjectMocks
  private AmendmentRepositoryOutPortImpl amendmentRepositoryPort;

  @Mock
  private AmendmentRepository amendmentRepository;

  @Mock
  private AmendmentRuleMapper amendmentRuleMapper;

  @Test
  void getTableName__shouldReturnConstantTableName() {
    //Act
    var tableName = amendmentRepositoryPort.getTableName();

    //Assert
    assertEquals("amendment_rule", tableName);
  }

  @Test
  void findNewRecords__shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new AmendmentRuleEntity();
    var dummyDomainRule = AmendmentRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(amendmentRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(amendmentRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = amendmentRepositoryPort.findNewRecords();

    //Assert
    verifyNoMoreInteractions(amendmentRepository);
    verify(amendmentRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(amendmentRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    AssertionsForClassTypes.assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findNewRecords__shouldSetToFailedRecordsWithInvalidRateType() {
    //Arrange
    var dummyEntityRule1 = createAmendmentRuleEntity("Flex");
    var dummyEntityRule2 = createAmendmentRuleEntity("SOMETHING RANDOM");
    var dummyDomainRule = AmendmentRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(amendmentRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule1, dummyEntityRule2));
    when(amendmentRuleMapper.toDomainModel(dummyEntityRule1)).thenReturn(dummyDomainRule);
    doThrow(IllegalArgumentException.class).when(amendmentRuleMapper).toDomainModel(dummyEntityRule2);

    //Act
    var newRecords = amendmentRepositoryPort.findNewRecords();

    //Assert
    verify(amendmentRepository).save(dummyEntityRule2);
    verifyNoMoreInteractions(amendmentRepository);
    verify(amendmentRuleMapper).toDomainModel(dummyEntityRule1);
    verify(amendmentRuleMapper).toDomainModel(dummyEntityRule2);
    verifyNoMoreInteractions(amendmentRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    AssertionsForClassTypes.assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords__shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new AmendmentRuleEntity();
    var dummyDomainRule = AmendmentRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(amendmentRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(amendmentRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = amendmentRepositoryPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    AssertionsForClassTypes.assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(amendmentRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(amendmentRepository);
    verify(amendmentRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(amendmentRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    AssertionsForClassTypes.assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords__shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new AmendmentRuleEntity();
    var dummyDomainRule = AmendmentRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(amendmentRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(amendmentRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = amendmentRepositoryPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    AssertionsForClassTypes.assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(amendmentRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(amendmentRepository);
    verify(amendmentRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(amendmentRuleMapper);
    AssertionsForClassTypes.assertThat(newRecords).usingRecursiveComparison()
        .isEqualTo(List.of(dummyDomainRule));
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new AmendmentRuleEntity();
    var dummyDomainRule = AmendmentRule.builder().build();
    var deleteId = 123;
    var compositeRule = RuleProcessorComposite.<AmendmentRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(amendmentRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    amendmentRepositoryPort.persistRecords(compositeRule);

    //Assert
    verify(amendmentRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(amendmentRuleMapper);
    verify(amendmentRepository).save(dummyEntityRule);
    verify(amendmentRepository).deleteById(deleteId);
    verifyNoMoreInteractions(amendmentRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new AmendmentRuleEntity();
    var dummyDomainRule = AmendmentRule.builder().build();
    var compositeRule = RuleProcessorComposite.<AmendmentRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(amendmentRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    amendmentRepositoryPort.persistRecords(compositeRule);

    //Assert
    verify(amendmentRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(amendmentRuleMapper);
    verify(amendmentRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(amendmentRepository);
  }

  private AmendmentRuleEntity createAmendmentRuleEntity(String rateType) {
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setStatus("NEW");
    amendmentRuleEntity.setRateType(rateType);
    return amendmentRuleEntity;
  }
}