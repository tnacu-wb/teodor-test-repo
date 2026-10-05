package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.RbacRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.RbacRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.RbacRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleHasAccessRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleRoleIdsRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RbacRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules/ccui")
public class RbacRuleController implements RbacRuleApiDocumentation {

  private final RbacRuleInPort rbacRuleInPort;
  private final RbacRuleDtoMapper rbacRuleDtoMapper;


  @GetMapping(value = "/rbac/resourceId", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getAllRbacRule(
      @Valid @ParameterObject RbacRuleRoleIdsRequestDto rbacRoleIds) {

    return rbacRuleInPort.getRbacRuleByRoles(rbacRoleIds.getRoleIdList());

  }

  @GetMapping(value = "/rbac", produces = MediaType.APPLICATION_JSON_VALUE)
  public RbacRuleResponseDto getRbacHasAccess(
      @Valid @ParameterObject RbacRuleHasAccessRequestDto rbacRuleHasAccessRequestDto) {

    var domainRbacRuleResponse =  rbacRuleInPort.getRbacHasAccess(rbacRuleHasAccessRequestDto.getRoleIdList(),
        rbacRuleHasAccessRequestDto.getResourceId());
    return rbacRuleDtoMapper.toDto(domainRbacRuleResponse);

  }
}
