package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.RbacRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RbacRuleEntity;

@Mapper(componentModel = "spring",
    uses = {RbacRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RbacRuleMapper {

  RbacRule toDomainModel(RbacRuleEntity entity);

  RbacRuleEntity toEntityDto(RbacRule domain);
}
