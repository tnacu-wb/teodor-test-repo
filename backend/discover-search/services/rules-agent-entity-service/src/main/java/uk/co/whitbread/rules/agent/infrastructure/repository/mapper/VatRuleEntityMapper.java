package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.VatRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.VatRuleEntity;

@Mapper(componentModel = "spring")
public interface VatRuleEntityMapper {

  VatRule toModel(VatRuleEntity vatRuleEntity);

}
