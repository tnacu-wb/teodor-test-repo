package uk.co.whitbread.promo.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;

@Component
@Slf4j
@ConditionalOnProperty(
    name = "app.batch-recovery.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class BatchRecoveryEventListener {

  private final PromoBatchInPort promoBatchInPort;

  public BatchRecoveryEventListener(PromoBatchInPort promoBatchInPort) {
    this.promoBatchInPort = promoBatchInPort;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    log.info("Application ready, triggering batch recovery");
    try {
      promoBatchInPort.recoverInterruptedBatches();
    } catch (Exception e) {
      log.error("Batch recovery failed", e);
    }
  }
}