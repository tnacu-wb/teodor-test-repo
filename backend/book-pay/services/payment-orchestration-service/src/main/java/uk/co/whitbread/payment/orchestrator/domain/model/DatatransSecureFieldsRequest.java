package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Request sent to Datatrans POST /v2/transactions/secure-fields.
 *
 * @param amount       total amount in minor currency units
 * @param currency     ISO 4217 currency code
 * @param returnUrl    3-D Secure redirect URL
 * @param merchantId   dynamic merchant identifier (format: deWB-{hotelId})
 * @param returnMethod HTTP method for the 3DS redirect back to returnUrl (default: "POST")
 */
public record DatatransSecureFieldsRequest(
    long amount,
    String currency,
    String returnUrl,
    String merchantId,
    String returnMethod
) {}
