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
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomsRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxRoomsRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomsRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxRoomsRepositoryOutPortImplTest {

  @InjectMocks
  private MaxRoomsRepositoryOutPortImpl maxRoomsRepositoryOutPort;

  @Mock
  private MaxRoomsRepository maxRoomsRepository;

  @Mock
  private MaxRoomsRuleMapper maxRoomsRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = maxRoomsRepositoryOutPort.getTableName();

    //Assert
    assertEquals("max_Rooms_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new MaxRoomsRuleEntity();
    var dummyDomainRule = MaxRoomsRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(maxRoomsRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(maxRoomsRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = maxRoomsRepositoryOutPort.findNewRecords();

    //Assert
    verify(maxRoomsRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(maxRoomsRepository);
    verify(maxRoomsRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxRoomsRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new MaxRoomsRuleEntity();
    var dummyDomainRule = MaxRoomsRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(maxRoomsRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(maxRoomsRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = maxRoomsRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(maxRoomsRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(maxRoomsRepository);
    verify(maxRoomsRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxRoomsRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new MaxRoomsRuleEntity();
    var dummyDomainRule = MaxRoomsRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(maxRoomsRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(maxRoomsRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = maxRoomsRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(maxRoomsRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(maxRoomsRepository);
    verify(maxRoomsRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(maxRoomsRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new MaxRoomsRuleEntity();
    var dummyDomainRule = MaxRoomsRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<MaxRoomsRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(maxRoomsRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    maxRoomsRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(maxRoomsRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(maxRoomsRuleMapper);
    verify(maxRoomsRepository).save(dummyEntityRule);
    verify(maxRoomsRepository).deleteById(deleteId);
    verifyNoMoreInteractions(maxRoomsRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new MaxRoomsRuleEntity();
    var dummyDomainRule = MaxRoomsRule.builder().build();
    var compositeRule = RuleProcessorComposite.<MaxRoomsRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(maxRoomsRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    maxRoomsRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(maxRoomsRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(maxRoomsRuleMapper);
    verify(maxRoomsRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(maxRoomsRepository);

  }
}
