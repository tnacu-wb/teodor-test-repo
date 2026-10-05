package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

@Mapper(componentModel = "spring",
    uses = {MaxRoomOccupancyRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MaxRoomOccupancyRuleMapper {

  MaxRoomOccupancyRule toDomainModel(MaxRoomOccupancyRuleEntity roomOccupancyRuleEntity);

  MaxRoomOccupancyRuleEntity toEntityDto(MaxRoomOccupancyRule maxRoomOccupancyRule);

}
