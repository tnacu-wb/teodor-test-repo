package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.CopyBookingRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CopyBookingRequestDto;

@Mapper(componentModel = "spring")
public interface CopyBookingRequestMapper {

  CopyBookingRequest toModel(CopyBookingRequestDto copyBookingRequestDto);
}
