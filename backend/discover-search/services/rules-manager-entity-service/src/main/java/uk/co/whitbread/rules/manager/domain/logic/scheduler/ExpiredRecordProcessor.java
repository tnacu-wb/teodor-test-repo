package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;

@Slf4j
public class ExpiredRecordProcessor implements RecordProcessor {

  @Override
  public void process(RuleEngineRepositoryOutPort<Rule> ruleEngineRepository) {
    log.info("Processing expired rules from table: {}", ruleEngineRepository.getTableName());
    ruleEngineRepository
        .findExpiredRecords()
        .forEach(rule -> processRule(ruleEngineRepository, rule));
  }

  private void processRule(RuleEngineRepositoryOutPort<Rule> ruleEngineRepository, Rule rule) {
    rule.setStatus(RuleStatus.INACTIVE);
    ruleEngineRepository.persistRecords(RuleProcessorComposite.builder()
        .recordToUpdate(rule)
        .build());
  }
}
