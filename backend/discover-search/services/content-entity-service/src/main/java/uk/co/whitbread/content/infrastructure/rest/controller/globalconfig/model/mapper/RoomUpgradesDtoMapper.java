package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomUpgrades;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomUpgradesDto;

@Mapper(componentModel = "spring")
public interface RoomUpgradesDtoMapper {

  RoomUpgradesDto toDtoModel(RoomUpgrades roomUpgrades);
}
