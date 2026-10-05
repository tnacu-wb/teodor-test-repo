package uk.co.whitbread.basket.processor.infrastructure.rest.client.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmAmendRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationRequest;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmAmendRequestDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationRequestMapper {

  ConfirmReservationRequestDto toDto(ConfirmReservationRequest confirmReservationRequest);

  CancelReservationRequestDto toDto(CancelReservationRequest cancelReservationRequest);

  ConfirmAmendRequestDto toDto(ConfirmAmendRequest confirmAmendRequest);
}
