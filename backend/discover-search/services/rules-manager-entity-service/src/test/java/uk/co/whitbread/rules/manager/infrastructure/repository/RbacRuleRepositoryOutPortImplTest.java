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
import uk.co.whitbread.rules.manager.domain.model.in.RbacRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RbacRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RbacRuleEntity;

@ExtendWith(MockitoExtension.class)
class RbacRuleRepositoryOutPortImplTest {

  @InjectMocks
  private RbacRuleRepositoryOutPortImpl rbacRuleRepositoryOutPort;

  @Mock
  private RbacRuleRepository rbacRuleRepository;

  @Mock
  private RbacRuleMapper rbacRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = rbacRuleRepositoryOutPort.getTableName();

    //Assert
    assertEquals("rbac_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new RbacRuleEntity();
    var dummyDomainRule = RbacRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(rbacRuleRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(rbacRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = rbacRuleRepositoryOutPort.findNewRecords();

    //Assert
    verify(rbacRuleRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(rbacRuleRepository);
    verify(rbacRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(rbacRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new RbacRuleEntity();
    var dummyDomainRule = RbacRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(rbacRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(rbacRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = rbacRuleRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(rbacRuleRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(rbacRuleRepository);
    verify(rbacRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(rbacRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new RbacRuleEntity();
    var dummyDomainRule = RbacRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(rbacRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(rbacRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = rbacRuleRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(rbacRuleRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(rbacRuleRepository);
    verify(rbacRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(rbacRuleMapper);
    assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new RbacRuleEntity();
    var dummyDomainRule = RbacRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<RbacRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(rbacRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    rbacRuleRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(rbacRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(rbacRuleMapper);
    verify(rbacRuleRepository).save(dummyEntityRule);
    verify(rbacRuleRepository).deleteById(deleteId);
    verifyNoMoreInteractions(rbacRuleRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new RbacRuleEntity();
    var dummyDomainRule = RbacRule.builder().build();
    var compositeRule = RuleProcessorComposite.<RbacRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(rbacRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    rbacRuleRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(rbacRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(rbacRuleMapper);
    verify(rbacRuleRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(rbacRuleRepository);

  }
}
