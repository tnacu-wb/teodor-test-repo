package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.ChannelRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.ChannelRuleResponseDto;

@Mapper(componentModel = "spring")
public interface ChannelRuleDtoMapper {

  ChannelRuleRequest toModel(ChannelRuleRequestDto channelRuleRequestDto);

  ChannelRuleResponseDto toDto(ChannelRuleResponse channelRuleResponse);

}
