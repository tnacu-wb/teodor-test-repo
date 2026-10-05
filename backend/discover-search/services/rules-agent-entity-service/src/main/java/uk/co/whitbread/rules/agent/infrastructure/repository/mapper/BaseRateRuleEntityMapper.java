package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BaseRateRuleEntity;

@Mapper(componentModel = "spring")
public interface BaseRateRuleEntityMapper {
  
  BaseRateRule toModel(BaseRateRuleEntity baseRateRuleEntity);
}
