package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.BaseRateRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.BaseRateRuleEntity;

@Mapper(componentModel = "spring",
    uses = {BaseRateRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BaseRateRuleMapper {
  
  BaseRateRule toDomainModel(BaseRateRuleEntity entity);
  
  BaseRateRuleEntity toEntityDto(BaseRateRule domain);
}
