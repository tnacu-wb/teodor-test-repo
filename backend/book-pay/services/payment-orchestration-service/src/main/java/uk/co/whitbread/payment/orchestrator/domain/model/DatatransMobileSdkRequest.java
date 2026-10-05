package uk.co.whitbread.payment.orchestrator.domain.model;

import java.util.List;

/**
 * Request sent to Datatrans POST /v2/transactions for Mobile SDK initialisation.
 *
 * @param amount         total amount in minor currency units (e.g. 8600 = £86.00)
 * @param currency       ISO 4217 currency code (e.g. "GBP")
 * @param refno          reference number (e.g. "PI-123456789")
 * @param paymentMethods card brand codes (e.g. ["VIS", "ECA"])
 * @param merchantId     dynamic merchant identifier (format: deWB-{hotelId})
 * @param webhookUrl     public callback URL Datatrans posts the webhook to,
 *                       carrying the {@code basketId} correlation key as a query parameter
 *                       (e.g. "https://host/api/payments/webhooks/datatrans?basketId=b-1");
 *                       may be {@code null} or blank to omit {@code webhook.url} from the
 *                       Datatrans request
 */
public record DatatransMobileSdkRequest(
    long amount,
    String currency,
    String refno,
    List<String> paymentMethods,
    String merchantId,
    String webhookUrl
) {

  /**
   * Convenience constructor for callers that do not register a webhook callback URL.
   *
   * @param amount         total amount in minor currency units
   * @param currency       ISO 4217 currency code
   * @param refno          reference number
   * @param paymentMethods card brand codes
   * @param merchantId     dynamic merchant identifier
   */
  public DatatransMobileSdkRequest(long amount, String currency, String refno,
      List<String> paymentMethods, String merchantId) {
    this(amount, currency, refno, paymentMethods, merchantId, null);
  }
}
