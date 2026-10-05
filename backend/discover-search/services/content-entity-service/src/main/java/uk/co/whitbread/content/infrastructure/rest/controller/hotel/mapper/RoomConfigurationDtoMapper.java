package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.RoomConfiguration;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.RoomConfigurationDto;

@Mapper(componentModel = "spring", uses = {
    RoomConfigurationGroupDtoMapper.class,
    RoomConfigurationItemDtoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomConfigurationDtoMapper {

  RoomConfigurationDto toDto(RoomConfiguration roomConfiguration);
}
