package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomClassConfigDto;

@Mapper(componentModel = "spring", uses = GlobalConfigQueryParamsMapper.class)
public interface RoomClassConfigDtoMapper {

  RoomClassConfigDto toDtoModel(RoomClassConfig roomClassConfig);
}
