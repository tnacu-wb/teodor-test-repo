package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when the gateway-confirmed transaction does not match the payment we asked about.
 *
 * <p>Raised while recovering from an "already done" conflict on a money-moving call: the
 * gateway reports the transaction as authorized or settled, but its identifiers
 * ({@code transactionId}, {@code refno}) or its authorized amount differ from the values the
 * workflow expected. Reporting success in that situation would silently accept a hold or a
 * capture for the wrong booking or the wrong amount, so the call fails instead.
 *
 * <p>This is a terminal condition — retrying re-reads the same mismatching transaction — so it
 * belongs on the {@code doNotRetry} list of every money-moving activity stub.
 */
public class TransactionMismatchException extends RuntimeException {

  public TransactionMismatchException(String message) {
    super(message);
  }

  public TransactionMismatchException(String message, Throwable cause) {
    super(message, cause);
  }
}
