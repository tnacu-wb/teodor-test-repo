package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when Datatrans rejects <em>our</em> merchant credentials or permissions.
 *
 * <p>Datatrans answers {@code 401} when the merchant id and password sent as HTTP Basic
 * credentials are wrong, expired, or disabled, and {@code 403} when those credentials are
 * valid but the merchant is not permitted to perform the requested operation. Neither says
 * anything about the customer's card: no card was ever presented to an issuer, and no money
 * moved. It is an operational fault on our side — a rotated password that was not deployed, a
 * merchant id pointed at the wrong Datatrans environment, a permission that was never granted.
 *
 * <p>Because of that it must never be reported to a customer as a decline, and must never
 * surface to our own clients as {@code 401}/{@code 403} (which would claim <em>their</em>
 * authentication failed). It maps to
 * {@link uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode
 * #GATEWAY_AUTHENTICATION_FAILED} and a {@code 502}.
 *
 * <p>This is terminal for the attempt: an activity's three quick retries all carry the same
 * bad credentials, so retrying only delays the customer's error while adding load and noise to
 * the gateway. It therefore belongs on the {@code doNotRetry} list of every activity stub that
 * calls Datatrans. It is kept distinct from {@link DatatransGatewayException} precisely for
 * that reason: the gateway exception is deliberately retryable (the conflict-recovery paths
 * depend on it staying retryable), so the two cannot share a type.
 */
public class DatatransAuthenticationException extends RuntimeException {

  public DatatransAuthenticationException(String message) {
    super(message);
  }

  public DatatransAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
