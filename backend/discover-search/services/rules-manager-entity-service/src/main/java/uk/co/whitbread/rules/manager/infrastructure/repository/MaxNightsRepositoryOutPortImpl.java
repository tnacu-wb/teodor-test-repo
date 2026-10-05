package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.MaxNightsRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxNightsRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class MaxNightsRepositoryOutPortImpl implements RuleEngineRepositoryOutPort<MaxNightsRule> {

  private static final String TABLE_NAME = "max_nights_rule";
  private final MaxNightsRepository maxNightsRepository;
  private final MaxNightsRuleMapper maxNightsRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<MaxNightsRule> findNewRecords() {
    log.debug("Find new records for max nights");
    return maxNightsRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(maxNightsRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxNightsRule> findExpiredRecords() {
    log.debug("Find expired records for max nights");
    return maxNightsRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(maxNightsRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxNightsRule> findReadyRecords() {
    log.debug("Find ready records for max nights");
    return maxNightsRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(maxNightsRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<MaxNightsRule> ruleProcessorComposite) {
    log.debug("Save records for max nights");
    maxNightsRepository.save(
        maxNightsRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      maxNightsRepository.deleteById(recordIdToDelete);
    }
  }
}
