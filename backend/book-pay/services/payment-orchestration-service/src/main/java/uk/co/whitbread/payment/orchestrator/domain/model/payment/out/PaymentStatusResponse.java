package uk.co.whitbread.payment.orchestrator.domain.model.payment.out;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;

/**
 * Current state of a payment, read from the running workflow.
 *
 * <p>This is the poll target for a caller that got {@code 202 Accepted} from
 * {@code POST /api/payments/authorize}: the authorization was durably accepted but had not
 * finished within the bounded wait. The caller polls here until {@code authorizeResult} is
 * present, or until {@code paymentStatus} leaves {@code INITIALIZED}.
 *
 * @param paymentStatus   the workflow's current payment status
 * @param authorizeResult the most recent authorization outcome, or {@code null} if the
 *                        authorization has not produced a result yet
 */
@Schema(description = "Current payment status, plus the authorization outcome when available")
@JsonPropertyOrder({"paymentStatus", "authorizeResult"})
public record PaymentStatusResponse(

    @Schema(description = "The workflow's current payment status", example = "INITIALIZED")
    PaymentStatus paymentStatus,

    @Schema(description = "The most recent authorization outcome, null while still processing",
        nullable = true)
    AuthorizeResult authorizeResult
) {}
