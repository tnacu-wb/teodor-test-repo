package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.RateSuppressionRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RateSuppressionRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class RateSuppressionRepositoryOutPortImpl implements
    RuleEngineRepositoryOutPort<RateSuppressionRule> {

  private static final String TABLE_NAME = "suppression_rule";
  private final RateSuppressionRepository rateSuppressionRepository;
  private final RateSuppressionRuleMapper rateSuppressionRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<RateSuppressionRule> findNewRecords() {
    return rateSuppressionRepository.findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream().map(rateSuppressionRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<RateSuppressionRule> findExpiredRecords() {
    return rateSuppressionRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(), LocalDateTime.now(
            ZoneOffset.UTC)).stream().map(rateSuppressionRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<RateSuppressionRule> findReadyRecords() {
    return rateSuppressionRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
            RuleStatus.FUTURE.toString(), LocalDateTime.now(ZoneOffset.UTC)).stream()
        .map(rateSuppressionRuleMapper::toDomainModel).toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<RateSuppressionRule> ruleProcessorComposite) {
    rateSuppressionRepository.save(
        rateSuppressionRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      rateSuppressionRepository.deleteById(recordIdToDelete);
    }
  }
}
