package uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.BookingInformationDto;

@Mapper(componentModel = "spring", uses = {
    DonationDtoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingInformationDtoMapper {

  @Mapping(source = "bookingDonation", target = "donation")
  BookingInformationDto toDto(BookingInformation bookingInformation);

}
