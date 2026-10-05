package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.paymentmethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Response DTO representing a payment method from the Payment Method Entity Service.
 *
 * @param name              the payment method name (e.g. "CARD", "PIBA", "APPLE")
 * @param type              the payment method type (e.g. "NEW_CARD", "NEW_PIBA", "AP")
 * @param paymentProvider   the payment provider identifier (e.g. "Datatrans", "3CP")
 * @param enabled           whether this payment method is currently enabled
 * @param acceptedCardTypes list of accepted card types for this payment method
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentMethodDto(
    String name,
    String type,
    String paymentProvider,
    boolean enabled,
    List<AcceptedCardTypeDto> acceptedCardTypes
) {}
