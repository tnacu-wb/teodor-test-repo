package uk.co.whitbread.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.in.PackagesRequestDto;

@Mapper(componentModel = "spring")
public interface PackagesRequestDtoMapper {

  PackagesRequest toModel(PackagesRequestDto packageRequestDto);

}
