package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

/**
 * Secondary port for resolving Datatrans merchant IDs based on hotel codes.
 *
 * <p>Defines the outbound contract for determining the correct Datatrans
 * merchant ID to use when initialising payment transactions. The resolution
 * strategy differs by environment: production maps hotel codes directly,
 * while sandbox environments use a provisioned-hotel lookup with a default
 * fallback.
 */
public interface MerchantIdResolver {

  /**
   * Resolve the appropriate Datatrans merchant ID for the given hotel code.
   *
   * @param hotelCode the hotel identifier (e.g. "HARHOR", "GRESOU")
   * @return the full Datatrans merchant ID (e.g. "deWB-HARHOR")
   */
  String resolveMerchantId(String hotelCode);
}
