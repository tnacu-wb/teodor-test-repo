package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.BookingInformationRequestAemDto;

@Mapper(componentModel = "spring")
public interface BookingInformationRequestMapper {

  BookingInformationRequestAemDto toDtoModel(BookingInformationRequest bookingInformationRequest);
}
