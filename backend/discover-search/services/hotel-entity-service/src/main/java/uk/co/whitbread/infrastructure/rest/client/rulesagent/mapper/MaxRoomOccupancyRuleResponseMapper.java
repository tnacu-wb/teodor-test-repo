package uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.rules.agent.generated.models.MaxRoomOccupancyResponseDto;

@Mapper(componentModel = "spring")
public interface MaxRoomOccupancyRuleResponseMapper {

  MaxRoomOccupancyResponse toModel(MaxRoomOccupancyResponseDto maxRoomOccupancyResponseDto);
}
