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
import uk.co.whitbread.rules.manager.domain.model.in.MaxArrivalDateRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxArrivalDateMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxArrivalDateRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxArrivalDateRepositoryOutPortImplTest {

  @InjectMocks
  private MaxArrivalDateRepositoryOutPortImpl maxArrivalDateRepositoryOutPort;

  @Mock
  private MaxArrivalDateRepository maxArrivalDateRepository;

  @Mock
  private MaxArrivalDateMapper maxArrivalDateMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = maxArrivalDateRepositoryOutPort.getTableName();

    //Assert
    assertEquals("max_arrival_date_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new MaxArrivalDateRuleEntity();
    var dummyDomainRule = MaxArrivalDateRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(maxArrivalDateRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(maxArrivalDateMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = maxArrivalDateRepositoryOutPort.findNewRecords();

    //Assert
    verify(maxArrivalDateRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(maxArrivalDateRepository);
    verify(maxArrivalDateMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxArrivalDateMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new MaxArrivalDateRuleEntity();
    var dummyDomainRule = MaxArrivalDateRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(maxArrivalDateRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(maxArrivalDateMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = maxArrivalDateRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(maxArrivalDateRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(maxArrivalDateRepository);
    verify(maxArrivalDateMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxArrivalDateMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new MaxArrivalDateRuleEntity();
    var dummyDomainRule = MaxArrivalDateRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(maxArrivalDateRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(maxArrivalDateMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = maxArrivalDateRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(maxArrivalDateRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(maxArrivalDateRepository);
    verify(maxArrivalDateMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxArrivalDateMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new MaxArrivalDateRuleEntity();
    var dummyDomainRule = MaxArrivalDateRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<MaxArrivalDateRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(maxArrivalDateMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    maxArrivalDateRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(maxArrivalDateMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(maxArrivalDateMapper);
    verify(maxArrivalDateRepository).save(dummyEntityRule);
    verify(maxArrivalDateRepository).deleteById(deleteId);
    verifyNoMoreInteractions(maxArrivalDateRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new MaxArrivalDateRuleEntity();
    var dummyDomainRule = MaxArrivalDateRule.builder().build();
    var compositeRule = RuleProcessorComposite.<MaxArrivalDateRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(maxArrivalDateMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    maxArrivalDateRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(maxArrivalDateMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(maxArrivalDateMapper);
    verify(maxArrivalDateRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(maxArrivalDateRepository);

  }
}
