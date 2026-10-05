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
import uk.co.whitbread.rules.manager.domain.model.in.BaseRateRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.BaseRateRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BaseRateRuleEntity;

@ExtendWith(MockitoExtension.class)
class BaseRateRuleRepositoryOutPortImplTest {
  
  @InjectMocks
  BaseRateRepositoryOutPortImpl repositoryOutPort;
  
  @Mock
  BaseRateRepository baseRateRepository;
  
  @Mock
  BaseRateRuleMapper baseRateRuleMapper;
  
  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = repositoryOutPort.getTableName();
    
    //Assert
    assertEquals("base_rate_rule", tableName);
  }
  
  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new BaseRateRuleEntity();
    var dummyDomainRule = BaseRateRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(baseRateRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(baseRateRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);
    
    //Act
    var newRecords = repositoryOutPort.findNewRecords();
    
    //Assert
    verify(baseRateRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(baseRateRepository);
    verify(baseRateRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(baseRateRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }
  
  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new BaseRateRuleEntity();
    var dummyDomainRule = BaseRateRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(baseRateRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(baseRateRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);
    
    //Act
    var expiredRecords = repositoryOutPort.findExpiredRecords();
    
    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(baseRateRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(baseRateRepository);
    verify(baseRateRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(baseRateRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }
  
  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new BaseRateRuleEntity();
    var dummyDomainRule = BaseRateRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(baseRateRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(baseRateRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);
    
    //Act
    var readyRecords = repositoryOutPort.findReadyRecords();
    
    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(baseRateRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(baseRateRepository);
    verify(baseRateRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(baseRateRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }
  
  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new BaseRateRuleEntity();
    var dummyDomainRule = BaseRateRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<BaseRateRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(baseRateRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);
    
    //Act
    repositoryOutPort.persistRecords(compositeRule);
    
    //Assert
    verify(baseRateRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(baseRateRuleMapper);
    verify(baseRateRepository).save(dummyEntityRule);
    verify(baseRateRepository).deleteById(deleteId);
    verifyNoMoreInteractions(baseRateRepository);
  }
  
  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new BaseRateRuleEntity();
    var dummyDomainRule = BaseRateRule.builder().build();
    var compositeRule = RuleProcessorComposite.<BaseRateRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(baseRateRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);
    
    //Act
    repositoryOutPort.persistRecords(compositeRule);
    
    //Assert
    verify(baseRateRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(baseRateRuleMapper);
    verify(baseRateRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(baseRateRepository);
    
  }
}
