package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.CheckInRequestDto;

@Mapper(componentModel = "spring")
public interface CheckInRequestMapper {

  CheckInRequest toCheckInRequestModel(CheckInRequestDto checkInRequestDto);

}
