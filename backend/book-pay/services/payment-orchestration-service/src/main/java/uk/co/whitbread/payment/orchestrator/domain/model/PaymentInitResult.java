package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Result returned from the unified workflow after processing a payment initialization request.
 *
 * @param success       whether the initialization succeeded
 * @param transactionId the Datatrans transaction identifier (null on failure)
 * @param errorCode     typed error code (null on success)
 * @param errorMessage  human-readable error description (null on success)
 */
public record PaymentInitResult(
    boolean success,
    String transactionId,
    PaymentErrorCode errorCode,
    String errorMessage
) {}
