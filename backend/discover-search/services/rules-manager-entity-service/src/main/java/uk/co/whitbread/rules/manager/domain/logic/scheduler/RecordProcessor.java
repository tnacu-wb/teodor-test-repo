package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;

public interface RecordProcessor {

  void process(
      RuleEngineRepositoryOutPort<Rule> ruleEngineRepository);
}
