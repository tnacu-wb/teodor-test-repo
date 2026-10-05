package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomUpgradeOptions;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomUpgradeOptionsDto;

@Mapper(componentModel = "spring", uses = {RoomUpgradesMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomUpgradeOptionsMapper {

  RoomUpgradeOptions toDomainModel(RoomUpgradeOptionsDto roomUpgradeOptionsDto);
}
