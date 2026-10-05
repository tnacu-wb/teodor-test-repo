package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxRoomsRuleResponseDto;

@Mapper(componentModel = "spring")
public interface MaxRoomRuleMapper {

  MaxRoomsRuleResponse toModel(MaxRoomsRuleResponseDto maxRoomsRuleResponseDto);
}