package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.ports.primary.RuleEngineSweeperInPort;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class RuleEngineSweeperInPortImpl implements RuleEngineSweeperInPort {

  private final List<? extends RuleEngineRepositoryOutPort<? extends Rule>> ruleEngineRepositories;
  private final RecordProcessorPipeline processorPipeline;

  @Override
  public void sweepTables() {
    log.info("Fetching records from rule tables in parallel");
    ruleEngineRepositories.forEach(this::processAllSetOfRules);
  }

  private void processAllSetOfRules(
      RuleEngineRepositoryOutPort<? extends Rule> ruleEngineRepository) {
    processorPipeline.getSteps()
        .forEach(
            onStep -> onStep.process((RuleEngineRepositoryOutPort<Rule>) ruleEngineRepository));
  }
}
