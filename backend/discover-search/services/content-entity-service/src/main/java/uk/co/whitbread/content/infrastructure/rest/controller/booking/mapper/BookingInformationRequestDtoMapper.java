package uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.BookingInformationRequestDto;

@Mapper(componentModel = "spring")
public interface BookingInformationRequestDtoMapper {

  BookingInformationRequest toDomainModel(BookingInformationRequestDto hotelInformationRequestDto);
}
