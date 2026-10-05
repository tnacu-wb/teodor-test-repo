package uk.co.whitbread.rules.agent.domain.logic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RbacRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class RbacRuleInPortImplTest {

  @Mock
  private RbacRuleRepositoryOutPort rbacRuleRepositoryOutPort;

  @InjectMocks
  private RbacRuleInPortImpl rbacRuleInPort;


  @Test
  void getAllRbacRule__shouldRetrunOk() {
    //Arrange
    var rbacList = createRbacRuleList();
    var roleIdList = List.of("AGENT_ROLE", "MANAGER_ROLE");
    when(rbacRuleRepositoryOutPort.findAllRbacRuleByRoles(roleIdList)).thenReturn(rbacList);

    //Act
    var response = rbacRuleInPort.getRbacRuleByRoles(roleIdList);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.size()).isSameAs(2);
    assertThat(response.get(0)).isEqualTo(rbacList.get(0).getResourceId());
    assertThat(response.get(1)).isEqualTo(rbacList.get(1).getResourceId());
    verifyNoMoreInteractions(rbacRuleRepositoryOutPort);
  }

  @Test
  void getAllHasAccess__shouldRetrunOk() {
    //Arrange
    var roleIdList = List.of("AGENT_ROLE", "MANAGER_ROLE");
    var resourceId = "CC_ROLE1";
    when(rbacRuleRepositoryOutPort.getRbacHasAccess(roleIdList, resourceId)).thenReturn(
        Optional.of(Boolean.TRUE));

    //Act
    var response = rbacRuleInPort.getRbacHasAccess(roleIdList, resourceId);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getHasAccess()).isEqualTo(Boolean.TRUE);
    verifyNoMoreInteractions(rbacRuleRepositoryOutPort);
  }

  @Test
  void getAllHasAccess__shouldReturnBooleanFalse() {
    //Arrange
    var roleIdList = List.of("AGENT_ROLE", "MANAGER_ROLE");
    var resourceId = "CC_ROLE1";
    when(rbacRuleRepositoryOutPort.getRbacHasAccess(roleIdList, resourceId)).thenReturn(
        Optional.empty());

    //Act
    var response =  rbacRuleInPort.getRbacHasAccess(roleIdList, resourceId);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getHasAccess(), is(Boolean.FALSE));
    verifyNoMoreInteractions(rbacRuleRepositoryOutPort);
  }

  private List<RbacRule> createRbacRuleList() {
    var time = LocalDateTime.now();
    var rbacRule1 = RbacRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .hasAccess(true)
        .roleId("CCUI_RES1")
        .resourceId("AGENT_ROLE")
        .build();
    var rbacRule2 = RbacRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .hasAccess(true)
        .roleId("CCUI_RES1")
        .resourceId("MANAGER_ROLE")
        .build();
    return List.of(rbacRule1, rbacRule2);
  }
}
