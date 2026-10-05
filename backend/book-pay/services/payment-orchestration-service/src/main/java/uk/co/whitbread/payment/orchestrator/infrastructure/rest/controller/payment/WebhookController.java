package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.in.MobileSdkWebhookRequest;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.WebhookInPort;

/**
 * REST controller for Datatrans webhook callbacks.
 *
 * <p>These endpoints are server-to-server callbacks from Datatrans only; they are never
 * invoked by the mobile app. Datatrans does not retry on a non-2xx response, so the
 * controller acknowledges anything it can process (including duplicates and webhooks that
 * cannot be correlated to a payment) with {@code 200 OK} and reserves non-2xx for genuine
 * rejects: {@code 401} for a bad signature and {@code 400} for a body it cannot read.
 *
 * <p>The body is accepted as raw bytes because the HMAC is computed over the exact bytes
 * Datatrans signed — re-serialising a parsed model would not reproduce them. Deserialization
 * therefore happens only after signature validation succeeds. Card data is never logged.
 *
 * <p>The endpoint is gateway-scoped (not method-scoped): the same {@code /datatrans} path
 * serves webhooks for both web Secure Fields and Mobile SDK payment events. The workflow
 * correlates the payload to a running transaction via the {@code transactionId}.
 *
 * <p>The mapper is the Jackson 3 {@link ObjectMapper} ({@code tools.jackson.databind}) that
 * Spring Boot 4 auto-configures as a {@code JsonMapper} bean, so this endpoint reads JSON with
 * exactly the application's configured behaviour. Jackson 2 is still on the classpath
 * transitively, but Boot 4 registers no {@code com.fasterxml.jackson.databind.ObjectMapper}
 * bean, and constructing one here would silently diverge from the rest of the service.
 */
@RestController
@RequestMapping("/api/payments/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks",
    description = "Datatrans webhook callback endpoints (gateway-scoped)")
public class WebhookController {

  private static final String STATUS_KEY = "status";
  private static final Map<String, String> RECEIVED_RESPONSE = Map.of(STATUS_KEY, "received");
  private static final Map<String, String> UNAUTHORIZED_RESPONSE =
      Map.of(STATUS_KEY, "unauthorized");
  private static final Map<String, String> INVALID_RESPONSE = Map.of(STATUS_KEY, "invalid");

  private final WebhookSignatureValidator signatureValidator;
  private final WebhookInPort webhookInPort;
  private final ObjectMapper objectMapper;

