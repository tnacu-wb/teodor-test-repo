package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.rules.manager.domain.model.in.VatRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.VatRuleEntity;

@Mapper(componentModel = "spring",
    uses = {VatRuleTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface VatRuleMapper {

  VatRule toDomainModel(VatRuleEntity entity);

  VatRuleEntity toEntityDto(VatRule domain);

}
