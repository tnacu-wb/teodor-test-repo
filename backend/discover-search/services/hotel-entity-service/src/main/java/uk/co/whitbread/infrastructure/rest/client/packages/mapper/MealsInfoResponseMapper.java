package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.hotel.content.generated.models.MealsInfoResponseDto;

@Mapper(componentModel = "spring", uses = {
    MealMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MealsInfoResponseMapper {

  MealsInfoResponse toDomainModel(MealsInfoResponseDto mealsInfoResponseDto);
}
