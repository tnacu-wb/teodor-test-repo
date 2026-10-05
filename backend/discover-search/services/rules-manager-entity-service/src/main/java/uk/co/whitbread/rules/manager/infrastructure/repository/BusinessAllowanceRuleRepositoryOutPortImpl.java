package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.BusinessAllowanceRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.BusinessAllowanceRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class BusinessAllowanceRuleRepositoryOutPortImpl implements
    RuleEngineRepositoryOutPort<BusinessAllowanceRule> {

  private static final String TABLE_NAME = "business_allowance_rule";
  private final BusinessAllowanceRuleRepository businessAllowanceRuleRepository;
  private final BusinessAllowanceRuleMapper businessAllowanceRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<BusinessAllowanceRule> findNewRecords() {
    log.debug("Find new records for business allowance");
    return businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCase(
            RuleStatus.NEW.toString())
        .stream().map(businessAllowanceRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<BusinessAllowanceRule> findExpiredRecords() {
    log.debug("Find expired records for business allowance");
    return businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(), LocalDateTime.now(
            ZoneOffset.UTC)).stream().map(businessAllowanceRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<BusinessAllowanceRule> findReadyRecords() {
    log.debug("Find ready records for business allowance");
    return businessAllowanceRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
            RuleStatus.FUTURE.toString(), LocalDateTime.now(ZoneOffset.UTC)).stream()
        .map(businessAllowanceRuleMapper::toDomainModel).toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<BusinessAllowanceRule> ruleProcessorComposite) {
    log.debug("Save records for business allowance");
    businessAllowanceRuleRepository.save(
        businessAllowanceRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      businessAllowanceRuleRepository.deleteById(recordIdToDelete);
    }
  }

}
