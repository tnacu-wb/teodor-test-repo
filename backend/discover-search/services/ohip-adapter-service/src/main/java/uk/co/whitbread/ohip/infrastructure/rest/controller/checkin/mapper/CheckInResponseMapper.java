package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out.CheckInResponseDto;

@Mapper(componentModel = "spring")
public interface CheckInResponseMapper {

  CheckInResponseDto toDto(CheckInResponse checkInResponse);

}
