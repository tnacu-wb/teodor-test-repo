package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Transaction status returned by Datatrans GET /v1/transactions/{transactionId}.
 *
 * @param transactionId             the Datatrans transaction identifier
 * @param status                    the current transaction status
 * @param currency                  the transaction currency
 * @param authorizedAmount          the authorized transaction amount
 * @param acquirerAuthorizationCode the authorization code from the card acquirer
 * @param card                      card details returned with the transaction status
 * @param paymentMethod             the Datatrans payment method code (e.g. "VIS", "ECA")
 * @param refno                     the merchant reference number the transaction was created
 *                                  with, used to confirm identity when recovering from an
 *                                  "already done" conflict; may be {@code null} when the
 *                                  gateway omits it
 */
public record DatatransTransactionStatus(
    String transactionId,
    String status,
    String currency,
    Integer authorizedAmount,
    String acquirerAuthorizationCode,
    DatatransCardInfo card,
    String paymentMethod,
    String refno
) {}
