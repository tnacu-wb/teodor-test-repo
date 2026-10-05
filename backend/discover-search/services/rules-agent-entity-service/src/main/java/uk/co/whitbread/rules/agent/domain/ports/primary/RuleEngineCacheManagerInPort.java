package uk.co.whitbread.rules.agent.domain.ports.primary;

public interface RuleEngineCacheManagerInPort {

  void loadRulesInMemory();

  void syncRulesInMemory();
}
