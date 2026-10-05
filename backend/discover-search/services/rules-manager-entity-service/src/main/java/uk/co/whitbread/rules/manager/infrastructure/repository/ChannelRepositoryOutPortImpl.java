package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.ChannelRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.ChannelRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class ChannelRepositoryOutPortImpl implements
    RuleEngineRepositoryOutPort<ChannelRule> {

  private static final String TABLE_NAME = "channel_rule";
  private final ChannelRepository channelRepository;
  private final ChannelRuleMapper channelRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<ChannelRule> findNewRecords() {
    return channelRepository.findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream().map(channelRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<ChannelRule> findExpiredRecords() {
    return channelRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(), LocalDateTime.now(
            ZoneOffset.UTC)).stream().map(channelRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<ChannelRule> findReadyRecords() {
    return channelRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
            RuleStatus.FUTURE.toString(), LocalDateTime.now(ZoneOffset.UTC)).stream()
        .map(channelRuleMapper::toDomainModel).toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<ChannelRule> ruleProcessorComposite) {
    channelRepository.save(
        channelRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      channelRepository.deleteById(recordIdToDelete);
    }
  }
}
