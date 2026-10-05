package uk.co.whitbread.content.infrastructure.rest.client.meals.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.out.MealsRequestAemDto;

@Mapper(componentModel = "spring")
public interface MealsRequestMapper {

  MealsRequest toModel(MealsRequestAemDto mealsRequestAemDto);

  MealsRequestAemDto toDto(MealsRequest mealsRequest);
}
