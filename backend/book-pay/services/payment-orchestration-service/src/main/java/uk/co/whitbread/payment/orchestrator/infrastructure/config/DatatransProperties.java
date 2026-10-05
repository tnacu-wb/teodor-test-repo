package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalised configuration for Datatrans integration.
 *
 * <p>Maps to the {@code integrations.datatrans} section in application.yml.
 * Credentials are injected via the {@code DATATRANS_BASE_URL} and
 * {@code DATATRANS_MERCHANT_PASSWORD} environment variables, sourced from
 * AWS Secrets Manager in deployed environments.
 *
 * <p>Validated at startup on purpose. The checked-in default for the merchant password is the
 * empty string, so without those environment variables the service used to boot happily and
 * only discover it had no credentials on the first live payment — a customer-facing failure
 * standing in for a deployment error. Failing the context refresh instead surfaces it to
 * whoever deployed it. Local, test, and integration profiles supply placeholder values.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "integrations.datatrans")
public class DatatransProperties {

  /** Base URL for the Datatrans API (e.g. https://api.sandbox.datatrans.com). */
  @NotBlank(message = "integrations.datatrans.base-url must not be blank")
  private String baseUrl;

  /** Merchant password used for HTTP Basic Authentication. */
  @NotBlank(message = "integrations.datatrans.merchant-password must not be blank")
  private String merchantPassword;

  /** HTTP connection timeout for Datatrans API calls. */
  private Duration connectTimeout;

  /** HTTP read timeout for Datatrans API calls. */
  private Duration readTimeout;
}
