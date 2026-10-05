package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.TabGroup;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TabGroupDto;

@Mapper(componentModel = "spring")
public interface RoomConfigurationGroupDtoMapper {

  TabGroupDto toDto(TabGroup tabItem);
}
