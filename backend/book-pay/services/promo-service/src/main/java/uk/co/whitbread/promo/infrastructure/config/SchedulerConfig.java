package uk.co.whitbread.promo.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;
import uk.co.whitbread.promo.infrastructure.scheduler.PromoBatchExpiryAndCleanUpScheduler;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "promo-service.scheduler.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SchedulerConfig {

  @Bean
  public PromoBatchExpiryAndCleanUpScheduler getPromoBatchExpiryAndCleanUpScheduler(
        PromoBatchInPort promoBatchInPort) {
    return new PromoBatchExpiryAndCleanUpScheduler(promoBatchInPort);
  }
}
