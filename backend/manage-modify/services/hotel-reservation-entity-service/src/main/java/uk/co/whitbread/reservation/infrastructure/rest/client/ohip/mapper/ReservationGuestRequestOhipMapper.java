package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestRequestDto;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;


@Mapper(componentModel = "spring")
public interface ReservationGuestRequestOhipMapper {

  ReservationGuestRequestDto toDto(ReservationGuestRequest reservationGuestRequest);

}
