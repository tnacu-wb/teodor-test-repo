package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.MaxNightsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxNightsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRuleResponseDto;

@Mapper(componentModel = "spring")
public interface MaxNightsRuleDtoMapper {

  MaxNightsRuleRequest toModel(MaxNightsRuleRequestDto maxNightsRuleRequestDto);

  MaxNightsRuleResponseDto toDto(MaxNightsRuleResponse maxNightsRuleResponse);
}
