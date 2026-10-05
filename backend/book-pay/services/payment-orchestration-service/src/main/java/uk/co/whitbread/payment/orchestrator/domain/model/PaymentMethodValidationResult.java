package uk.co.whitbread.payment.orchestrator.domain.model;

import java.util.List;

/**
 * Result of payment method validation for a given hotel.
 *
 * @param cardPaymentAvailable whether card payment is available for the hotel
 * @param availableCardBrands  list of available card brand codes (e.g. VIS, ECA, AMX)
 */
public record PaymentMethodValidationResult(
    boolean cardPaymentAvailable,
    List<String> availableCardBrands
) {}
