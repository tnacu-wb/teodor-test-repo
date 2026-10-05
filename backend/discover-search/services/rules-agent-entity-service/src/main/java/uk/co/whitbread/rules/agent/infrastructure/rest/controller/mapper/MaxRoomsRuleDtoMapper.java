package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRuleResponseDto;

@Mapper(componentModel = "spring")
public interface MaxRoomsRuleDtoMapper {

  MaxRoomsRuleRequest toModel(MaxRoomsRuleRequestDto maxRoomsRuleRequestDto);

  MaxRoomsRuleResponseDto toDto(MaxRoomsRuleResponse maxRoomsRuleResponse);
}
