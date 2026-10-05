package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxArrivalDateRuleEntity;

@Mapper(componentModel = "spring")
public interface MaxArrivalDateRuleEntityMapper {

  MaxArrivalDateRule toModel(MaxArrivalDateRuleEntity maxArrivalDateRuleEntity);
}
