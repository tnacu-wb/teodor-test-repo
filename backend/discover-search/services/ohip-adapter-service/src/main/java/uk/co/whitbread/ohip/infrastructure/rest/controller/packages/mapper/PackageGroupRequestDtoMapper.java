package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackageGroupsRequestDto;

@Mapper(componentModel = "spring")
public interface PackageGroupRequestDtoMapper {

  PackageGroupRequest toModel(PackageGroupsRequestDto packageGroupsRequestDto);
}
