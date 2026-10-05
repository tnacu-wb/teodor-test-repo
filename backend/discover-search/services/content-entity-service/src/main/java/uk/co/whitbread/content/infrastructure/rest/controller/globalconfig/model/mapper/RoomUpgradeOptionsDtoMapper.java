package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomUpgradeOptions;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomUpgradeOptionsDto;

@Mapper(componentModel = "spring", uses = RoomUpgradesDtoMapper.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomUpgradeOptionsDtoMapper {

  RoomUpgradeOptionsDto toDtoModel(RoomUpgradeOptions roomUpgradeOptionss);
}
