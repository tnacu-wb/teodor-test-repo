package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RbacRuleEntity;

@Mapper(componentModel = "spring")
public interface RbacRuleEntityMapper {

  RbacRule toModel(RbacRuleEntity rbacRuleEntity);
}
