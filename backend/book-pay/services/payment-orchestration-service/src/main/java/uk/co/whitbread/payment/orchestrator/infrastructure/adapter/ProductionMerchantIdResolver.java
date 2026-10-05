package uk.co.whitbread.payment.orchestrator.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.MerchantIdResolver;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.MerchantIdProperties;

/**
 * Production implementation of {@link MerchantIdResolver}.
 *
 * <p>Active only when the {@code opera-prod} Spring profile is enabled. In production every
 * hotel has a provisioned Datatrans merchant account, so the merchant ID is constructed
 * directly from the configured prefix and the hotel code.
 */
@Slf4j
@Component
@Profile("opera-prod")
@RequiredArgsConstructor
public class ProductionMerchantIdResolver implements MerchantIdResolver {

  private final MerchantIdProperties properties;

  @Override
  public String resolveMerchantId(String hotelCode) {
    if (hotelCode == null || hotelCode.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Hotel code must not be null or blank in production — "
              + "cannot resolve a Datatrans merchant ID without a valid hotel code");
    }
    String merchantId = properties.getPrefix() + hotelCode;
    log.debug("Resolved merchant ID for hotel {}: {}", hotelCode, merchantId);
    return merchantId;
  }
}
