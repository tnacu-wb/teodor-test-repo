package uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.out.BookingDonation;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.DonationDto;

@Mapper(componentModel = "spring")
public interface DonationDtoMapper {

  DonationDto toDto(BookingDonation bookingDonation);

}
