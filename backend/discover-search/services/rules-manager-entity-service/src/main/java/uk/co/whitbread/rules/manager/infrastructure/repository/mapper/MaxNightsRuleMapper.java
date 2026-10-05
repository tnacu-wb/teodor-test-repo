package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.MaxNightsRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxNightsRuleEntity;

@Mapper(componentModel = "spring",
    uses = {MaxNightsRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MaxNightsRuleMapper {

  MaxNightsRule toDomainModel(MaxNightsRuleEntity entity);

  MaxNightsRuleEntity toEntityDto(MaxNightsRule domain);
}