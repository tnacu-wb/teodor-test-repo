package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.PaymentInitializationException;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardMobileInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardWebInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.in.AuthorizePaymentRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.in.NewCardMobileInitRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.in.NewCardWebInitRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.in.PaymentInitRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentInitResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.PaymentOrchestrationInPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.PaymentGlobalExceptionHandler;

/**
 * REST controller for payment operations.
 *
 * <p>Provides a unified polymorphic payment initialization endpoint,
 * synchronous payment authorization, and webhook handling for Datatrans callbacks.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Unified payment orchestration endpoints")
public class PaymentController {

  private final PaymentOrchestrationInPort paymentOrchestrationInPort;

  /**
   * Initialize a payment session using the unified polymorphic endpoint.
   *
   * <p>Accepts discriminated request subtypes via Jackson's {@code @JsonTypeInfo}
   * on the {@code paymentMethod} field. Maps each request subtype 1:1 to a
   * {@link PaymentInitCommand} and delegates to the workflow adapter.
   */
  @PostMapping(value = "/init",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Initialize payment session",
      description = "Creates a payment session using the specified payment method "
          + "(NEW_CARD_WEB for Secure Fields, NEW_CARD_MOBILE for Mobile SDK). "
          + "Accepts a polymorphic request discriminated by paymentMethod. "
          + "For NEW_CARD_WEB, the returnUrl must use HTTPS and its host must be "
          + "in the configured allowlist (payment.security.allowed-return-url-hosts)."
  )
  @ApiResponse(responseCode = "201", description = "Payment session created", content = {
      @Content(schema = @Schema(implementation = PaymentInitResponse.class))
  })
  @ApiResponse(responseCode = "400", description = "Invalid request (validation failed, "
      + "returnUrl not HTTPS, or returnUrl host not in allowlist)", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "404", description = "Basket not found", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "409", description = "Conflict (authorization in progress, "
      + "transaction already authorized, or invalid state for re-initialization)", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "410", description = "Payment session expired "
      + "(EXPIRED, TRANSACTION_EXPIRED)", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "422", description = "Card payment not available for this hotel "
      + "(PAYMENT_METHOD_NOT_AVAILABLE)", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "502", description = "Payment gateway error (GATEWAY_ERROR), or "
      + "the gateway rejected this service's merchant credentials "
      + "(GATEWAY_AUTHENTICATION_FAILED) — not a card problem",
      content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "500", description = "Unexpected internal error (INTERNAL_ERROR) "
      + "— a fault in this service, not a transient one; retrying is unlikely to help",
      content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "503", description = "A downstream dependency is unreachable "
      + "(SERVICE_UNAVAILABLE) — retry the request", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  public ResponseEntity<PaymentInitResponse> initPayment(
      @Valid @RequestBody PaymentInitRequest request) {
    log.info("POST /api/payments/init called with basketId={}, paymentMethod={}",
        request.basketId(), request.paymentMethod());

    PaymentInitCommand command = mapToCommand(request);
    PaymentInitResult result = paymentOrchestrationInPort.initPayment(command);

    if (!result.success()) {
      throw new PaymentInitializationException(result.errorCode(), result.errorMessage());
    }

    PaymentInitResponse response = new PaymentInitResponse(
        request.paymentMethod(), result.transactionId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Finalize payment authorization after 3-D Secure (web only).
   *
   * <p>Calls the workflow's {@code @UpdateMethod authorize()} directly for an immediate
   * synchronous response, eliminating the previous signal+polling pattern.
   */
  @PostMapping(value = "/authorize",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Authorize payment",
      description = "Finalizes the payment authorization post-3DS for web Secure Fields flow. "
          + "Returns synchronous result from the workflow update method."
  )
  @ApiResponse(responseCode = "200", description = "Authorization result returned", content = {
      @Content(schema = @Schema(implementation = AuthorizeResult.class))
  })
  @ApiResponse(responseCode = "202", description = "Authorization accepted and still processing "
      + "(AUTHORIZATION_PENDING) — the payment may still succeed. Poll "
      + "GET /api/payments/{basketId}/status for the outcome; do not retry this call",
      content = {
      @Content(schema = @Schema(implementation = AuthorizeResult.class))
  })
  @ApiResponse(responseCode = "400", description = "Invalid request", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "404", description = "Basket not found (BASKET_NOT_FOUND), or "
      + "no payment workflow exists for the basket — authorize called before init, or the "
      + "workflow already closed (TRANSACTION_NOT_FOUND)",
      content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "409", description = "Conflict — authorization already in "
      + "progress, transaction already authorized, or payment not in a state that allows "
      + "authorization (AUTHORIZATION_IN_PROGRESS, TRANSACTION_ALREADY_AUTHORIZED, "
      + "INVALID_TRANSACTION_STATE)", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "410", description = "Transaction expired (TRANSACTION_EXPIRED)",
      content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  @ApiResponse(responseCode = "502", description = "Payment gateway error (GATEWAY_ERROR), or "
      + "the gateway rejected this service's merchant credentials "
      + "(GATEWAY_AUTHENTICATION_FAILED) — not a card problem",
      content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  public ResponseEntity<AuthorizeResult> authorizePayment(
      @Valid @RequestBody AuthorizePaymentRequest request) {
    log.info("POST /api/payments/authorize called with basketId={}", request.basketId());

    AuthorizeResult result = paymentOrchestrationInPort.authorizePayment(request.basketId());

    // "Still processing" is not a failure: the update is durably accepted and the workflow is
    // still working on it. Mapping it to an error would tell the customer their payment failed
    // while it may yet be charged, and would invite a retry that double-charges.
    if (result.errorCode() == PaymentErrorCode.AUTHORIZATION_PENDING) {
      log.info("Authorization still processing for basketId={}; returning 202",
          request.basketId());
      return ResponseEntity.accepted().body(result);
    }

    if (!result.success()) {
      // The typed code flows straight through to the status mapping in PaymentExceptionHandler
      // (TRANSACTION_NOT_FOUND → 404, GATEWAY_ERROR → 502, ...). A failed result with no code
      // has no better answer than a gateway fault.
      throw new PaymentInitializationException(
          result.errorCode() != null ? result.errorCode() : PaymentErrorCode.GATEWAY_ERROR,
          result.errorMessage());
    }

    return ResponseEntity.ok(result);
  }

  /**
   * Read the current payment status for a basket.
   *
   * <p>The poll target behind the {@code 202} from {@code POST /api/payments/authorize}: served
   * by workflow queries, so it never changes the payment and is safe to call repeatedly.
   */
  @GetMapping(value = "/{basketId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get payment status",
      description = "Returns the workflow's current payment status, plus the authorization "
          + "result once one exists. Poll this after a 202 from /api/payments/authorize."
  )
  @ApiResponse(responseCode = "200", description = "Current payment status", content = {
      @Content(schema = @Schema(implementation = PaymentStatusResponse.class))
  })
  @ApiResponse(responseCode = "404", description = "No payment found for this basket "
      + "(BASKET_NOT_FOUND)", content = {
      @Content(schema = @Schema(implementation =
          PaymentGlobalExceptionHandler.GlobalErrorResponse.class))
  })
  public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
      @PathVariable("basketId") String basketId) {
    log.info("GET /api/payments/{}/status called", basketId);

    return ResponseEntity.ok(paymentOrchestrationInPort.getPaymentStatus(basketId));
  }

  /**
   * Maps a polymorphic {@link PaymentInitRequest} to its corresponding
   * {@link PaymentInitCommand} subtype.
   *
   * <p>Deliberately an exhaustive switch over the sealed hierarchy rather than the MapStruct
   * mapper the service convention prefers (see structure.md, Key Conventions): with no
   * {@code default} branch, adding a new payment method fails to compile until it is mapped
   * here, whereas {@code @SubclassMapping} would only fail at runtime on the first live
   * request. The fields themselves map 1:1, so MapStruct would add nothing but the weaker
   * guarantee.
   */
  private PaymentInitCommand mapToCommand(PaymentInitRequest request) {
    return switch (request) {
      case NewCardWebInitRequest web -> new NewCardWebInitCommand(
          web.basketId(), web.returnUrl(),
          web.country(), web.language(), web.userType(), web.clientChannel());
      case NewCardMobileInitRequest mobile -> new NewCardMobileInitCommand(
          mobile.basketId(),
          mobile.country(), mobile.language(), mobile.userType(), mobile.clientChannel());
    };
  }
}
