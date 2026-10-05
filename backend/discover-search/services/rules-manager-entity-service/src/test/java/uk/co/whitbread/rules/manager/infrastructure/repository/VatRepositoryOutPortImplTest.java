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
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.model.in.VatRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.VatRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.VatRuleEntity;

@ExtendWith(MockitoExtension.class)
class VatRepositoryOutPortImplTest {

  @InjectMocks
  private VatRuleRepositoryOutPortImpl vatRuleRepositoryOutPort;

  @Mock
  private VatRuleRepository vatRuleRepository;

  @Mock
  private VatRuleMapper vatRuleMapper;

  private VatRuleEntity dummyEntityRule;
  private VatRule dummyDomainRule;

  @BeforeEach
  void setUp() {
    dummyEntityRule = new VatRuleEntity();
    dummyDomainRule = VatRule.builder().build();
  }

  @Test
  void getTableName_shouldReturnConstantTableName() {
    //Act
    var tableName = vatRuleRepositoryOutPort.getTableName();

    //Assert
    assertEquals("vat_rule", tableName);
  }

  @Test
  void findNewRecords_shouldGetListOfNewRecords() {
    //Arrange
    var newStatus = RuleStatus.NEW.toString();
    when(vatRuleRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(vatRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = vatRuleRepositoryOutPort.findNewRecords();

    //Assert
    verify(vatRuleRepository).findAllByStatusEqualsIgnoreCase(newStatus);
    verifyNoMoreInteractions(vatRuleRepository);
    verify(vatRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(vatRuleMapper);
    assertThat(newRecords).hasSize(1);
    assertThat(newRecords.get(0)).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords_shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(
        vatRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
            disableTimestampArgumentCapture.capture())).thenReturn(List.of(dummyEntityRule));
    when(vatRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var expiredRecords = vatRuleRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(vatRuleRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(vatRuleRepository);
    verify(vatRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(vatRuleMapper);
    assertThat(expiredRecords).hasSize(1);
    assertThat(expiredRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords_shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(vatRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(List.of(dummyEntityRule));
    when(vatRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var readyRecords = vatRuleRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(vatRuleRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(vatRuleRepository);
    verify(vatRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(vatRuleMapper);
    assertThat(readyRecords).hasSize(1);
    assertThat(readyRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var deleteId = 1234;
    var compositeRule = RuleProcessorComposite.<VatRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(vatRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    vatRuleRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(vatRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(vatRuleMapper);
    verify(vatRuleRepository).save(dummyEntityRule);
    verify(vatRuleRepository).deleteById(deleteId);
    verifyNoMoreInteractions(vatRuleRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var compositeRule = RuleProcessorComposite.<VatRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(vatRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    vatRuleRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(vatRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(vatRuleMapper);
    verify(vatRuleRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(vatRuleRepository);
  }

}
