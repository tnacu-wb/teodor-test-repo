package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.amend.out.ConfirmAmendLogicResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.ConfirmAmendLogicResponseDto;

@Mapper(componentModel = "spring")
public interface ConfirmAmendLogicResponseMapper {

  ConfirmAmendLogicResponseDto toDto(ConfirmAmendLogicResponse confirmAmendLogicResponse);
}
