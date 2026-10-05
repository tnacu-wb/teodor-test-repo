package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesGroupResponseDto;

@Mapper(componentModel = "spring")
public interface PackageGroupResponseDtoMapper {

  PackagesGroupResponseDto toDto(PackageGroupsResponse packageGroups);

}
