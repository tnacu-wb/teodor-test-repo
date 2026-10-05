package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Response from Datatrans POST /v1/transactions/secureFields.
 *
 * @param transactionId the Datatrans transaction identifier (valid for 30 minutes)
 */
public record DatatransSecureFieldsResponse(
    String transactionId
) {}
