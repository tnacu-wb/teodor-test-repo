package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.RbacRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RbacRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class RbacRuleRepositoryOutPortImpl implements RuleEngineRepositoryOutPort<RbacRule> {

  private static final String TABLE_NAME = "rbac_rule";
  private final RbacRuleRepository rbacRuleRepository;
  private final RbacRuleMapper rbacRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<RbacRule> findNewRecords() {
    log.debug("Find new records for rbac");
    return rbacRuleRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(rbacRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<RbacRule> findExpiredRecords() {
    log.debug("Find expired records for rbac");
    return rbacRuleRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(rbacRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<RbacRule> findReadyRecords() {
    log.debug("Find ready records for rbac");
    return rbacRuleRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(rbacRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<RbacRule> ruleProcessorComposite) {
    log.debug("Save records for rbac");
    rbacRuleRepository.save(
        rbacRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      rbacRuleRepository.deleteById(recordIdToDelete);
    }
  }
}
