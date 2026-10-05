package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationOverrideReasonsRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationOverrideReasonsRequestMapper {

  UpdateReservationOverrideReasonsRequest toModel(
      UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto);

}
