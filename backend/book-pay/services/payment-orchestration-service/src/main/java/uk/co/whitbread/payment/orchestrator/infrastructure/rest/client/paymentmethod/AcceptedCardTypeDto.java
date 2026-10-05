package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.paymentmethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO representing an accepted card type within a payment method response.
 *
 * @param type the card type code (e.g. "VIS", "ECA", "AMX", "DIN")
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AcceptedCardTypeDto(
    String type
) {}
