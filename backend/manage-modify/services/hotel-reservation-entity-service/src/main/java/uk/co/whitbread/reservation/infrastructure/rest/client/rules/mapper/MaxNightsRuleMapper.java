package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxRoomOccupancyResponseDto;

@Mapper(componentModel = "spring")
public interface MaxNightsRuleMapper {

  MaxNightsRuleResponse toModel(MaxNightsRuleResponseDto maxNightsRuleResponseDto);

  MaxRoomOccupancyResponse toModel(MaxRoomOccupancyResponseDto maxRoomOccupancyResponseDto);
}
