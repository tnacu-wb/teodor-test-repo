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
import uk.co.whitbread.rules.manager.domain.model.in.RateSuppressionRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RateSuppressionRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RateSuppressionRuleEntity;

@ExtendWith(MockitoExtension.class)
class RateRateSuppressionRepositoryOutPortImplTest {

  @InjectMocks
  private RateSuppressionRepositoryOutPortImpl suppressionRepositoryOutPort;

  @Mock
  private RateSuppressionRepository rateSuppressionRepository;

  @Mock
  private RateSuppressionRuleMapper rateSuppressionRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = suppressionRepositoryOutPort.getTableName();

    //Assert
    assertEquals("suppression_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new RateSuppressionRuleEntity();
    var dummyDomainRule = RateSuppressionRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(rateSuppressionRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(rateSuppressionRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = suppressionRepositoryOutPort.findNewRecords();

    //Assert
    verify(rateSuppressionRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(rateSuppressionRepository);
    verify(rateSuppressionRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(rateSuppressionRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new RateSuppressionRuleEntity();
    var dummyDomainRule = RateSuppressionRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(
        rateSuppressionRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
            disableTimestampArgumentCapture.capture())).thenReturn(List.of(dummyEntityRule));
    when(rateSuppressionRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = suppressionRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(rateSuppressionRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(rateSuppressionRepository);
    verify(rateSuppressionRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(rateSuppressionRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new RateSuppressionRuleEntity();
    var dummyDomainRule = RateSuppressionRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(rateSuppressionRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(List.of(dummyEntityRule));
    when(rateSuppressionRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = suppressionRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(rateSuppressionRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(rateSuppressionRepository);
    verify(rateSuppressionRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(rateSuppressionRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new RateSuppressionRuleEntity();
    var dummyDomainRule = RateSuppressionRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<RateSuppressionRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(rateSuppressionRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    suppressionRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(rateSuppressionRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(rateSuppressionRuleMapper);
    verify(rateSuppressionRepository).save(dummyEntityRule);
    verify(rateSuppressionRepository).deleteById(deleteId);
    verifyNoMoreInteractions(rateSuppressionRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new RateSuppressionRuleEntity();
    var dummyDomainRule = RateSuppressionRule.builder().build();
    var compositeRule = RuleProcessorComposite.<RateSuppressionRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(rateSuppressionRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    suppressionRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(rateSuppressionRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(rateSuppressionRuleMapper);
    verify(rateSuppressionRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(rateSuppressionRepository);
  }

}
