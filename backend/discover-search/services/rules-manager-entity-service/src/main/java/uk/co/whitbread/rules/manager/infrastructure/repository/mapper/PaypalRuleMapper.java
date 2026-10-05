package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.PaypalRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.PaypalRuleEntity;

@Mapper(componentModel = "spring",
    uses = {PaypalRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaypalRuleMapper {

  PaypalRule toDomainModel(PaypalRuleEntity entity);

  PaypalRuleEntity toEntityDto(PaypalRule domain);
}