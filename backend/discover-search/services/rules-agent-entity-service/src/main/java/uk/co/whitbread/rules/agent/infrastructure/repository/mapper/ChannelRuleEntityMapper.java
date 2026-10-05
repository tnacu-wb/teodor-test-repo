package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.ChannelRuleEntity;

@Mapper(componentModel = "spring")
public interface ChannelRuleEntityMapper {

  ChannelRule toModel(ChannelRuleEntity channelRuleEntity);
}
