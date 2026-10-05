package uk.co.whitbread.payment.orchestrator.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Datatrans-specific webhook payload received after a payment event.
 *
 * <p>This record carries the fields relevant to workflow processing from a Datatrans webhook
 * callback. It is constructed by the controller layer from the raw
 * {@link uk.co.whitbread.payment.orchestrator.domain.model.payment.in.MobileSdkWebhookRequest}
 * HTTP payload.
 *
 * <p>The record deliberately does <em>not</em> expose a {@code paymentMethod()} accessor —
 * webhooks are gateway-scoped, not method-scoped. The workflow correlates the payload to a
 * running transaction via {@link #transactionId()} and lets the active strategy decide how to
 * process it.
 *
 * @param transactionId    the Datatrans transaction identifier (correlation key)
 * @param merchantId       the merchant identifier
 * @param status           the transaction status from Datatrans (e.g. "authorized", "canceled")
 * @param currency         the three-letter ISO 4217 currency code
 * @param refno            the Datatrans reference number (booking reference set at init)
 * @param paymentMethod    the payment method code from Datatrans (e.g. "VIS", "ECA")
 * @param authorizedAmount the authorized amount in minor units (pence/cents)
 * @param cardAlias        the tokenised card alias, or {@code null} if not present
 * @param maskedCardNumber the masked card number (e.g. "424242xxxxxx4242"), or {@code null}
 * @param expiryMonth      the card expiry month (MM), or {@code null}
 * @param expiryYear       the card expiry year (YY), or {@code null}
 * @param acquirerAuthorizationCode the authorization code from the card acquirer, or {@code null}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DatatransWebhookPayload(
    String transactionId,
    String merchantId,
    String status,
    String currency,
    String refno,
    String paymentMethod,
    Integer authorizedAmount,
    String cardAlias,
    String maskedCardNumber,
    String expiryMonth,
    String expiryYear,
    String acquirerAuthorizationCode
) implements WebhookPayload {}
