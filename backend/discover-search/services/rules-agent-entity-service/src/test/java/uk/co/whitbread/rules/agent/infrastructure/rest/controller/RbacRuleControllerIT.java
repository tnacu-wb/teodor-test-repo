package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.RbacRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.RbacRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleHasAccessRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleRoleIdsRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RbacRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class RbacRuleControllerIT {

  @InjectMocks
  private RbacRuleController rbacRuleController;

  @Mock
  private RbacRuleInPort rbacRuleInPort;

  @Mock
  private RbacRuleDtoMapper rbacRuleDtoMapper;

  @Test
  void shouldRetrieveAllRbacRule() {
    //Arrange
    var request = RbacRuleRoleIdsRequestDto.builder()
        .roleIdList(List.of("AGENT_ROLE", "MANAGER_ROLE"))
        .build();
    var rules = List.of("CCUI_ROLE1", "CCUI_ROLE2");

    when(rbacRuleInPort.getRbacRuleByRoles(request.getRoleIdList())).thenReturn(rules);

    //Act
    var response = rbacRuleController.getAllRbacRule(request);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals("CCUI_ROLE2", response.get(1));

  }

  @Test
  void shouldRetrieveRbacRuleHasAccess() {
    var request = RbacRuleHasAccessRequestDto.builder()
        .roleIdList(List.of("AGENT_ROLE", "MANAGER_ROLE"))
        .resourceId("CC_ROLE1")
        .build();

    when(rbacRuleInPort.getRbacHasAccess(request.getRoleIdList(),
        request.getResourceId())).thenReturn(mockRbacRuleResponse());
    when(rbacRuleDtoMapper.toDto(any())).thenReturn(mockRbacRuleResponseDto());

    //Act
    var response = rbacRuleController.getRbacHasAccess(request);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(true, response.getHasAccess());

  }

  private RbacRuleResponse mockRbacRuleResponse() {
    return RbacRuleResponse.builder().
        generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .hasAccess(true)
        .build();
  }

  private RbacRuleResponseDto mockRbacRuleResponseDto() {
    return RbacRuleResponseDto.builder().
        generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .hasAccess(true)
        .build();
  }

}
