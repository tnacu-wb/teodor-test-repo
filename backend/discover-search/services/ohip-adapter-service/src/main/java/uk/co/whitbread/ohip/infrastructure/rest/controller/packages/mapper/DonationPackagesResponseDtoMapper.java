package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.DonationPackagesResponseDto;

@Mapper(componentModel = "spring")
public interface DonationPackagesResponseDtoMapper {

  DonationPackagesResponseDto toDto(DonationPackagesResponse packagesResponse);
}