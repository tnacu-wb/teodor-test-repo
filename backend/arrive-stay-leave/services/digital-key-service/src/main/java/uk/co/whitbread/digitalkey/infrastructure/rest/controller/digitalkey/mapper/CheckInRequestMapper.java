package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.CheckInRequestDto;

@Mapper(componentModel = "spring")
public interface CheckInRequestMapper {

  CheckInRequest toCheckInRequestModel(CheckInRequestDto checkInRequestDto);

}