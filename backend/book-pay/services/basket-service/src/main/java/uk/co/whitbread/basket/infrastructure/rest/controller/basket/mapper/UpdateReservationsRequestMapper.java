package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ReservationPackagesRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationsRequestMapper {
  ReservationGuestRequest toModel(ReservationGuestRequestDto reservationGuestRequestDto);

  ReservationPackagesRequest toModel(ReservationPackagesRequestDto reservationPackagesRequestDto);
}
