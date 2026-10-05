package uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.rules.agent.generated.models.MaxNightsRuleResponseDto;

@Mapper(componentModel = "spring")
public interface MaxNightsRuleResponseMapper {

  MaxNightsRuleResponse toModel(MaxNightsRuleResponseDto maxNightsRuleResponseDto);
}
