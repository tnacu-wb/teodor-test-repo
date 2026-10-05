package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.ChannelRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.ChannelRuleEntity;

@Mapper(componentModel = "spring",
    uses = {ChannelRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ChannelRuleMapper {

  ChannelRule toDomainModel(ChannelRuleEntity entity);

  ChannelRuleEntity toEntityDto(ChannelRule domain);
}
