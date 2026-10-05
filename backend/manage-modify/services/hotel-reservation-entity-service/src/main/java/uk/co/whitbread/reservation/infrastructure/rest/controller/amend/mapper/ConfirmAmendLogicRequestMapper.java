package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ConfirmAmendLogicRequestDto;

@Mapper(componentModel = "spring")
public interface ConfirmAmendLogicRequestMapper {

  ConfirmAmendLogicRequest toModel(ConfirmAmendLogicRequestDto confirmAmendLogicRequestDto);
}
