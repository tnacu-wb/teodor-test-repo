package uk.co.whitbread.rules.agent.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.Rule;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RuleEngineRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class RuleEngineCacheManagerInPortImpl implements RuleEngineCacheManagerInPort {

  private final List<? extends RuleEngineRepositoryOutPort<? extends Rule>> ruleEngineRepositories;

  @Override
  public void loadRulesInMemory() {
    ruleEngineRepositories
        .parallelStream()
        .forEach(RuleEngineRepositoryOutPort::cacheRules);
  }

  @Override
  public void syncRulesInMemory() {
    ruleEngineRepositories
        .parallelStream()
        .forEach(RuleEngineRepositoryOutPort::updateCache);
  }
}

