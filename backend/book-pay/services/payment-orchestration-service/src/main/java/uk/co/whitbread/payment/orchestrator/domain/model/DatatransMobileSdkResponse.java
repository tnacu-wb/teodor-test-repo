package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Response from Datatrans POST /v2/transactions for Mobile SDK initialisation.
 *
 * @param transactionId the Datatrans transaction identifier in UUID format
 */
public record DatatransMobileSdkResponse(
    String transactionId
) {}
