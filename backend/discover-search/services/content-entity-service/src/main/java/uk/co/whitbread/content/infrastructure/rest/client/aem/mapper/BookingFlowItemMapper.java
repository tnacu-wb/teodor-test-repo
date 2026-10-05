package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.hotel.out.BookingFlowItem;

@Mapper(componentModel = "spring")
public interface BookingFlowItemMapper {

  @Mapping(source = "bookingBusinessId", target = "bookingIdBB")
  BookingFlowItem toBookingFlowItemsModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.BookingFlowItem bookingFlowItem);
}
