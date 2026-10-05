package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.MaxArrivalDateRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxArrivalDateRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRuleResponseDto;

@Mapper(componentModel = "spring")
public interface MaxArrivalDateRuleDtoMapper {

  MaxArrivalDateRuleRequest toModel(MaxArrivalDateRuleRequestDto maxArrivalDateRuleRequestDto);

  MaxArrivalDateRuleResponseDto toDto(MaxArrivalDateRuleResponse maxArrivalDateRuleResponse);
}
