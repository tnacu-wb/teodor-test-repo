package uk.co.whitbread.rules.agent.domain.ports.secondary;

import java.util.List;
import java.util.Optional;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;

public interface RbacRuleRepositoryOutPort extends RuleEngineRepositoryOutPort<RbacRule> {

  List<RbacRule> findAllRbacRuleByRoles(List<String> rbacRoleIds);

  Optional<Boolean> getRbacHasAccess(List<String> roleIdList, String resourceId);
}
