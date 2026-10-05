package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.datatrans;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionMismatchException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.DatatransOutPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.ErrorBodyReader;

/**
 * REST client adapter for the Datatrans payment gateway.
 *
 * <p>Implements the Secure Fields integration via {@code POST /v2/transactions/secure-fields}
 * and settlement via {@code POST /v2/transactions/{transactionId}/settle}
 * with HTTP Basic Authentication.
 *
 * <h2>Idempotent money movement</h2>
 * <p>{@code authorize} and {@code settle} are retried by Temporal, and a retry after an
 * ambiguous failure (the gateway processed the request but the response never arrived) reaches
 * a gateway that has already done the work. Datatrans signals that with a conflict response
 * — the same {@code 409} it uses for a genuine 3-D Secure failure on authorize — so the status
 * code alone cannot be trusted. Both calls therefore resolve a conflict by reading the
 * authoritative transaction status from {@code GET /v2/transactions/{transactionId}} and only
 * report success when the gateway confirms the work is done for this exact transaction, refno,
 * and amount. Every other outcome keeps its original failure semantics.
 *
 * <h2>Rejected merchant credentials</h2>
 * <p>Every call authenticates with the merchant id and the configured merchant password. A
 * {@code 401} or {@code 403} therefore says our credentials or our merchant permissions are
 * wrong — it is never a statement about the customer's card, and no money moved. All calls map
 * it uniformly to {@link DatatransAuthenticationException} and log it at ERROR, so an expired
 * merchant password reads as the operational fault it is instead of a wave of card declines.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatatransRestAdapter implements DatatransOutPort {

  private static final String REFNO = "refno";

  /** Datatrans transaction status meaning the money is held on the card. */
  private static final String STATUS_AUTHORIZED = "authorized";

  /** Datatrans transaction status meaning the held money has been captured. */
  private static final String STATUS_SETTLED = "settled";

  /**
   * Extracts the {@code code} field from a Datatrans error body such as
   * {@code {"error":{"code":"INVALID_TRANSACTION_STATUS","message":"..."}}}. The code is a
   * diagnostic hint only — the transaction status is what decides the outcome — so a body in
   * an unexpected shape simply yields no code rather than failing the call.
   */
  private static final Pattern ERROR_CODE_PATTERN =
      Pattern.compile("\"code\"\\s*:\\s*\"([^\"]+)\"");

  private static final String UNKNOWN_ERROR_CODE = "(none)";

  /**
   * Datatrans rejects our HTTP Basic credentials with {@code 401} and refuses an operation the
   * merchant is not permitted to perform with {@code 403}. Both are faults in our own merchant
   * configuration, never a statement about the customer's card.
   */
  private static final Predicate<HttpStatusCode> CREDENTIALS_REJECTED =
      status -> status.value() == 401 || status.value() == 403;

  @Qualifier("datatransRestClient")
  private final RestClient datatransRestClient;
  private final DatatransProperties properties;

  /**
   * Authorizes a previously initialized transaction.
   *
   * <p>A {@code 409} is ambiguous: Datatrans returns it both when 3-D Secure authentication
   * failed and when this transaction has already been authorized (which is what a retry of a
   * request that actually succeeded looks like). The conflict is therefore resolved against
   * the transaction status rather than assumed to be a 3-D Secure failure — see
   * {@link #recoverFromAuthorizeConflict}.
   */
  @Override
  public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
      long amount, String merchantId) {
    log.info("authorizeTransaction called for transactionId={}", transactionId);
    try {
      DatatransAuthorizeResponse response = datatransRestClient.post()
          .uri("/v2/transactions/{transactionId}/authorize", transactionId)
          .headers(h -> h.setBasicAuth(merchantId, properties.getMerchantPassword()))
          .body(Map.of(REFNO, refno, "amount", amount))
          .retrieve()
          .onStatus(CREDENTIALS_REJECTED, rejectedCredentials("authorize", merchantId))
          .onStatus(status -> status.value() == 404, (request, response1) -> {
            throw new TransactionNotFoundException(
                "Transaction not found or expired");
          })
          .onStatus(status -> status.value() == 409, (request, response1) -> {
            throw new ConflictSignal(409, errorCodeOf(response1));
          })
          .onStatus(HttpStatusCode::isError, (request, response1) -> {
            log.error("Datatrans authorize error: status={}, body={}",
                response1.getStatusCode(), ErrorBodyReader.read(response1));
            throw new DatatransGatewayException(
                "Datatrans returned " + response1.getStatusCode());
          })
          .body(DatatransAuthorizeResponse.class);
      log.info("Transaction authorized successfully: transactionId={}",
          transactionId);
      return response;
    } catch (ConflictSignal conflict) {
      return recoverFromAuthorizeConflict(transactionId, refno, amount, merchantId, conflict);
    } catch (ResourceAccessException e) {
      log.error("Datatrans is unreachable during authorize: {}", e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Datatrans is unreachable: " + e.getMessage());
    }
  }

  /**
   * Decides what a {@code 409} from {@code /authorize} actually meant, by asking Datatrans for
   * the transaction's current status.
   *
   * <p>The status endpoint is the authority:
   * <ul>
   *   <li><b>authorized or settled</b> — the money is already held for this transaction, so the
   *       conflict was our own earlier attempt succeeding. The call returns the result a fresh
   *       success would have returned, rebuilt from the status response, but only after the
   *       identifiers and the authorized amount are confirmed to match.</li>
   *   <li><b>anything else</b> — nothing was authorized, so the {@code 409} was a genuine 3-D
   *       Secure failure and is reported as {@link ThreeDsAuthenticationFailedException}.</li>
   *   <li><b>status unreadable</b> — the status call's own failure propagates
   *       ({@link ServiceUnavailableException} or {@link DatatransGatewayException}), both of
   *       which are retryable. Ambiguity must never be converted into a terminal payment
   *       failure.</li>
   * </ul>
   */
  private DatatransAuthorizeResponse recoverFromAuthorizeConflict(String transactionId,
      String refno, long amount, String merchantId, ConflictSignal conflict) {
    log.warn("Datatrans authorize returned 409 [transactionId={}, errorCode={}] — confirming"
        + " transaction status before deciding", transactionId, conflict.errorCode());

    DatatransTransactionStatus status = getTransactionStatus(transactionId, merchantId);
    String confirmedStatus = normalise(status.status());

    if (!STATUS_AUTHORIZED.equals(confirmedStatus) && !STATUS_SETTLED.equals(confirmedStatus)) {
      throw new ThreeDsAuthenticationFailedException(
          "3-D Secure authentication failed [errorCode=" + conflict.errorCode()
              + ", confirmedStatus=" + confirmedStatus + "]");
    }

    assertIdentifiersMatch(transactionId, refno, status);
    assertAuthorizedAmountMatches(transactionId, amount, status);

    log.info("Datatrans authorize 409 resolved as an idempotent replay of a successful"
        + " authorization [transactionId={}, confirmedStatus={}]", transactionId, confirmedStatus);
    return new DatatransAuthorizeResponse(status.transactionId(), STATUS_AUTHORIZED,
        status.acquirerAuthorizationCode(), status.card(), status.paymentMethod());
  }

  @Override
  public DatatransTransactionStatus getTransactionStatus(String transactionId,
      String merchantId) {
    try {
      DatatransTransactionStatus response = datatransRestClient.get()
          .uri("/v2/transactions/{transactionId}", transactionId)
          .headers(h -> h.setBasicAuth(merchantId, properties.getMerchantPassword()))
          .retrieve()
          .onStatus(CREDENTIALS_REJECTED, rejectedCredentials("status", merchantId))
          .onStatus(status -> status.value() == 404, (request, clientResponse) -> {
            throw new TransactionNotFoundException(
                "Transaction not found or expired");
          })
          .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
            throw new DatatransGatewayException(
                "Datatrans status request returned HTTP "
                    + clientResponse.getStatusCode().value());
          })
          .body(DatatransTransactionStatus.class);

      if (response == null || response.status() == null
          || response.status().isBlank()) {
        throw new DatatransGatewayException(
            "Datatrans status response was absent or contained a blank status");
      }

      return response;
    } catch (ResourceAccessException e) {
      throw new ServiceUnavailableException(
          "Datatrans is unreachable while retrieving transaction status", e);
    } catch (RestClientException e) {
      throw new ServiceUnavailableException(
          "Datatrans status response could not be read", e);
    }
  }

  @Override
  public String initSecureFields(DatatransSecureFieldsRequest request) {
    log.info("Initializing Secure Fields transaction: currency={}, amount={}",
        request.currency(), request.amount());
    try {
      DatatransSecureFieldsResponse response = datatransRestClient.post()
          .uri("/v2/transactions/secure-fields")
          .headers(h -> h.setBasicAuth(
              request.merchantId(), properties.getMerchantPassword()))
          .body(Map.of(
              "amount", request.amount(),
              "currency", request.currency(),
              "returnUrl", request.returnUrl(),
              "returnMethod", request.returnMethod()
          ))
          .retrieve()
          .onStatus(CREDENTIALS_REJECTED,
              rejectedCredentials("Secure Fields init", request.merchantId()))
          .onStatus(HttpStatusCode::isError, (req, clientResponse) -> {
            log.error("Datatrans Secure Fields init error: status={}, body={},"
                    + " merchantId={}, returnUrl={}",
                clientResponse.getStatusCode(),
                ErrorBodyReader.read(clientResponse),
                request.merchantId(), request.returnUrl());
            throw new DatatransGatewayException(
                "Datatrans returned " + clientResponse.getStatusCode());
          })
          .body(DatatransSecureFieldsResponse.class);

      if (response == null || response.transactionId() == null
          || response.transactionId().isBlank()) {
        throw new DatatransGatewayException(
            "Datatrans returned empty response");
      }

      log.info("Secure Fields transaction initialized: transactionId={}",
          response.transactionId());
      return response.transactionId();
    } catch (ResourceAccessException e) {
      log.error("Datatrans is unreachable: {}", e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Datatrans is unreachable: " + e.getMessage());
    }
  }

  /**
   * Settles a previously authorized transaction.
   *
   * <p>Settling a transaction that is already settled is what a retry of a request that
   * actually succeeded looks like, and Datatrans rejects it as an invalid transaction status.
   *
   * <p><b>Assumption to confirm against the Datatrans sandbox:</b> the exact status code for
   * that rejection is not pinned down by anything in this codebase, so both {@code 400} and
   * {@code 409} are treated as potentially meaning "already settled" and are resolved against
   * the transaction status. A rejection for any other reason still fails, because the status
   * check only reports success when Datatrans confirms the transaction really is settled.
   */
  @Override
  public void settleTransaction(String transactionId, long amount,
      String currency, String refno, String merchantId) {
    log.info("settleTransaction called for transactionId={}", transactionId);
    try {
      datatransRestClient.post()
          .uri("/v2/transactions/{transactionId}/settle", transactionId)
          .headers(h -> h.setBasicAuth(merchantId, properties.getMerchantPassword()))
          .body(Map.of(
              REFNO, refno,
              "amount", amount,
              "currency", currency))
          .retrieve()
          .onStatus(CREDENTIALS_REJECTED, rejectedCredentials("settle", merchantId))
          .onStatus(status -> status.value() == 400 || status.value() == 409,
              (request, response) -> {
                throw new ConflictSignal(response.getStatusCode().value(), errorCodeOf(response));
              })
          .onStatus(HttpStatusCode::isError, (request, response) -> {
            throw new DatatransGatewayException(
                "Settlement failed: " + response.getStatusCode());
          })
          .toBodilessEntity();
      log.info("Transaction settled successfully: transactionId={}", transactionId);
    } catch (ConflictSignal conflict) {
      recoverFromSettleConflict(transactionId, refno, amount, merchantId, conflict);
    } catch (ResourceAccessException e) {
      log.error("Datatrans is unreachable during settle: {}", e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Datatrans is unreachable: " + e.getMessage());
    }
  }

  /**
   * Decides whether a rejected settlement had in fact already been settled.
   *
   * <p>Returns normally — the settlement is complete — only when Datatrans confirms the
   * transaction is {@code settled} and its identifiers and amount match what we asked to
   * settle. Any other confirmed status keeps the original {@link DatatransGatewayException},
   * and a failure of the status call itself propagates unchanged so the ambiguity stays
   * retryable.
   */
  private void recoverFromSettleConflict(String transactionId, String refno, long amount,
      String merchantId, ConflictSignal conflict) {
    log.warn("Datatrans settle returned {} [transactionId={}, errorCode={}] — confirming"
            + " transaction status before deciding",
        conflict.statusCode(), transactionId, conflict.errorCode());

    DatatransTransactionStatus status = getTransactionStatus(transactionId, merchantId);
    String confirmedStatus = normalise(status.status());

    if (!STATUS_SETTLED.equals(confirmedStatus)) {
      throw new DatatransGatewayException("Settlement failed: " + conflict.statusCode()
          + " [errorCode=" + conflict.errorCode()
          + ", confirmedStatus=" + confirmedStatus + "]");
    }

    assertIdentifiersMatch(transactionId, refno, status);
    assertAuthorizedAmountMatches(transactionId, amount, status);

    log.info("Datatrans settle conflict resolved as an idempotent replay of a successful"
        + " settlement [transactionId={}]", transactionId);
  }

  @Override
  public String initMobileSdk(DatatransMobileSdkRequest request) {
    log.info("Initializing Mobile SDK transaction: currency={}, amount={}, refno={}",
        request.currency(), request.amount(), request.refno());
    try {
      DatatransMobileSdkResponse response = datatransRestClient.post()
          .uri("/v2/transactions")
          .headers(h -> h.setBasicAuth(
              request.merchantId(), properties.getMerchantPassword()))
          .body(buildMobileSdkBody(request))
          .retrieve()
          .onStatus(CREDENTIALS_REJECTED,
              rejectedCredentials("Mobile SDK init", request.merchantId()))
          .onStatus(HttpStatusCode::isError, (req, clientResponse) -> {
            log.error("Datatrans Mobile SDK init error: status={}, body={}",
                clientResponse.getStatusCode(),
                ErrorBodyReader.read(clientResponse));
            throw new DatatransGatewayException(
                "Datatrans v2 returned " + clientResponse.getStatusCode());
          })
          .body(DatatransMobileSdkResponse.class);

      if (response == null || response.transactionId() == null
          || response.transactionId().isBlank()) {
        throw new DatatransGatewayException(
            "Datatrans v2 returned empty or invalid transactionId");
      }

      log.info("Mobile SDK transaction initialized: transactionId={}",
          response.transactionId());
      return response.transactionId();
    } catch (ResourceAccessException e) {
      log.error("Datatrans is unreachable: {}", e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Datatrans is unreachable: " + e.getMessage());
    }
  }

  /**
   * Builds the Datatrans v2 init request body for Mobile SDK.
   */
  private Map<String, Object> buildMobileSdkBody(DatatransMobileSdkRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("amount", request.amount());
    body.put("currency", request.currency());
    body.put(REFNO, request.refno());
    body.put("paymentMethods", request.paymentMethods());
    body.put("option", Map.of("createAlias", true));
    if (request.webhookUrl() != null && !request.webhookUrl().isBlank()) {
      body.put("webhook", Map.of("url", request.webhookUrl()));
    }
    return body;
  }

  @Override
  public void cancelTransaction(String transactionId, String merchantId) {
    log.info("cancelTransaction called for transactionId={}, merchantId={}",
        transactionId, merchantId);
    try {
      datatransRestClient.post()
          .uri("/v2/transactions/{transactionId}/cancel", transactionId)
          .headers(h -> h.setBasicAuth(merchantId, properties.getMerchantPassword()))
          .retrieve()
          .onStatus(CREDENTIALS_REJECTED, rejectedCredentials("cancel", merchantId))
          .onStatus(status -> status.value() == 404, (request, response) -> {
            throw new TransactionNotFoundException(
                "Transaction not found or already settled");
          })
          .onStatus(HttpStatusCode::isError, (request, response) -> {
            throw new DatatransGatewayException(
                "Cancellation failed: " + response.getStatusCode());
          })
          .toBodilessEntity();
      log.info("Transaction cancelled successfully: transactionId={}", transactionId);
    } catch (ResourceAccessException e) {
      log.error("Datatrans is unreachable during cancel: {}", e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Datatrans is unreachable: " + e.getMessage());
    }
  }

  // ==========================================================================
  // Conflict recovery helpers
  // ==========================================================================

  /**
   * Confirms the transaction the gateway reported on is the one we asked about.
   *
   * <p>The transaction id pins the payment and the refno pins the booking it belongs to. A
   * mismatch means we are about to report success for somebody else's money, so it fails
   * terminally instead.
   */
  private void assertIdentifiersMatch(String transactionId, String refno,
      DatatransTransactionStatus status) {
    if (status.transactionId() != null && !status.transactionId().equals(transactionId)) {
      throw new TransactionMismatchException(
          "Datatrans status is for a different transaction [expected=" + transactionId
              + ", received=" + status.transactionId() + "]");
    }
    if (status.refno() != null && refno != null && !status.refno().equals(refno)) {
      throw new TransactionMismatchException(
          "Datatrans status is for a different refno [transactionId=" + transactionId
              + ", expected=" + refno + ", received=" + status.refno() + "]");
    }
  }

  /**
   * Confirms the gateway is holding the amount we expected.
   *
   * <p>An absent amount counts as a mismatch: an unconfirmable amount is exactly the case
   * where silently reporting success would hide a wrong charge.
   */
  private void assertAuthorizedAmountMatches(String transactionId, long expectedAmount,
      DatatransTransactionStatus status) {
    Integer authorizedAmount = status.authorizedAmount();
    if (authorizedAmount == null || authorizedAmount.longValue() != expectedAmount) {
      throw new TransactionMismatchException(
          "Datatrans authorized amount does not match the expected amount [transactionId="
              + transactionId + ", expected=" + expectedAmount
              + ", authorized=" + authorizedAmount + "]");
    }
  }

  /**
   * Fails a Datatrans call whose credentials were rejected, and says so loudly.
   *
   * <p>Logged at ERROR because nobody but us can fix it: every customer on every hotel is
   * failing until the merchant configuration is corrected, and nothing in the customer-facing
   * error would ever reveal that. The merchant id identifies which configuration is at fault
   * and the response body carries Datatrans' own reason; the merchant password is never
   * logged.
   *
   * @param operation the Datatrans operation being attempted, for the log line
   * @param merchantId the merchant id whose credentials were rejected
   * @return an error handler that always throws {@link DatatransAuthenticationException}
   */
  private static RestClient.ResponseSpec.ErrorHandler rejectedCredentials(String operation,
      String merchantId) {
    return (request, response) -> {
      log.error("Datatrans rejected merchant credentials during {}: status={}, merchantId={},"
              + " body={}",
          operation, response.getStatusCode(), merchantId, ErrorBodyReader.read(response));
      throw new DatatransAuthenticationException(
          "Datatrans rejected merchant credentials during " + operation + ": "
              + response.getStatusCode() + " [merchantId=" + merchantId + "]");
    };
  }

  /**
   * Reads the Datatrans error code from a conflict response body, for diagnostics.
   *
   * @return the reported error code, or {@code "(none)"} when the body carries none
   */
  private static String errorCodeOf(ClientHttpResponse response) {
    Matcher matcher = ERROR_CODE_PATTERN.matcher(ErrorBodyReader.read(response));
    return matcher.find() ? matcher.group(1) : UNKNOWN_ERROR_CODE;
  }

  private static String normalise(String status) {
    return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
  }

  /**
   * Internal marker carrying a conflict response out of a {@code RestClient} status handler so
   * the surrounding method can resolve it against the transaction status. It never escapes
   * this adapter.
   */
  private static final class ConflictSignal extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    private ConflictSignal(int statusCode, String errorCode) {
      super("Datatrans returned " + statusCode, null, false, false);
      this.statusCode = statusCode;
      this.errorCode = errorCode;
    }

    private int statusCode() {
      return statusCode;
    }

    private String errorCode() {
      return errorCode;
    }
  }
}
