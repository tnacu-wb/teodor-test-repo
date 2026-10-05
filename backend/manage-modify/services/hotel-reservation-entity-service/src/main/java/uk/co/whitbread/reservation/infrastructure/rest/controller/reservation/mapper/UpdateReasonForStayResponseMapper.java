package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.UpdateReasonForStayResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReasonForStayResponseDto;

@Mapper(componentModel = "spring")
public interface UpdateReasonForStayResponseMapper {

  UpdateReasonForStayResponseDto toDto(UpdateReasonForStayResponse updateReasonForStayResponse);

}
