package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationCcAgentIdRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateReservationCcAgentIdRequestMapper {

  UpdateReservationCcAgentIdRequest toModel(
      UpdateReservationCcAgentIdRequestDto updateCcAgentIdRequestDto);

}
