package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.UpdateReasonForStayResponseDto;

@Mapper(componentModel = "spring")
public interface UpdateReasonForStayResponseMapper {

  UpdateReasonForStayResponseDto toDto(UpdateReasonForStayResponse updateReasonForStayResponse);

}
