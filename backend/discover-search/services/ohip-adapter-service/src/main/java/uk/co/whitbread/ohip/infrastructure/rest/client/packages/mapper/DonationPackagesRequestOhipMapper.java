package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.DonationPackagesRequestOhipDto;

@Mapper(componentModel = "spring")
public interface DonationPackagesRequestOhipMapper {

  DonationPackagesRequestOhipDto toOhipDto(DonationPackagesRequest packagesRequest);

}
