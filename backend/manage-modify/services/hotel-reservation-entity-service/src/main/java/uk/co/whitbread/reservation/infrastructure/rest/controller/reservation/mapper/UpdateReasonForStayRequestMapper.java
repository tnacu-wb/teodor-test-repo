package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReasonForStayRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateReasonForStayRequestMapper {
  UpdateReasonForStayRequest toModel(UpdateReasonForStayRequestDto updateResonForStayRequestDto);
}
