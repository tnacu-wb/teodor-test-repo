package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Externalised configuration for the Datatrans webhook callback.
 *
 * <p>Maps to the {@code integrations.datatrans.webhook} section in application.yml.
 * The HMAC signing key is injected via an environment variable sourced from
 * AWS Secrets Manager in deployed environments and is never logged.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "integrations.datatrans.webhook")
public class DatatransWebhookProperties {

  /**
   * Public base URL Datatrans calls back on. The {@code basketId} correlation key is appended
   * as a query parameter when the Mobile SDK transaction is initialised.
   */
  private String callbackBaseUrl;

  /** Hex-encoded HMAC sign key used to verify the {@code Datatrans-Signature} header. */
  private String hmacKey;

  /**
   * Whether webhook signature validation is performed. Documented escape hatch for local and
   * integration profiles only; it MUST remain {@code true} in deployed environments.
   */
  private boolean validationEnabled = true;
}
