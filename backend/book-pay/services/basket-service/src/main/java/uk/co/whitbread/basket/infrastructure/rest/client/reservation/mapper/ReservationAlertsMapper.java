package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.reservation.in.ReservationAlertsRequest;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationAlertsRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationAlertsMapper {

  UpdateReservationAlertsRequestDto toDto(ReservationAlertsRequest reservationAlertsRequest);
}
