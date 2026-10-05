package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmAmendRequestDto;

@Mapper(componentModel = "spring")
public interface ConfirmAmendRequestMapper {

  ConfirmAmendRequest toModel(ConfirmAmendRequestDto requestDto);

}
