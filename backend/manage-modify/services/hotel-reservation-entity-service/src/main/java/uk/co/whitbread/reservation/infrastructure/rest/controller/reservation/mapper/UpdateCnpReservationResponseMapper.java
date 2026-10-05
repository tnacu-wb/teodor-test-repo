package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.UpdateCnpReservationResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateCnpReservationResponseDto;

@Mapper(componentModel = "spring")
public interface UpdateCnpReservationResponseMapper {

  UpdateCnpReservationResponseDto toDto(UpdateCnpReservationResponse updateCnpReservationResponse);

}
