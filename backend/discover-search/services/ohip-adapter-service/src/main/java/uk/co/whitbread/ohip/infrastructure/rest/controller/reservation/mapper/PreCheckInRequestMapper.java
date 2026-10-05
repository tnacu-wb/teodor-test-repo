package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PreCheckInRequestDto;

@Mapper(componentModel = "spring")
public interface PreCheckInRequestMapper {

  PreCheckInRequest toModel(PreCheckInRequestDto preCheckInRequestDto);
}
