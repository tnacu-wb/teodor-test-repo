package uk.co.whitbread.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.PackagesResponseDto;

@Mapper(componentModel = "spring")
public interface PackagesResponseDtoMapper {

  PackagesResponseDto toDto(PackagesResponse packagesResponse);
}
