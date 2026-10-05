package uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.rules.agent.generated.models.MaxRoomsRuleResponseDto;

@Mapper(componentModel = "spring")
public interface MaxRoomsRuleResponseMapper {

  MaxRoomsRuleResponse toModel(MaxRoomsRuleResponseDto maxRoomsRuleResponseDto);
}
