package uk.co.whitbread.rules.manager.infrastructure.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.whitbread.rules.manager.domain.ports.primary.RuleEngineSweeperInPort;

@Slf4j
@RequiredArgsConstructor
public class RuleEngineScheduler {

  private final RuleEngineSweeperInPort ruleEngineSweeperInPort;

  @Scheduled(fixedDelayString = "${rules-engine.scheduler.fixedDelay.in.ms}")
  public void tidyRules() {
    log.info("Initiating rules analysis");
    ruleEngineSweeperInPort.sweepTables();
  }
}
