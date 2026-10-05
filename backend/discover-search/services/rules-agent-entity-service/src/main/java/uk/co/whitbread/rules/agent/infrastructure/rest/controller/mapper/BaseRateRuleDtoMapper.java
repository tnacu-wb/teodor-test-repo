package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.BaseRateRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BaseRateRuleResponseDto;

@Mapper(componentModel = "spring")
public interface BaseRateRuleDtoMapper {
  
  BaseRateRuleResponseDto toDto(BaseRateRuleResponse baseRateRuleResponse);
}
