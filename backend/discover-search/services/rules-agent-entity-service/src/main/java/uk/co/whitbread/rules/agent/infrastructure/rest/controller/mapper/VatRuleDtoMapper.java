package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.VatRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.VatRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.VatRuleResponseDto;

@Mapper(componentModel = "spring")
public interface VatRuleDtoMapper {

  VatRuleRequest toModel(VatRuleRequestDto vatRuleRequestDto);

  VatRuleResponseDto toDto(VatRuleResponse vatRuleResponse);

}
