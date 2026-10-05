package uk.co.whitbread.rules.agent.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;
import uk.co.whitbread.rules.agent.infrastructure.scheduler.RuleEngineScheduler;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "rules-engine.scheduler.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SchedulerConfig {

  @Bean
  public RuleEngineScheduler ruleEngineScheduler(
      RuleEngineCacheManagerInPort ruleEngineCacheManagerInPort) {
    return new RuleEngineScheduler(ruleEngineCacheManagerInPort);
  }
}
