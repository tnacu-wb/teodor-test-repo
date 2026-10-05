package uk.co.whitbread.content.infrastructure.rest.controller.meal.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.meal.model.out.MealsInfoResponseDto;

@Mapper(componentModel = "spring")
public interface MealsInfoResponseDtoMapper {

  MealsInfoResponseDto toDto(MealsInfoResponse mealsInfoResponse);

}
