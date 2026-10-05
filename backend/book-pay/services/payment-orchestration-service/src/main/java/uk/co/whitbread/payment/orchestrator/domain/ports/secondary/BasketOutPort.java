package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;

/**
 * Secondary port for interacting with the Basket Service.
 *
 * <p>Defines the outbound contract for retrieving reservation information
 * and changing basket status during payment orchestration.
 */
public interface BasketOutPort {

  /**
   * Retrieves reservation data from the Hotel Reservation Entity Service.
   *
   * @param basketId the basket/reservation identifier
   * @return Reservation domain model with bookingReference, totalCostOfStay, currencyCode
   * @throws BasketNotFoundException if no reservation exists for the given basket ID
   */
  Reservation getReservation(String basketId);

  /**
   * Reads the current basket status from the Basket Service.
   *
   * <p>Calls {@code GET /v1/baskets/{basket-reference}} and returns the {@code status} field.
   * Used by the payment workflow to reconcile a booking outcome when the {@code bookingCompleted}
   * Kafka event does not arrive.
   *
   * @param basketId the basket identifier (the basket reference)
   * @return the current basket status, or {@link BasketStatus#UNKNOWN} if the Basket Service
   *     reports a status this service does not recognise
   * @throws BasketNotFoundException if the basket does not exist
   */
  BasketStatus getBasketStatus(String basketId);

  /**
   * Changes the basket status via the Basket Service.
   *
   * <p>Calls {@code PUT /v1/baskets/{bookingReference}/changeStatus} with the target status.
   * This must be called before returning the transaction ID to the frontend to ensure
   * the basket is in PAY_PENDING state before payment authorisation begins.
   *
   * @param bookingReference the booking reference (3-letter + 7-digit format)
   * @param status the target basket status (e.g. "PAY_PENDING")
   * @throws BasketNotFoundException if the basket does not exist
   * @throws RuntimeException if the status change fails
   */
  void changeBasketStatus(String bookingReference, String status);
}
