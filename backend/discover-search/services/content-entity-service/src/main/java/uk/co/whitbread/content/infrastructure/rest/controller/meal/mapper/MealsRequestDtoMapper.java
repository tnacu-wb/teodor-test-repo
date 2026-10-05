package uk.co.whitbread.content.infrastructure.rest.controller.meal.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.meal.model.in.MealsRequestDto;

@Mapper(componentModel = "spring")
public interface MealsRequestDtoMapper {

  MealsRequest toDomainModel(MealsRequestDto mealsRequestAemDto);
}
