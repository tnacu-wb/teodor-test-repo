package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.MaxArrivalDateRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.MaxArrivalDateRuleEntity;

@Mapper(componentModel = "spring",
    uses = {MaxArrivalDateRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MaxArrivalDateMapper {

  MaxArrivalDateRule toDomainModel(MaxArrivalDateRuleEntity entity);

  MaxArrivalDateRuleEntity toEntityDto(MaxArrivalDateRule domain);
}