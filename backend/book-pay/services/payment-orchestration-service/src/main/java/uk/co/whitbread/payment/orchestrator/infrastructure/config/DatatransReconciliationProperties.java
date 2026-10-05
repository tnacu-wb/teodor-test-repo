package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalised configuration for the Datatrans transaction reconciliation poller.
 *
 * <p>Maps to the {@code integrations.datatrans.reconciliation} section in application.yml.
 * Duration values use Spring Boot's duration notation, such as {@code 2m} and {@code 30s}.
 */
@Data
@Validated
@ValidReconciliationDurationRange
@Configuration
@ConfigurationProperties(prefix = "integrations.datatrans.reconciliation")
public class DatatransReconciliationProperties {

  private static final String INITIAL_DELAY_KEY =
      "integrations.datatrans.reconciliation.initial-delay";
  private static final String POLL_INTERVAL_KEY =
      "integrations.datatrans.reconciliation.poll-interval";
  private static final String MAX_DURATION_KEY =
      "integrations.datatrans.reconciliation.max-duration";

  /** Delay after initialisation before the first status poll. */
  @NotNull(message = INITIAL_DELAY_KEY + " must be positive")
  @PositiveDuration(message = INITIAL_DELAY_KEY + " must be positive")
  private Duration initialDelay = Duration.ofMinutes(2);

  /** Fixed interval between status polls. */
  @NotNull(message = POLL_INTERVAL_KEY + " must be positive")
  @PositiveDuration(message = POLL_INTERVAL_KEY + " must be positive")
  private Duration pollInterval = Duration.ofSeconds(30);

  /** Maximum time spent reconciling a transaction after initialisation. */
  @NotNull(message = MAX_DURATION_KEY + " must be positive")
  @PositiveDuration(message = MAX_DURATION_KEY + " must be positive")
  private Duration maxDuration = Duration.ofMinutes(30);

  /** Whether the reconciliation poller is enabled. */
  private boolean enabled = true;
}
