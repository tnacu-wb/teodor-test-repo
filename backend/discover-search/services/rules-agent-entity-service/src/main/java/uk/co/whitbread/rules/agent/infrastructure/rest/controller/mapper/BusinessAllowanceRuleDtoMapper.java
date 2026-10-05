package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.BusinessAllowanceRuleResponseDto;

@Mapper(componentModel = "spring")
public interface BusinessAllowanceRuleDtoMapper {

  BusinessAllowanceRuleResponseDto toDto(BusinessAllowanceRuleResponse model);
}
