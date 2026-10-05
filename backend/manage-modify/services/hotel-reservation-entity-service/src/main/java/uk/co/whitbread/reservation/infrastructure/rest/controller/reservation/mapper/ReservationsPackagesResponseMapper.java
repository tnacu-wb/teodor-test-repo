package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationsPackagesResponseDto;

@Mapper(componentModel = "spring", uses = {RoomStayMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationsPackagesResponseMapper {

  ReservationsPackagesResponseDto toDto(ReservationsPackagesResponse reservationResponse);

}
