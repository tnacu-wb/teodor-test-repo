package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;

@Mapper(componentModel = "spring")
public interface BookingChannelRequestMapper {

  @Mapping(target = "channel", defaultValue = "PI")
  BookingChannel toModel(BookingChannelDto bookingChannelDto);

}
