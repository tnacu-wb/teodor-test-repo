package uk.co.whitbread.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.DonationPackagesResponseDto;

@Mapper(componentModel = "spring")
public interface DonationPackagesResponseDtoMapper {

  DonationPackagesResponseDto toDto(DonationPackagesResponse donationPackagesResponse);
}
