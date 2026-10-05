package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomsRuleEntity;

@Mapper(componentModel = "spring")
public interface MaxRoomsRuleEntityMapper {

  MaxRoomsRule toModel(MaxRoomsRuleEntity maxRoomsRuleEntity);
}
