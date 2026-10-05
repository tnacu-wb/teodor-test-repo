package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.AmendmentRuleEntity;

@Mapper(componentModel = "spring")
public interface AmendmentRuleEntityMapper {

  AmendmentRule toModel(AmendmentRuleEntity amendmentRuleEntity);
}
