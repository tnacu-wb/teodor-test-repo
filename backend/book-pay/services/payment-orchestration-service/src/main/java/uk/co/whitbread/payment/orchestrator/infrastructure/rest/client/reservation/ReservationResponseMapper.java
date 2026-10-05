package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation;

import org.mapstruct.Mapper;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.RateInfoSummaryDto;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.ReservationByBasketResponse;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.ReservationByIdDto;

/**
 * Maps the Hotel Reservation Entity Service response DTOs to the {@link Reservation} domain model.
 */
@Mapper(componentModel = "spring")
public interface ReservationResponseMapper {

  /**
   * Maps a {@link ReservationByBasketResponse} to the {@link Reservation} domain model.
   *
   * <p>Extracts {@code totalCostOfStay} and {@code currencyCode} from the first reservation's
   * {@code rateInfo.summary}. Sets both {@code bookingReference} and {@code refno} to
   * {@code response.bookingReference()}.
   *
   * @param basketId the basket/reservation identifier from the frontend request
   * @param response the raw response from the Hotel Reservation Entity Service
   * @return the mapped Reservation domain model
   * @throws BasketNotFoundException if {@code reservationByIdList} is null or empty
   */
  default Reservation toDomain(String basketId, ReservationByBasketResponse response) {
    if (response.reservationByIdList() == null || response.reservationByIdList().isEmpty()) {
      throw new BasketNotFoundException("No reservations found for basket " + basketId);
    }

    ReservationByIdDto firstReservation = response.reservationByIdList().getFirst();
    if (firstReservation.rateInfo() == null || firstReservation.rateInfo().summary() == null) {
      throw new BasketNotFoundException(
          "Reservation rate information is incomplete for basket " + basketId);
    }

    RateInfoSummaryDto summary = firstReservation.rateInfo().summary();

    return new Reservation(
        basketId,
        response.hotelId(),
        summary.totalCostOfStay(),
        summary.currencyCode(),
        response.bookingReference(),
        response.bookingReference(),
        response.channel()
    );
  }
}
