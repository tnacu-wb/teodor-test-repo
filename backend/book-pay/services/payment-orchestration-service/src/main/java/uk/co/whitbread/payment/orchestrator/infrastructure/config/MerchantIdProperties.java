package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Externalised configuration for Datatrans merchant ID resolution.
 *
 * <p>Maps to the {@code integrations.datatrans.merchant-id} section in application.yml.
 * Used by the sandbox resolver to determine which hotels have provisioned merchant IDs
 * and what default to fall back to for non-provisioned hotels.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "integrations.datatrans.merchant-id")
public class MerchantIdProperties {

  /**
   * Merchant ID prefix for all Datatrans merchants.
   * Default: "deWB-" (Datatrans convention for Whitbread).
   */
  private String prefix = "deWB-";

  /**
   * Default merchant ID for hotels not provisioned in sandbox.
   * Default: "deWB-default".
   */
  private String defaultMerchantId = "deWB-default";

  /**
   * List of hotel codes provisioned in Datatrans sandbox.
   * Only used by {@code SandboxMerchantIdResolver}.
   */
  private List<String> provisionedHotels = List.of("HARHOR", "GRESOU");

  /** Validates that required properties are present and non-blank. */
  @PostConstruct
  public void validate() {
    if (prefix == null || prefix.trim().isEmpty()) {
      throw new IllegalStateException(
          "integrations.datatrans.merchant-id.prefix must not be null or empty");
    }
    if (defaultMerchantId == null || defaultMerchantId.trim().isEmpty()) {
      throw new IllegalStateException(
          "integrations.datatrans.merchant-id.default-merchant-id must not be null or empty");
    }
  }
}
