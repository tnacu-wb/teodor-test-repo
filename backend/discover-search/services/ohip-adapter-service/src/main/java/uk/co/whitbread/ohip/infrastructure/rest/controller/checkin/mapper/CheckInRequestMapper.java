package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper;

import java.util.Arrays;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.Reservation;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CheckInDetailsDto;

@Mapper(componentModel = "spring", imports = {Arrays.class})
public interface CheckInRequestMapper {

  @Mapping(target = "fetchReservationInstruction", expression = "java(Arrays.asList(\"ReservationDetail\"))")
  @Mapping(target = "reservation", source = "checkInDetailsDto", qualifiedByName = "reservationMapping")
  CheckInRequest toCheckInInputRequestModel(CheckInDetailsDto checkInDetailsDto);

  @Named("reservationMapping")
  default Reservation toReservationMappingModel(CheckInDetailsDto checkInDetailsDto) {
    return Reservation.builder().roomId(checkInDetailsDto.getRoomId()).ignoreWarnings(true)
        .overrideAdvancePaymentValidation(true)
        .build();
  }

}
