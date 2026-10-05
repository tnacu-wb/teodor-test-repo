package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Status values carried by a {@link BookingCompletedEvent} indicating the
 * outcome of the downstream booking process.
 */
public enum BookingStatus {
  COMPLETED,
  FAILED
}
