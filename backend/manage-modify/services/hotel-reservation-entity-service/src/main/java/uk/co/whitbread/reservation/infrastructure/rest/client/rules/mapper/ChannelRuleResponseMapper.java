package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.ChannelRuleResponseDto;

@Mapper(componentModel = "spring")
public interface ChannelRuleResponseMapper {

  ChannelRuleResponse toModel(ChannelRuleResponseDto channelRuleResponseDto);
}
