package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;

@Slf4j
public class NewRecordProcessor implements RecordProcessor {

  @Override
  public void process(RuleEngineRepositoryOutPort<Rule> ruleEngineRepository) {
    log.info("Processing new rules from table: {}", ruleEngineRepository.getTableName());
    ruleEngineRepository
        .findNewRecords()
        .forEach(rule -> processRule(ruleEngineRepository, rule));
  }

  private void processRule(RuleEngineRepositoryOutPort<Rule> ruleEngineRepository, Rule rule) {
    Integer ruleIdToDelete = null;
    try {
      rule.validateSelf();
      if (rule.getRefRuleId() == null) {
        processAsNewRule(rule);
      } else {
        ruleIdToDelete = rule.getRuleId();
        processAsUpdateRule(rule);
      }
    } catch (Exception ex) {
      log.error("Unable to process the rule", ex);
      ruleIdToDelete = null;
      rule.setStatus(RuleStatus.FAILED);
    }
    ruleEngineRepository.persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(rule)
        .recordIdToDelete(ruleIdToDelete)
        .build());
  }

  private void processAsUpdateRule(Rule rule) {
    rule.setRuleId(rule.getRefRuleId());
    rule.setRefRuleId(null);
  }

  private void processAsNewRule(Rule rule) {
    var currentTimestamp = LocalDateTime.now(ZoneOffset.UTC);
    var enableTimestamp = rule.getEnableTimestamp();
    var isEnableTimeNotSetOrBeforeCurrentDate =
        enableTimestamp == null || enableTimestamp.isBefore(currentTimestamp);
    if (isEnableTimeNotSetOrBeforeCurrentDate) {
      rule.setStatus(RuleStatus.ACTIVE);
    } else {
      rule.setStatus(RuleStatus.FUTURE);
    }
  }
}