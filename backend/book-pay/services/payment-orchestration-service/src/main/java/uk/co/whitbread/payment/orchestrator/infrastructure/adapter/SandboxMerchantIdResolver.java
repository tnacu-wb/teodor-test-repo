package uk.co.whitbread.payment.orchestrator.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.MerchantIdResolver;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.MerchantIdProperties;

/**
 * Sandbox implementation of {@link MerchantIdResolver}.
 *
 * <p>Active when the {@code opera-prod} profile is NOT enabled. Resolves merchant IDs
 * by checking whether the hotel code is provisioned in the Datatrans sandbox. Hotels
 * that are not provisioned fall back to a configured default merchant ID.
 */
@Slf4j
@Component
@Profile("!opera-prod")
@RequiredArgsConstructor
public class SandboxMerchantIdResolver implements MerchantIdResolver {

  private final MerchantIdProperties properties;

  @Override
  public String resolveMerchantId(String hotelCode) {
    if (hotelCode == null || hotelCode.trim().isEmpty()) {
      log.warn("Hotel code is null or empty, using default merchant ID");
      return properties.getDefaultMerchantId();
    }

    List<String> provisionedHotels = properties.getProvisionedHotels();
    if (provisionedHotels != null && provisionedHotels.contains(hotelCode)) {
      String merchantId = properties.getPrefix() + hotelCode;
      log.debug("Hotel {} is provisioned in sandbox, using specific merchant ID: {}",
          hotelCode, merchantId);
      return merchantId;
    }

    String merchantId = properties.getDefaultMerchantId();
    log.debug("Hotel {} not provisioned in sandbox, using default merchant ID: {}",
        hotelCode, merchantId);
    return merchantId;
  }
}
