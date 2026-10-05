package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.RoomSubstitutionRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@Mapper(componentModel = "spring",
    uses = {RoomSubstitutionRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomSubstitutionRuleMapper {

  RoomSubstitutionRule toDomainModel(RoomSubstitutionRuleEntity entity);

  RoomSubstitutionRuleEntity toEntityDto(RoomSubstitutionRule domain);
}
