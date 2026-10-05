package uk.co.whitbread.rules.agent.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRuleResponse;

public interface RbacRuleInPort {

  List<String> getRbacRuleByRoles(List<String> rbacRoleIds);

  RbacRuleResponse getRbacHasAccess(List<String> roleIdList, String resourceId);
}
