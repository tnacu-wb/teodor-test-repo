package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.model.in.VatRule;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.VatRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class VatRuleRepositoryOutPortImpl implements
    RuleEngineRepositoryOutPort<VatRule> {

  private static final String TABLE_NAME = "vat_rule";
  private final VatRuleRepository vatRuleRepository;
  private final VatRuleMapper vatRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<VatRule> findNewRecords() {
    log.debug("Find new records for vat");
    return vatRuleRepository.findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream().map(vatRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<VatRule> findExpiredRecords() {
    log.debug("Find expired records for vat");
    return vatRuleRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(), LocalDateTime.now(
            ZoneOffset.UTC)).stream().map(vatRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<VatRule> findReadyRecords() {
    log.debug("Find ready records for vat");
    return vatRuleRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
            RuleStatus.FUTURE.toString(), LocalDateTime.now(ZoneOffset.UTC)).stream()
        .map(vatRuleMapper::toDomainModel).toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<VatRule> ruleProcessorComposite) {
    log.debug("Save records for vat");
    vatRuleRepository.save(
        vatRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      vatRuleRepository.deleteById(recordIdToDelete);
    }
  }

}
