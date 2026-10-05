package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Restaurant;

@Mapper(componentModel = "spring", uses = {
    MenuMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RestaurantMapper {

  @Mapping(source = "image", target = "logoSrc")
  uk.co.whitbread.content.domain.model.hotel.out.Restaurant toDomainModel(Restaurant restaurant);

}
