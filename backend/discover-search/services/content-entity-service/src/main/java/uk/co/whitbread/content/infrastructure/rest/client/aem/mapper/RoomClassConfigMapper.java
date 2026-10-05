package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomClassConfigDto;

@Mapper(componentModel = "spring")
public interface RoomClassConfigMapper {

  RoomClassConfig toDomainModel(RoomClassConfigDto roomClassConfigDto);

  RoomClassConfig toDomainModel(GlobalConfigDto globalConfigDto);
}
