package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationOverrideReasonsRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationOverrideReasonsRequestMapper {

  UpdateReservationOverrideReasonsRequest toModel(
      UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto);

}
