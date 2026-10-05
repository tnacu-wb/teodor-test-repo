package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.KioskCheckInRequest;

@Mapper(componentModel = "spring")
public interface KioskCheckInRequestMapper {

  KioskCheckInRequest toKioskCheckInRequestModel(CheckInRequest checkInRequest);

}
