package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalised configuration for Basket Service integration.
 *
 * <p>Maps to the {@code integrations.basket} section in application.yml.
 * The host is configurable per environment via the {@code BASKET_SERVICE_HOST}
 * environment variable.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "integrations.basket")
public class BasketProperties {

  /** Base URL for the Basket Service. */
  @NotBlank(message = "integrations.basket.host must not be blank")
  private String host = "http://localhost:8080";

  /** Endpoint path template for changing basket status. */
  private String changeStatusEndpoint = "/v1/baskets/{bookingReference}/changeStatus";

  /**
   * Endpoint path template for reading a basket by its reference.
   *
   * <p>Used by the payment workflow's booking-completion poll to reconcile a booking outcome
   * when the {@code bookingCompleted} Kafka event does not arrive.
   */
  private String basketEndpoint = "/v1/baskets/{basketReference}";

  /** HTTP connection timeout for basket service calls. */
  private Duration connectTimeout = Duration.ofSeconds(5);

  /** HTTP read timeout for basket service calls. */
  private Duration readTimeout = Duration.ofSeconds(10);
}
