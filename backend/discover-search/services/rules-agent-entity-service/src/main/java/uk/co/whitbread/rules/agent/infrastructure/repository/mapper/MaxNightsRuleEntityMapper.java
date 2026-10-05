package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxNightsRuleEntity;

@Mapper(componentModel = "spring")
public interface MaxNightsRuleEntityMapper {

  MaxNightsRule toModel(MaxNightsRuleEntity maxNightsRuleEntity);
}
