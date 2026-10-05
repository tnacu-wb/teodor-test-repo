package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.MaxNightsRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxNightsRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxNightsRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxNightsRepositoryOutPortImplTest {

  @InjectMocks
  private MaxNightsRepositoryOutPortImpl maxNightsRepositoryOutPort;

  @Mock
  private MaxNightsRepository maxNightsRepository;

  @Mock
  private MaxNightsRuleMapper maxNightsRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = maxNightsRepositoryOutPort.getTableName();

    //Assert
    assertEquals("max_nights_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new MaxNightsRuleEntity();
    var dummyDomainRule = MaxNightsRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(maxNightsRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(maxNightsRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = maxNightsRepositoryOutPort.findNewRecords();

    //Assert
    verify(maxNightsRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(maxNightsRepository);
    verify(maxNightsRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxNightsRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new MaxNightsRuleEntity();
    var dummyDomainRule = MaxNightsRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(maxNightsRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(maxNightsRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = maxNightsRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(maxNightsRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(maxNightsRepository);
    verify(maxNightsRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxNightsRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new MaxNightsRuleEntity();
    var dummyDomainRule = MaxNightsRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(maxNightsRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(maxNightsRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = maxNightsRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(maxNightsRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(maxNightsRepository);
    verify(maxNightsRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxNightsRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new MaxNightsRuleEntity();
    var dummyDomainRule = MaxNightsRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<MaxNightsRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(maxNightsRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    maxNightsRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(maxNightsRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(maxNightsRuleMapper);
    verify(maxNightsRepository).save(dummyEntityRule);
    verify(maxNightsRepository).deleteById(deleteId);
    verifyNoMoreInteractions(maxNightsRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new MaxNightsRuleEntity();
    var dummyDomainRule = MaxNightsRule.builder().build();
    var compositeRule = RuleProcessorComposite.<MaxNightsRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(maxNightsRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    maxNightsRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(maxNightsRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(maxNightsRuleMapper);
    verify(maxNightsRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(maxNightsRepository);

  }
}
