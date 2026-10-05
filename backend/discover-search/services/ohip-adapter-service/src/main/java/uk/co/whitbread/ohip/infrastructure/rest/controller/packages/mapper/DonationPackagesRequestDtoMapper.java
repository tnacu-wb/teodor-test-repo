package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.DonationPackagesRequestDto;

@Mapper(componentModel = "spring")
public interface DonationPackagesRequestDtoMapper {

  DonationPackagesRequest toModel(DonationPackagesRequestDto donationPackagesRequestDto);

}
