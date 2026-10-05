package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabGroup;

@Mapper(componentModel = "spring")
public interface RoomConfigurationGroupMapper {

  @Mapping(target = "groupName", source = "groupTitle")
  uk.co.whitbread.content.domain.model.hotel.out.TabGroup toDomainModel(TabGroup tabGroup);
}
