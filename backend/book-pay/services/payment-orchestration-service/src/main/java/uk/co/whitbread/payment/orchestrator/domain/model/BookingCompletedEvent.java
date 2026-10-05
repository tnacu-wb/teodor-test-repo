package uk.co.whitbread.payment.orchestrator.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Event consumed from the {@code booking-completed} Kafka topic indicating the
 * final outcome of the booking process. Published by the Basket Service after
 * reservation confirmation or failure.
 *
 * <p>The payment workflow uses this event to decide whether to settle (on
 * {@link BookingStatus#COMPLETED}) or cancel (on {@link BookingStatus#FAILED})
 * the previously authorized payment.
 *
 * @param basketReference the basket identifier linking the event to the payment workflow
 * @param status          the booking outcome status ({@code "COMPLETED"} or {@code "FAILED"})
 */
public record BookingCompletedEvent(
    String basketReference,
    String status
) {

  /**
   * Returns the validated {@link BookingStatus} for this event.
   *
   * @return the corresponding {@link BookingStatus}
   * @throws IllegalArgumentException if the status is null or not a recognised value
   */
  @JsonIgnore
  public BookingStatus bookingStatus() {
    if (status == null) {
      throw new IllegalArgumentException("BookingCompletedEvent status must not be null");
    }
    try {
      return BookingStatus.valueOf(status);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(
          "Unknown BookingCompletedEvent status: '%s'. Expected one of: COMPLETED, FAILED"
              .formatted(status));
    }
  }

  /**
   * Returns {@code true} if the booking completed successfully and the
   * payment should be settled.
   */
  @JsonIgnore
  public boolean isCompleted() {
    return BookingStatus.COMPLETED.name().equals(status);
  }

  /**
   * Returns {@code true} if the booking did not complete successfully and the
   * payment should be cancelled. This includes explicit {@code "FAILED"} status
   * as well as any unrecognised or null status values — the fail-safe default
   * is cancellation.
   */
  @JsonIgnore
  public boolean isFailed() {
    return !isCompleted();
  }
}
