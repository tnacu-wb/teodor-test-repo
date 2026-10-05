package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationDto;

@Mapper(componentModel = "spring", uses = ReservationPackageMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationMapper {

  @Mapping(source = "basketReferenceId", target = "externalReferenceId")
  Reservation toModel(ReservationDto reservationDto);
}
