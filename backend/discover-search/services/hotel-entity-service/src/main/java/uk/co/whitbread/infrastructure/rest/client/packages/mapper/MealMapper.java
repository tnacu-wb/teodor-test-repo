package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MealDto;

@Mapper(componentModel = "spring")
public interface MealMapper {

  @Mapping(target = "name", source = "title")
  @Mapping(target = "allergyInfoSrc", source = "allergyInfoUrl")
  Meal toModel(MealDto meal);
}
