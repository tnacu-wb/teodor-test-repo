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
import uk.co.whitbread.rules.manager.domain.model.in.RoomSubstitutionRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RoomSubstitutionRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@ExtendWith(MockitoExtension.class)
class RoomSubstitutionRepositoryOutPortImplTest {

  @InjectMocks
  private RoomSubstitutionRepositoryOutPortImpl roomSubstitutionRepositoryOutPort;

  @Mock
  private RoomSubstitutionRepository roomSubstitutionRepository;

  @Mock
  private RoomSubstitutionRuleMapper roomSubstitutionRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = roomSubstitutionRepositoryOutPort.getTableName();

    //Assert
    assertEquals("room_substitution_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new RoomSubstitutionRuleEntity();
    var dummyDomainRule = RoomSubstitutionRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(roomSubstitutionRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(roomSubstitutionRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = roomSubstitutionRepositoryOutPort.findNewRecords();

    //Assert
    verify(roomSubstitutionRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(roomSubstitutionRepository);
    verify(roomSubstitutionRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(roomSubstitutionRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new RoomSubstitutionRuleEntity();
    var dummyDomainRule = RoomSubstitutionRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(
        roomSubstitutionRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
            disableTimestampArgumentCapture.capture())).thenReturn(List.of(dummyEntityRule));
    when(roomSubstitutionRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = roomSubstitutionRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(roomSubstitutionRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(roomSubstitutionRepository);
    verify(roomSubstitutionRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(roomSubstitutionRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new RoomSubstitutionRuleEntity();
    var dummyDomainRule = RoomSubstitutionRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(roomSubstitutionRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(List.of(dummyEntityRule));
    when(roomSubstitutionRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = roomSubstitutionRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(roomSubstitutionRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(roomSubstitutionRepository);
    verify(roomSubstitutionRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(roomSubstitutionRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new RoomSubstitutionRuleEntity();
    var dummyDomainRule = RoomSubstitutionRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<RoomSubstitutionRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(roomSubstitutionRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    roomSubstitutionRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(roomSubstitutionRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(roomSubstitutionRuleMapper);
    verify(roomSubstitutionRepository).save(dummyEntityRule);
    verify(roomSubstitutionRepository).deleteById(deleteId);
    verifyNoMoreInteractions(roomSubstitutionRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new RoomSubstitutionRuleEntity();
    var dummyDomainRule = RoomSubstitutionRule.builder().build();
    var compositeRule = RuleProcessorComposite.<RoomSubstitutionRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(roomSubstitutionRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    roomSubstitutionRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(roomSubstitutionRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(roomSubstitutionRuleMapper);
    verify(roomSubstitutionRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(roomSubstitutionRepository);
  }

}
