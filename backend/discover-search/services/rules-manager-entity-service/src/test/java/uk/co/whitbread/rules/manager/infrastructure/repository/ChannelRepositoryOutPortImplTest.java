package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.ChannelRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.ChannelRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.ChannelRuleEntity;

@ExtendWith(MockitoExtension.class)
class ChannelRepositoryOutPortImplTest {

  @InjectMocks
  private ChannelRepositoryOutPortImpl channelRepositoryOutPort;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ChannelRuleMapper channelRuleMapper;

  @Test
  void getTableName__shouldReturnConstantTableName() {
    //Act
    var tableName = channelRepositoryOutPort.getTableName();

    //Assert
    assertEquals("channel_rule", tableName);
  }

  @Test
  void findNewRecords__shouldGetListOfNewRecords() {
    //Arrange
    var dummyEntityRule = new ChannelRuleEntity();
    var dummyDomainRule = ChannelRule.builder().build();
    var newStatus = RuleStatus.NEW.toString();
    when(channelRepository.findAllByStatusEqualsIgnoreCase(newStatus)).thenReturn(
        List.of(dummyEntityRule));
    when(channelRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = channelRepositoryOutPort.findNewRecords();

    //Assert
    verifyNoMoreInteractions(channelRepository);
    verify(channelRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(channelRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    AssertionsForClassTypes.assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findExpiredRecords__shouldFindAllRecordsToBeSetAsInactive() {
    //Arrange
    var dummyEntityRule = new ChannelRuleEntity();
    var dummyDomainRule = ChannelRule.builder().build();
    var disableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(channelRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(any(),
        disableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(channelRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = channelRepositoryOutPort.findExpiredRecords();

    //Assert
    var capturedDisableTimestamp = disableTimestampArgumentCapture.getValue();
    AssertionsForClassTypes.assertThat(capturedDisableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(channelRepository).findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(),
        capturedDisableTimestamp);
    verifyNoMoreInteractions(channelRepository);
    verify(channelRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(channelRuleMapper);
    Assertions.assertThat(newRecords).hasSize(1);
    AssertionsForClassTypes.assertThat(newRecords.get(0)).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(dummyDomainRule);
  }

  @Test
  void findReadyRecords__shouldFindAllRecordsReadyForActivation() {
    //Arrange
    var dummyEntityRule = new ChannelRuleEntity();
    var dummyDomainRule = ChannelRule.builder().build();
    var enableTimestampArgumentCapture = ArgumentCaptor.forClass(LocalDateTime.class);
    when(channelRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(any(),
        enableTimestampArgumentCapture.capture())).thenReturn(
        List.of(dummyEntityRule));
    when(channelRuleMapper.toDomainModel(dummyEntityRule)).thenReturn(dummyDomainRule);

    //Act
    var newRecords = channelRepositoryOutPort.findReadyRecords();

    //Assert
    var capturedEnableTimestamp = enableTimestampArgumentCapture.getValue();
    AssertionsForClassTypes.assertThat(capturedEnableTimestamp)
        .isBefore(LocalDateTime.now());
    verify(channelRepository).findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        RuleStatus.FUTURE.toString(),
        capturedEnableTimestamp);
    verifyNoMoreInteractions(channelRepository);
    verify(channelRuleMapper).toDomainModel(dummyEntityRule);
    verifyNoMoreInteractions(channelRuleMapper);
    AssertionsForClassTypes.assertThat(newRecords).usingRecursiveComparison()
        .isEqualTo(List.of(dummyDomainRule));
  }

  @Test
  void persistRecords_givenDomainRuleAndIdToDelete_shouldUpdatePastRecordAndDeleteNewRecordById() {
    //Arrange
    var dummyEntityRule = new ChannelRuleEntity();
    var dummyDomainRule = ChannelRule.builder().build();
    var deleteId = 123;
    var compositeRule = RuleProcessorComposite.<ChannelRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(deleteId)
        .build();
    when(channelRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    channelRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(channelRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(channelRuleMapper);
    verify(channelRepository).save(dummyEntityRule);
    verify(channelRepository).deleteById(deleteId);
    verifyNoMoreInteractions(channelRepository);
  }

  @Test
  void persistRecords_givenDomainRuleAndNoIdToDelete_shouldUpdateNewRecord() {
    //Arrange
    var dummyEntityRule = new ChannelRuleEntity();
    var dummyDomainRule = ChannelRule.builder().build();
    var compositeRule = RuleProcessorComposite.<ChannelRule>builder()
        .recordToUpdate(dummyDomainRule)
        .recordIdToDelete(null)
        .build();
    when(channelRuleMapper.toEntityDto(dummyDomainRule)).thenReturn(dummyEntityRule);

    //Act
    channelRepositoryOutPort.persistRecords(compositeRule);

    //Assert
    verify(channelRuleMapper).toEntityDto(dummyDomainRule);
    verifyNoMoreInteractions(channelRuleMapper);
    verify(channelRepository).save(dummyEntityRule);
    verifyNoMoreInteractions(channelRepository);
  }
}