package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.UpdateReservationOverrideReasonsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReservationOverrideReasonsResponseDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationOverrideReasonsResponseMapper {

  UpdateReservationOverrideReasonsResponseDto toDto(
      UpdateReservationOverrideReasonsResponse updateReservationOverrideReasonsResponse);

}
