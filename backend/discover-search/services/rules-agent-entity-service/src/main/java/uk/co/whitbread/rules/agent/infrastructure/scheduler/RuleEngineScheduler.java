package uk.co.whitbread.rules.agent.infrastructure.scheduler;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;

@Slf4j
@RequiredArgsConstructor
public class RuleEngineScheduler {

  private final RuleEngineCacheManagerInPort ruleEngineCacheManagerInPort;
  private final AtomicBoolean isDataLoaded = new AtomicBoolean(false);

  @PostConstruct
  public void initialDataLoad() {
    log.info("Initiating initial data load in-memory");
    ruleEngineCacheManagerInPort.loadRulesInMemory();
    isDataLoaded.set(true);
  }

  @Scheduled(cron = "${rules-engine.scheduler.cron.regex}")
  public void updateData() {
    if (isDataLoaded.get()) {
      log.info("Initiating rules update");
      ruleEngineCacheManagerInPort.syncRulesInMemory();
    }
  }
}
