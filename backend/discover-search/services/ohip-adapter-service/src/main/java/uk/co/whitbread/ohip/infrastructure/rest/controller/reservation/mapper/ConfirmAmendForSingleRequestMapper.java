package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendForSingleRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmAmendForSingleRequestDto;

@Mapper(componentModel = "spring")
public interface ConfirmAmendForSingleRequestMapper {
  ConfirmAmendForSingleRequest toRequestModel(
          ConfirmAmendForSingleRequestDto confirmAmendForSingleRequestDto
  );
}