package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.BaseRateRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.BaseRateRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class BaseRateRepositoryOutPortImpl implements RuleEngineRepositoryOutPort<BaseRateRule> {
  
  private static final String TABLE_NAME = "base_rate_rule";
  private final BaseRateRepository baseRateRepository;
  private final BaseRateRuleMapper baseRateRuleMapper;
  
  @Override
  public String getTableName() {
    return TABLE_NAME;
  }
  
  @Override
  public List<BaseRateRule> findNewRecords() {
    log.debug("Find new records for base rate");
    return baseRateRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(baseRateRuleMapper::toDomainModel)
        .toList();
  }
  
  @Override
  public List<BaseRateRule> findExpiredRecords() {
    log.debug("Find expired records for base rate");
    return baseRateRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(baseRateRuleMapper::toDomainModel)
        .toList();
  }
  
  @Override
  public List<BaseRateRule> findReadyRecords() {
    log.debug("Find ready records for base rate");
    return baseRateRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(baseRateRuleMapper::toDomainModel)
        .toList();
  }
  
  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<BaseRateRule> ruleProcessorComposite) {
    log.debug("Save records for max rooms");
    baseRateRepository.save(
        baseRateRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      baseRateRepository.deleteById(recordIdToDelete);
    }
  }
}
