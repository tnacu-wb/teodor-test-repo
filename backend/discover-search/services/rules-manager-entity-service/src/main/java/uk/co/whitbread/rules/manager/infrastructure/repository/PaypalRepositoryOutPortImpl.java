package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.PaypalRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.PaypalRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class PaypalRepositoryOutPortImpl implements RuleEngineRepositoryOutPort<PaypalRule> {

  private static final String TABLE_NAME = "paypal_rule";
  private final PaypalRepository paypalRepository;
  private final PaypalRuleMapper paypalRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<PaypalRule> findNewRecords() {
    log.debug("Find new records for Paypal");
    return paypalRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(paypalRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<PaypalRule> findExpiredRecords() {
    log.debug("Find expired records for paypal");
    return paypalRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(paypalRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<PaypalRule> findReadyRecords() {
    log.debug("Find ready records for paypal");
    return paypalRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(paypalRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<PaypalRule> ruleProcessorComposite) {
    log.debug("Save records for paypal");
    paypalRepository.save(
        paypalRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      paypalRepository.deleteById(recordIdToDelete);
    }
  }
}
