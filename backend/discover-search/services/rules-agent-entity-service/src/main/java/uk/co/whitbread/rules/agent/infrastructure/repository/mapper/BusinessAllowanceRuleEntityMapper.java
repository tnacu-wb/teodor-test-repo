package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BusinessAllowanceRuleEntity;

@Mapper(componentModel = "spring")
public interface BusinessAllowanceRuleEntityMapper {

  BusinessAllowanceRule toModel(BusinessAllowanceRuleEntity businessAllowanceRuleEntity);
}
