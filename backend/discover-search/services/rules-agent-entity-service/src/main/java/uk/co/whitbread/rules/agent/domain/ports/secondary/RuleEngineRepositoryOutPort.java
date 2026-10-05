package uk.co.whitbread.rules.agent.domain.ports.secondary;

import uk.co.whitbread.rules.agent.domain.model.out.Rule;

public interface RuleEngineRepositoryOutPort<T extends Rule> {

  void cacheRules();

  void updateCache();

  default T getEntity() {
    return null;
  }
}
