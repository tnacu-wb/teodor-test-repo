package uk.co.whitbread.payment.orchestrator.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Result returned synchronously from the workflow's {@code authorize()} update method.
 *
 * <p>Pre-condition failures return immediately without blocking.
 *
 * <p>One error code is not a failure: {@code AUTHORIZATION_PENDING} means the authorization was
 * durably accepted and is still running past the caller's bounded wait. It is returned with
 * {@code 202 Accepted}, and the caller polls the payment status endpoint rather than retrying.
 *
 * @param success      whether the authorization succeeded
 * @param errorCode    machine-readable error code (null on success)
 * @param errorMessage human-readable error description (null on success)
 */
@Schema(description = "Result returned synchronously from the workflow authorize update")
public record AuthorizeResult(

    @Schema(description = "Whether the authorization succeeded", example = "true")
    boolean success,

    @Schema(description = "Machine-readable error code, null on success", nullable = true)
    PaymentErrorCode errorCode,

    @Schema(description = "Human-readable error description, null on success", nullable = true)
    String errorMessage
) {}
