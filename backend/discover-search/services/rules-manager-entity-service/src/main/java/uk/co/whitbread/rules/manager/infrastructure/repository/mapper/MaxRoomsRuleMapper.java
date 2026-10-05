package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomsRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxRoomsRuleEntity;

@Mapper(componentModel = "spring",
    uses = {MaxRoomsRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MaxRoomsRuleMapper {

  MaxRoomsRule toDomainModel(MaxRoomsRuleEntity entity);

  MaxRoomsRuleEntity toEntityDto(MaxRoomsRule domain);
}