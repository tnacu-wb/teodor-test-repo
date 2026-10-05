package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.RbacRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RbacRuleResponseDto;

@Mapper(componentModel = "spring")
public interface RbacRuleDtoMapper {

  RbacRuleRequest toModel(RbacRuleRequestDto rbacRuleRequestDto);

  RbacRuleResponseDto toDto(RbacRuleResponse rbacRuleResponse);

}
