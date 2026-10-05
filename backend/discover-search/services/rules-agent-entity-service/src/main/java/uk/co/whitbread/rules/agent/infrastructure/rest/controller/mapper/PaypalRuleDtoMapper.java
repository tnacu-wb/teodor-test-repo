package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.PaypalRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.PaypalRuleResponseDto;

@Mapper(componentModel = "spring")
public interface PaypalRuleDtoMapper {

  PaypalRuleResponseDto toDto(PaypalRuleResponse paypalRuleResponse);
}
