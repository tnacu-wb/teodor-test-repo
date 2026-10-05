package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackagesRequestDto;

@Mapper(componentModel = "spring")
public interface PackagesRequestMapper {

  PackagesRequest toModel(PackagesRequestDto packagesRequestDto);
}
