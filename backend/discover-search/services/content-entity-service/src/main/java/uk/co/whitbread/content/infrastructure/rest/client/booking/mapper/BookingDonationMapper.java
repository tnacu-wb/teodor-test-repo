package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.BookingDonation;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.DonationDto;

@Mapper(componentModel = "spring")
public interface BookingDonationMapper {

  @Mapping(target = "name", source = "title")
  @Mapping(target = "imageSrc", source = "imagePath")
  BookingDonation toDomainModel(DonationDto donationDto);
}
