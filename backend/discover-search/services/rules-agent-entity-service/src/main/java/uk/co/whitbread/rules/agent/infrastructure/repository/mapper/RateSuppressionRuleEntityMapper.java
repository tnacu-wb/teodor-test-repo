package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RateSuppressionRuleEntity;

@Mapper(componentModel = "spring")
public interface RateSuppressionRuleEntityMapper {

  RateSuppressionRule toModel(RateSuppressionRuleEntity rateSuppressionRuleEntity);
}
