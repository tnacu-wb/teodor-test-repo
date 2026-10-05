package uk.co.whitbread.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.in.DonationPackagesRequestDto;

@Mapper(componentModel = "spring")
public interface DonationPackagesRequestDtoMapper {

  DonationPackagesRequest toModel(DonationPackagesRequestDto donationPackageRequestDto);

}
