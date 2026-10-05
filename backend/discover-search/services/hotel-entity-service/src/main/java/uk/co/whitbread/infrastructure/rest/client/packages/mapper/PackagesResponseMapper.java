package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;

@Mapper(componentModel = "spring", uses = {
    MealMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PackagesResponseMapper {

  @Mapping(target = "restaurant.logoSrc", source = "restaurant.logoUrl")
  PackagesResponse toModel(PackagesResponseDto packagesResponse);

}
