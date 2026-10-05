package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.BusinessAllowanceRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.BusinessAllowanceRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BusinessAllowanceRuleEntity;

@ExtendWith(MockitoExtension.class)
class BusinessAllowanceRepositoryOutPortImplTest {

  @InjectMocks
  private BusinessAllowanceRuleRepositoryOutPortImpl businessAllowanceRuleRepositoryOutPort;

  @Mock
  private BusinessAllowanceRuleRepository businessAllowanceRuleRepository;

  @Mock
  private BusinessAllowanceRuleMapper businessAllowanceRuleMapper;

  private BusinessAllowanceRuleEntity businessAllowanceRuleEntity;
  private BusinessAllowanceRule dummyDomainRule;

  @BeforeEach
  void setUp() {
    businessAllowanceRuleEntity = new BusinessAllowanceRuleEntity();
    dummyDomainRule = BusinessAllowanceRule.builder().build();
  }

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = businessAllowanceRuleRepositoryOutPort.getTableName();

    //Assert
    assertEquals("business_allowance_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var newStatus = RuleStatus.NEW.toString();
    when(businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(businessAllowanceRuleEntity));
    when(businessAllowanceRuleMapper.toDomainModel(businessAllowanceRuleEntity)).thenReturn(
        dummyDomainRule);

    //Act
    var newRecords = businessAllowanceRuleRepositoryOutPort.findNewRecords();

    //Assert
    verify(businessAllowanceRuleRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(businessAllowanceRuleRepository);
    verify(businessAllowanceRuleMapper).toDomainModel(businessAllowanceRuleEntity);
    verifyNoMoreInteractions(businessAllowanceRuleMapper);
    assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(
        businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
            any(),
            disableTimestampArgumentCapture.capture())).thenReturn(List.of(
        businessAllowanceRuleEntity));
    when(businessAllowanceRuleMapper.toDomainModel(businessAllowanceRuleEntity)).thenReturn(
        dummyDomainRule);

    //Act
    var expiredRecords = businessAllowanceRuleRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(
        businessAllowanceRuleRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(businessAllowanceRuleRepository);
    verify(businessAllowanceRuleMapper).toDomainModel(businessAllowanceRuleEntity);
    verifyNoMoreInteractions(businessAllowanceRuleMapper);
    assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        any(),
        enableTimestampArgumentCapture.capture())).thenReturn(List.of(businessAllowanceRuleEntity));
    when(businessAllowanceRuleMapper.toDomainModel(businessAllowanceRuleEntity)).thenReturn(
        dummyDomainRule);

    //Act
    var readyRecords = businessAllowanceRuleRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(
        businessAllowanceRuleRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(businessAllowanceRuleRepository);
    verify(businessAllowanceRuleMapper).toDomainModel(businessAllowanceRuleEntity);
    verifyNoMoreInteractions(businessAllowanceRuleMapper);
    assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<BusinessAllowanceRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(businessAllowanceRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(
        businessAllowanceRuleEntity);

    //Act
    businessAllowanceRuleRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(businessAllowanceRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(businessAllowanceRuleMapper);
    verify(businessAllowanceRuleRepository).save(businessAllowanceRuleEntity);
    verify(businessAllowanceRuleRepository).deleteById(deleteId);
    verifyNoMoreInteractions(businessAllowanceRuleRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var compositeRule = RuleProcessorComposite.<BusinessAllowanceRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(businessAllowanceRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(
        businessAllowanceRuleEntity);

    //Act
    businessAllowanceRuleRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(businessAllowanceRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(businessAllowanceRuleMapper);
    verify(businessAllowanceRuleRepository).save(businessAllowanceRuleEntity);
    verifyNoMoreInteractions(businessAllowanceRuleRepository);
  }

}
