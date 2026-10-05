package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;

@Mapper(componentModel = "spring")
public interface PackagesRequestOhipMapper {

  PackagesRequestOhipDto toOhipDto(PackagesRequest packagesRequest);
}
