package uk.co.whitbread.payment.orchestrator.domain.model;

import java.util.Locale;

/**
 * Basket lifecycle status as reported by the Basket Service.
 *
 * <p>Mirrors the {@code BasketStatusDto} enum the Basket Service serialises in the
 * {@code status} field of {@code GET /v1/baskets/{basket-reference}}. The names are copied
 * verbatim from that service so the mapping is a lookup rather than a translation.
 *
 * <p>{@link #UNKNOWN} covers a value this service has not seen before. It is classified as
 * pending on purpose: an unrecognised status is not evidence that a booking failed, and the
 * fail-safe for an authorized payment is to keep waiting rather than to cancel.
 */
public enum BasketStatus {
  OPEN,
  PROCESSING,
  AMENDING,
  COMPLETED,
  AMENDED,
  PRE_CHECKED_IN,
  CANCELLED,
  PAY_PENDING,
  AMEND_FAILED,
  FAILED,
  CIOL_FAILED,
  PRE_CHECKED_OUT,
  CIOL_RC_FAILED,
  SECURE_FAILED,
  UNKNOWN;

  /**
   * Parses a Basket Service status string, returning {@link #UNKNOWN} for anything unrecognised
   * rather than throwing — a new status on the far side must never fail a payment workflow.
   *
   * @param value the raw status string from the Basket Service, may be {@code null}
   * @return the matching status, or {@link #UNKNOWN}
   */
  public static BasketStatus fromValue(String value) {
    if (value == null || value.isBlank()) {
      return UNKNOWN;
    }
    try {
      return BasketStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }

  /**
   * Returns {@code true} if the booking behind this basket is confirmed, so the authorized
   * payment must be settled.
   *
   * <p>{@code AMENDED}, {@code PRE_CHECKED_IN}, and {@code PRE_CHECKED_OUT} are states a basket
   * can only reach after it completed, so they count as completed too.
   */
  public boolean isBookingCompleted() {
    return switch (this) {
      case COMPLETED, AMENDED, PRE_CHECKED_IN, PRE_CHECKED_OUT -> true;
      default -> false;
    };
  }

  /**
   * Returns {@code true} if the booking behind this basket will not happen, so the authorized
   * payment must be cancelled.
   */
  public boolean isBookingFailed() {
    return switch (this) {
      case CANCELLED, FAILED, AMEND_FAILED, CIOL_FAILED, CIOL_RC_FAILED, SECURE_FAILED -> true;
      default -> false;
    };
  }

  /**
   * Returns {@code true} if the booking outcome is not yet decided and the workflow should keep
   * waiting.
   */
  public boolean isPending() {
    return !isBookingCompleted() && !isBookingFailed();
  }
}
