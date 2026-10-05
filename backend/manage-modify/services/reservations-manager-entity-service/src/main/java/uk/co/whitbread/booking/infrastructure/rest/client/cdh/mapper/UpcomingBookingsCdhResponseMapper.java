package uk.co.whitbread.booking.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsCdhResponse;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBookingsResponse;

@Mapper(componentModel = "spring")
public interface UpcomingBookingsCdhResponseMapper {

  UpcomingBookingsCdhResponse toModel(UpcomingBookingsResponse companySpendingResponse);
}
