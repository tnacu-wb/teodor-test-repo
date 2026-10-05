package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationByIdGuestsDto;

@Mapper(componentModel = "spring")
public interface ReservationGuestsMapper {

  @Mapping(target = "givenName", source = "givenName", defaultValue = "")
  @Mapping(target = "surName", source = "surName", defaultValue = "")
  ReservationByIdGuestsDto toDto(ReservationByIdGuestsResponse response);

}
