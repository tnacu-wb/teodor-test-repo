package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.infrastructure.rest.client.packages.model.DonationPackagesRequestOhipDto;

@Mapper(componentModel = "spring")
public interface DonationPackagesRequestMapper {

  DonationPackagesRequestOhipDto toOhipDto(DonationPackagesRequest donationPackagesRequest);
}
