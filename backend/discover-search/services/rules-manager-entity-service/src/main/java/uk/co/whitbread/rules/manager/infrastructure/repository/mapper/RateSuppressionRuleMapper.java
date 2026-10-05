package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.RateSuppressionRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RateSuppressionRuleEntity;

@Mapper(componentModel = "spring",
    uses = {RateSuppressionRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RateSuppressionRuleMapper {

  RateSuppressionRule toDomainModel(RateSuppressionRuleEntity entity);

  RateSuppressionRuleEntity toEntityDto(RateSuppressionRule domain);
}
