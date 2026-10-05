package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackageGroupsRequestOhipDto;

@Mapper(componentModel = "spring")
public interface PackageGroupsRequestOhipMapper {

  @Mapping(target = "packageCode", source = "packageCode")
  PackageGroupsRequestOhipDto toOhipDto(String hotelId, String packageCode);
}
