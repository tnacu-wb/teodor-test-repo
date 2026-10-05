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
import uk.co.whitbread.rules.manager.domain.model.in.PaypalRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.PaypalRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.PaypalRuleEntity;

@ExtendWith(MockitoExtension.class)
class PaypalRepositoryOutPortImplTest {

  @InjectMocks
  private PaypalRepositoryOutPortImpl paypalRepositoryOutPort;

  @Mock
  private PaypalRepository paypalRepository;

  @Mock
  private PaypalRuleMapper paypalRuleMapper;

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = paypalRepositoryOutPort.getTableName();

    //Assert
    assertEquals("paypal_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new PaypalRuleEntity();
    var dummyDomainRule = PaypalRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(paypalRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(paypalRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = paypalRepositoryOutPort.findNewRecords();

    //Assert
    verify(paypalRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(paypalRepository);
    verify(paypalRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(paypalRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new PaypalRuleEntity();
    var dummyDomainRule = PaypalRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(paypalRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(paypalRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = paypalRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(paypalRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(paypalRepository);
    verify(paypalRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(paypalRuleMapper);
    Assertions.assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new PaypalRuleEntity();
    var dummyDomainRule = PaypalRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(paypalRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(paypalRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = paypalRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(paypalRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(paypalRepository);
    verify(paypalRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(paypalRuleMapper);
    Assertions.assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new PaypalRuleEntity();
    var dummyDomainRule = PaypalRule.builder().build();
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<PaypalRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(paypalRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    paypalRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(paypalRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(paypalRuleMapper);
    verify(paypalRepository).save(dummyEntityRule);
    verify(paypalRepository).deleteById(deleteId);
    verifyNoMoreInteractions(paypalRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new PaypalRuleEntity();
    var dummyDomainRule = PaypalRule.builder().build();
    var compositeRule = RuleProcessorComposite.<PaypalRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(paypalRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    paypalRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(paypalRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(paypalRuleMapper);
    verify(paypalRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(paypalRepository);

  }
}
