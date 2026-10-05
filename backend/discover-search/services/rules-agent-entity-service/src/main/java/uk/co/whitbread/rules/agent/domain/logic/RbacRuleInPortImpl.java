package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.RbacRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RbacRuleRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class RbacRuleInPortImpl implements RbacRuleInPort {

  private final RbacRuleRepositoryOutPort rbacRuleRepositoryOutPort;

  @Override
  public List<String> getRbacRuleByRoles(List<String> rbacRoleIds) {
    return rbacRuleRepositoryOutPort.findAllRbacRuleByRoles(rbacRoleIds).stream()
        .map(RbacRule::getResourceId)
        .distinct()
        .toList();
  }

  @Override
  public RbacRuleResponse getRbacHasAccess(List<String> roleIdList, String resourceId) {
    var hasAccess = rbacRuleRepositoryOutPort.getRbacHasAccess(roleIdList, resourceId)
        .orElseGet(() -> {
          log.error("Unable to find resourceId {} ", resourceId);
          return Boolean.FALSE;
        });
    return RbacRuleResponse.builder()
        .hasAccess(hasAccess)
        .generatedAt(LocalDateTime.now())
        .build();
  }

}
