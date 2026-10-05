package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RateSuppressionRuleResponseDto;

@Mapper(componentModel = "spring")
public interface RateSuppressionRuleDtoMapper {

  RateSuppressionRuleResponseDto toDto(RateSuppressionRuleResponse rateSuppressionRuleResponse);
}
