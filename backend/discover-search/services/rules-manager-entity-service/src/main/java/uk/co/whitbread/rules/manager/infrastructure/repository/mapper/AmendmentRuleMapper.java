package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.AmendmentRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.AmendmentRuleEntity;

@Mapper(componentModel = "spring",
    uses = {AmendmentRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AmendmentRuleMapper {

  AmendmentRule toDomainModel(AmendmentRuleEntity entity);

  AmendmentRuleEntity toEntityDto(AmendmentRule domain);
}
