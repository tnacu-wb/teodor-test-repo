package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCustomReferenceNumberRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateCustomReferenceNumberRequestMapper {

  UpdateCustomReferenceNumberRequest toModel(
      UpdateCustomReferenceNumberRequestDto updateCustomReferenceNumberRequestDto);
}
