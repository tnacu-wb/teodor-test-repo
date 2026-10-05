package uk.co.whitbread.basket.processor.infrastructure.rest.client.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.hotel.reservation.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ConfirmReservationResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationResponseMapper {

  ConfirmReservationResponse toModel(ConfirmReservationResponseDto reservationResponse);

  CancelReservationResponse toModel(CancelReservationResponseDto cancelReservationResponseDto);
}
