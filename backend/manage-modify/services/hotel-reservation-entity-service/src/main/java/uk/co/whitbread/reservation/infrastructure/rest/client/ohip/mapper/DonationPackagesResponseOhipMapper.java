package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.reservation.domain.model.out.DonationPackagesResponse;

@Mapper(componentModel = "spring")
public interface DonationPackagesResponseOhipMapper {

  DonationPackagesResponseDto toDto(DonationPackagesResponse donationPackagesResponse);

  DonationPackagesResponse toModel(DonationPackagesResponseDto donationPackagesResponseDto);

}
