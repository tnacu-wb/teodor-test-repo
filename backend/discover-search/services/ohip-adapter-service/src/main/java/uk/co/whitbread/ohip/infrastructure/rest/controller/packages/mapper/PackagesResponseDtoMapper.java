package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.packages.out.Meal;
import uk.co.whitbread.ohip.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.MealDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesResponseDto;

@Mapper(componentModel = "spring")
public interface PackagesResponseDtoMapper {

  MealDto toDto(Meal meal);

  PackagesResponseDto toDto(PackagesResponse packagesResponse);

}
