package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.FindBookingKioskRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.FindBookingRequestDto;

@Mapper(componentModel = "spring")
public interface FindBookingRequestMapper {

  FindBookingRequest toModel(FindBookingRequestDto findBookingRequestDto);

  FindBookingRequest toModel(FindBookingKioskRequestDto findBookingKioskRequestDto);

}
