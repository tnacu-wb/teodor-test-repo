package uk.co.whitbread.basket.processor.infrastructure.rest.client;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmAmendRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.basket.processor.domain.ports.secondary.BasketOrderProcessOutPort;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.mapper.ReservationRequestMapper;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.mapper.ReservationResponseMapper;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.ReservationsClient;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationByBasketRefResponseDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasketOrderProcessOutPortImpl implements BasketOrderProcessOutPort {

  private final ReservationsClient reservationsClient;
  private final ReservationRequestMapper reservationRequestMapper;
  private final ReservationResponseMapper reservationResponseMapper;

  @Override
  public ConfirmReservationResponse confirmReservation(final ConfirmReservationRequest confirmReservationRequest) {
    log.info("Entering confirm reservation for hotelCode={} and reservationId={}",
        confirmReservationRequest.getHotelId(), confirmReservationRequest.getReservationId());
    final var response = reservationsClient
        .confirmReservation(reservationRequestMapper.toDto(confirmReservationRequest));
    return reservationResponseMapper.toModel(response);
  }

  @Override
  public CancelReservationResponse cancelReservation(final CancelReservationRequest cancelReservationRequest) {
    log.info("Entering cancel reservation for hotelCode={} and reservationId={}",
        cancelReservationRequest.getHotelId(), cancelReservationRequest.getReservationIds());
    final var response = reservationsClient
        .cancelReservation(reservationRequestMapper.toDto(cancelReservationRequest));
    return reservationResponseMapper.toModel(response);
  }

  @Override
  public Boolean confirmAmend(ConfirmAmendRequest confirmReservationRequest) {
    log.info("Entering amend reservation for originalBasketRef={} and tmpBasketRef={}",
        confirmReservationRequest.getOriginalBookingRef(),
        confirmReservationRequest.getTempBookingRef());
    final Optional<ReservationByBasketRefResponseDto> response;
    response = Optional.ofNullable(reservationsClient
        .confirmAmend(reservationRequestMapper.toDto(confirmReservationRequest)));
    return response.isPresent();
  }

}
