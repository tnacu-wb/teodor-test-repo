package uk.co.whitbread.promo.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;

@Slf4j
@RequiredArgsConstructor
public class PromoBatchExpiryAndCleanUpScheduler {

  private final PromoBatchInPort promoBatchInPort;

  @Scheduled(cron = "${promo-service.batch.refresh}")
  public void updatePromoBatchStatusExpiryAndCleanUpJob() {
    log.info("Launching promo batch status expiry and clean up job");
    promoBatchInPort.updatePromoBatchStatusToExpiry();

    promoBatchInPort.deleteExpiredPromoCodesAfterRetention();
    log.info("Expiry job completed");
  }
}
