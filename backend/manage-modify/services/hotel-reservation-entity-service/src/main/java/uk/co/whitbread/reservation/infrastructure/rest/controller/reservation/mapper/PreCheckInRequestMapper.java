package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.PreCheckInRequestDto;

@Mapper(componentModel = "spring")
public interface PreCheckInRequestMapper {

  PreCheckInRequest toModel(PreCheckInRequestDto preCheckInRequestDto);
}
