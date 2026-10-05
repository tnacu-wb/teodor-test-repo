package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Externalised security configuration for payment initialization.
 *
 * <p>Maps to the {@code payment.security} section in application.yml. The allowlist is bound
 * through {@code @ConfigurationProperties} rather than {@code @Value} because a YAML sequence
 * is not a single resolvable placeholder value.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "payment.security")
public class PaymentSecurityProperties {

  /**
   * Hosts a {@code returnUrl} may point at. Requests whose returnUrl host is absent from this
   * list are rejected, which closes the open-redirect vector. An empty list rejects every
   * returnUrl, so deployed environments must configure it.
   */
  private List<String> allowedReturnUrlHosts = new ArrayList<>();
}
