package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomUpgrades;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomUpgradesDto;

@Mapper(componentModel = "spring")
public interface RoomUpgradesMapper {

  RoomUpgrades toDomainModel(RoomUpgradesDto roomUpgradesDto);
}
