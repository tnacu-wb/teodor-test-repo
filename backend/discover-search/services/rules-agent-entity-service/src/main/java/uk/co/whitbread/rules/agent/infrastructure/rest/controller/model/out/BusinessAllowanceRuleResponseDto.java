package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BusinessAllowanceRuleResponseDto {

  List<BusinessAllowanceRuleDto> businessAllowances;
}
