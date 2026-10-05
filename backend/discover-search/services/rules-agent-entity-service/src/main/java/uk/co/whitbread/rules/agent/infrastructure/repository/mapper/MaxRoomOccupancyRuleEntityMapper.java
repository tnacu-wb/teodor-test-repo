package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

@Mapper(componentModel = "spring")
public interface MaxRoomOccupancyRuleEntityMapper {

  MaxRoomOccupancyRule toModel(MaxRoomOccupancyRuleEntity entity);

}
