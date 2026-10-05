package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.Restaurant;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.RestaurantDto;

@Mapper(componentModel = "spring")
public interface RestaurantDtoMapper {

  RestaurantDto toDto(Restaurant restaurant);
}
