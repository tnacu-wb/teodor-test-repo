package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Response from Datatrans POST /v1/transactions/{transactionId}/authorize.
 *
 * @param transactionId             the Datatrans transaction identifier
 * @param status                    the authorization status (e.g. "authorized")
 * @param acquirerAuthorizationCode the authorization code from the card acquirer
 * @param card                      card details returned with the authorization
 * @param paymentMethod             the Datatrans payment method code (e.g. "VIS", "ECA"), nullable
 */
public record DatatransAuthorizeResponse(
    String transactionId,
    String status,
    String acquirerAuthorizationCode,
    DatatransCardInfo card,
    String paymentMethod
) {}
