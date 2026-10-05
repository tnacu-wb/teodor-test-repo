package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalised configuration for Payment Method Entity Service integration.
 *
 * <p>Maps to the {@code integrations.payment-method-service} section in application.yml.
 * The host is configurable per environment via the {@code PAYMENT_METHOD_SERVICE_HOST}
 * environment variable.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "integrations.payment-method-service")
public class PaymentMethodProperties {

  private static final String CONNECT_TIMEOUT_KEY =
      "integrations.payment-method-service.connect-timeout";
  private static final String READ_TIMEOUT_KEY =
      "integrations.payment-method-service.read-timeout";

  /** Host URL for the Payment Method Entity Service. */
  @NotBlank(message = "integrations.payment-method-service.host must not be blank")
  private String host = "http://localhost:9107";

  /** Endpoint path for retrieving payment methods. */
  private String paymentMethodsEndpoint = "/v1/payment-methods";

  /** HTTP connection timeout for Payment Method Entity Service calls. */
  @PositiveDuration(message = CONNECT_TIMEOUT_KEY + " must be positive")
  private Duration connectTimeout = Duration.ofSeconds(5);

  /** HTTP read timeout for Payment Method Entity Service calls. */
  @PositiveDuration(message = READ_TIMEOUT_KEY + " must be positive")
  private Duration readTimeout = Duration.ofSeconds(10);
}
