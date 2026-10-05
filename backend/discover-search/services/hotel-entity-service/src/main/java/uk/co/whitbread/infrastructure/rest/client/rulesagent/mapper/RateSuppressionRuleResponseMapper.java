package uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.rules.agent.generated.models.RateSuppressionRuleResponseDto;

@Mapper(componentModel = "spring")
public interface RateSuppressionRuleResponseMapper {

  RateSuppressionRuleResponse toModel(RateSuppressionRuleResponseDto rateSuppressionRuleResponseDto);

}