  /**
   * Handle a Datatrans webhook callback.
   *
   * <p>Validates the {@code Datatrans-Signature} HMAC against the raw body, deserializes the
   * payload, maps it to the gateway-scoped domain model, and hands workflow progression off
   * asynchronously via a Temporal signal so the response returns promptly.
   */
  @PostMapping(value = "/datatrans",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Datatrans webhook",
      description = "Server-to-server callback from Datatrans after a payment event (web Secure "
          + "Fields or Mobile SDK). The Datatrans-Signature HMAC is verified against the raw "
          + "request body, then the payment workflow correlated by the basketId query parameter "
          + "is signalled asynchronously. Acknowledged with 200 even when the webhook is a "
          + "duplicate or cannot be correlated to a payment, because Datatrans does not retry "
          + "on non-2xx."
  )
  @ApiResponse(responseCode = "200",
      description = "Webhook accepted for processing (also returned for duplicate webhooks and "
          + "webhooks that cannot be correlated to a payment)",
      content = {
          @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(type = "object", additionalPropertiesSchema = String.class),
              examples = @ExampleObject(value = "{\"status\":\"received\"}"))
      })
  @ApiResponse(responseCode = "400",
      description = "Request body is missing, is not valid JSON, or omits transactionId/status",
      content = {
          @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(type = "object", additionalPropertiesSchema = String.class),
              examples = @ExampleObject(value = "{\"status\":\"invalid\"}"))
      })
  @ApiResponse(responseCode = "401",
      description = "Datatrans-Signature header is missing or the signature is invalid",
      content = {
          @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(type = "object", additionalPropertiesSchema = String.class),
              examples = @ExampleObject(value = "{\"status\":\"unauthorized\"}"))
      })
  public ResponseEntity<Map<String, String>> handleDatatransWebhook(
      @Parameter(description = "Basket identifier correlating this webhook to a running "
          + "payment workflow")
      @RequestParam(name = "basketId", required = false) String basketId,
      @Parameter(description = "HMAC signature Datatrans computed over the raw request body")
      @RequestHeader(name = "Datatrans-Signature", required = false) String signature,
      @RequestBody(required = false) byte[] rawBody) {

    String body = rawBody == null || rawBody.length == 0
        ? null : new String(rawBody, StandardCharsets.UTF_8);

    if (body == null || body.isBlank()) {
      log.warn("Rejected Datatrans webhook: request body is missing or blank");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_RESPONSE);
    }

    if (!signatureValidator.isValid(body, signature)) {
      log.warn("Rejected Datatrans webhook: signature validation failed");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_RESPONSE);
    }

    MobileSdkWebhookRequest httpPayload = deserialize(body);
    if (httpPayload == null || isBlank(httpPayload.transactionId())
        || isBlank(httpPayload.status())) {
      log.warn("Rejected Datatrans webhook: body missing or missing required fields");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_RESPONSE);
    }

    DatatransWebhookPayload domainPayload = mapToDomainPayload(httpPayload);

    log.info("Datatrans webhook received [basketId={}, transactionId={}, status={}, refno={}]",
        basketId, domainPayload.transactionId(), domainPayload.status(), domainPayload.refno());

    webhookInPort.handleWebhook(basketId, domainPayload);
    return ResponseEntity.ok(RECEIVED_RESPONSE);
  }

  /**
   * Maps the HTTP request DTO to the gateway-scoped domain model.
   *
   * <p>Extracts card and attempt details from the nested HTTP payload structure into the
   * flat domain record that the workflow consumes via the event-inbox pattern.
   *
   * @param httpPayload the deserialized HTTP request body
   * @return the domain-level webhook payload for workflow signalling
   */
  private DatatransWebhookPayload mapToDomainPayload(MobileSdkWebhookRequest httpPayload) {
    String cardAlias = httpPayload.card() != null ? httpPayload.card().alias() : null;
    String maskedCardNumber = httpPayload.card() != null ? httpPayload.card().masked() : null;
    String expiryMonth = httpPayload.card() != null ? httpPayload.card().expiryMonth() : null;
    String expiryYear = httpPayload.card() != null ? httpPayload.card().expiryYear() : null;

    String acquirerAuthorizationCode = null;
    if (httpPayload.attempts() != null && !httpPayload.attempts().isEmpty()) {
      acquirerAuthorizationCode =
          httpPayload.attempts().getFirst().acquirerAuthorizationCode();
    }

    return new DatatransWebhookPayload(
        httpPayload.transactionId(),
        httpPayload.merchantId(),
        httpPayload.status(),
        httpPayload.currency(),
        httpPayload.refno(),
        httpPayload.paymentMethod(),
        httpPayload.authorizedAmount(),
        cardAlias,
        maskedCardNumber,
        expiryMonth,
        expiryYear,
        acquirerAuthorizationCode
    );
  }

  /**
   * Deserializes the validated raw body into the webhook HTTP payload DTO.
   *
   * @param body the raw request body, may be {@code null}
   * @return the deserialized payload, or {@code null} when the body is absent or unreadable
   */
  private MobileSdkWebhookRequest deserialize(String body) {
    if (body == null || body.isBlank()) {
      return null;
    }
    try {
      return objectMapper.readValue(body, MobileSdkWebhookRequest.class);
    } catch (JacksonException e) {
      // Message only: never echo the body or a stack trace, it may carry card data.
      log.warn("Unreadable Datatrans webhook body [{}]", e.getOriginalMessage());
      return null;
    }
  }

  /**
   * Null-safe blank check for a required payload field.
   *
   * @param value the field value
   * @return {@code true} when the value is {@code null} or blank
   */
  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
