package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Card information from a Datatrans authorize response.
 *
 * @param alias       the tokenised card alias
 * @param masked      the masked card number (e.g. "424242xxxxxx4242")
 * @param expiryMonth the card expiry month (MM format)
 * @param expiryYear  the card expiry year (YY format)
 */
public record DatatransCardInfo(
    String alias,
    String masked,
    String expiryMonth,
    String expiryYear
) {}
