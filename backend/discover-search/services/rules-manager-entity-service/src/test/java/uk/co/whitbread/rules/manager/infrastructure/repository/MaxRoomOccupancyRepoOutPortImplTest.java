package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxRoomOccupancyRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

@ExtendWith(MockitoExtension.class)
class MaxRoomOccupancyRepoOutPortImplTest {

  @InjectMocks
  MaxRoomOccupancyRepositoryOutPortImpl repositoryOutPort;

  @Mock
  private MaxRoomOccupancyRepository roomOccupancyRepository;

  @Mock
  private MaxRoomOccupancyRuleMapper roomOccupancyRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = repositoryOutPort.getTableName();

    //Assert
    assertEquals("max_room_occupancy_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new MaxRoomOccupancyRuleEntity();
    var dummyDomainRule = MaxRoomOccupancyRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(roomOccupancyRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(roomOccupancyRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = repositoryOutPort.findNewRecords();

    //Assert
    verify(roomOccupancyRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(roomOccupancyRepository);
    verify(roomOccupancyRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(roomOccupancyRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new MaxRoomOccupancyRuleEntity();
    var dummyDomainRule = MaxRoomOccupancyRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(roomOccupancyRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(roomOccupancyRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = repositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(roomOccupancyRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(roomOccupancyRepository);
    verify(roomOccupancyRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(roomOccupancyRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new MaxRoomOccupancyRuleEntity();
    var dummyDomainRule = MaxRoomOccupancyRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(roomOccupancyRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(roomOccupancyRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = repositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(roomOccupancyRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(roomOccupancyRepository);
    verify(roomOccupancyRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(roomOccupancyRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new MaxRoomOccupancyRuleEntity();
    var dummyDomainRule = MaxRoomOccupancyRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<MaxRoomOccupancyRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(roomOccupancyRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    repositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(roomOccupancyRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(roomOccupancyRuleMapper);
    verify(roomOccupancyRepository).save(dummyEntityRule);
    verify(roomOccupancyRepository).deleteById(deleteId);
    verifyNoMoreInteractions(roomOccupancyRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new MaxRoomOccupancyRuleEntity();
    var dummyDomainRule = MaxRoomOccupancyRule.builder().build();
    var compositeRule = RuleProcessorComposite.<MaxRoomOccupancyRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(roomOccupancyRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    repositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(roomOccupancyRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(roomOccupancyRuleMapper);
    verify(roomOccupancyRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(roomOccupancyRepository);

  }

}
