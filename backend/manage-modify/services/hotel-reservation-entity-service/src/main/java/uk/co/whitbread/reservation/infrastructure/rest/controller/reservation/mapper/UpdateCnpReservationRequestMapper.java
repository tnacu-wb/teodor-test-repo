package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.UpdateCnpReservationRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateCnpReservationRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateCnpReservationRequestMapper {

  @Mapping(source = "language", target = "language", defaultValue = "en")
  UpdateCnpReservationRequest toModel(UpdateCnpReservationRequestDto updateCnpReservationRequestDto);

}
