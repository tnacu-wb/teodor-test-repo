package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.BusinessAllowanceRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BusinessAllowanceRuleEntity;

@Mapper(componentModel = "spring",
    uses = {BusinessAllowanceRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BusinessAllowanceRuleMapper {

  BusinessAllowanceRule toDomainModel(BusinessAllowanceRuleEntity entity);

  BusinessAllowanceRuleEntity toEntityDto(BusinessAllowanceRule domain);

}
