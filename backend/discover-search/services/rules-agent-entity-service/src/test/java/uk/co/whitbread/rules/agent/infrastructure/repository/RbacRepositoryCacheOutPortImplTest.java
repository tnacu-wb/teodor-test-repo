package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.in.RbacRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RbacRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RbacRuleEntity;

@ExtendWith(MockitoExtension.class)
class RbacRepositoryCacheOutPortImplTest {

  @Mock
  private RbacCacheRepository rbacCacheRepository;

  @InjectMocks
  private RbacRepositoryCacheOutPortImpl rbacRepositoryCacheOutPort;

  @Mock
  private RbacRuleEntityMapper rbacRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var rre1 = createRbacRuleEntity(1, "ACTIVE");
    var rre2 = createRbacRuleEntity(2, "ACTIVE");
    when(rbacCacheRepository.findAllByStatusActive()).thenReturn(List.of(rre1, rre2));
    Map<Integer, RbacRuleEntity> expectedCachedRules = Map.of(1, rre1, 2, rre2);

    //Act
    rbacRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(rbacRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        rbacRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(rbacCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(rbacCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var rre1 = createRbacRuleEntity(1, "ACTIVE");
    var rre2 = createRbacRuleEntity(2, "ACTIVE");
    when(rbacCacheRepository.findAllByStatusActive()).thenReturn(List.of(rre1, rre2));
    rbacRepositoryCacheOutPort.cacheRules();
    var rre1inactive = createRbacRuleEntity(1, "INACTIVE");
    var rre3 = createRbacRuleEntity(3, "ACTIVE");
    when(rbacCacheRepository.findAllUpdatedAfter(any())).thenReturn(List.of(rre1inactive, rre3));
    Map<Integer, RbacRuleEntity> expectedCachedRules = Map.of(2, rre2, 3, rre3);

    //Act
    rbacRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(rbacRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        rbacRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(rbacCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(rbacCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var rre1 = createRbacRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var rre2 = createRbacRuleEntity(2, "ACTIVE");
    when(rbacCacheRepository.findAllUpdatedAfter(any())).thenReturn(List.of(rre1, rre2));
    var expectedCachedRules = Map.of(2, rre2);

    //Act
    rbacRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(rbacRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(rbacCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(rbacCacheRepository);
  }


  @Test
  void findAllRbacRule_withRoleID_shouldReturnOptionalEmpty() throws IllegalAccessException {
    //Arrange
    var rbacRequest = List.of("AGENT_ROLE", "MANAGER_ROLE");
    var rbacEntity1 = createRRE(1, true, "CCUI_RES1", "AGENT_ROLE", 1);
    var rbacEntity2 = createRRE(2, false, "CCUI_RES6", "AGENT_ROLE", 2);
    var rbacEntity3 = createRRE(3, true, "CCUI_RES7", "MANAGER_ROLE", 3);
    var rbacEntity4 = createRRE(4, true, "CCUI_RES7", "MANAGER_ROLE", 5);
    setCachedRules(List.of(rbacEntity1, rbacEntity2, rbacEntity3, rbacEntity4));
    when(rbacRuleEntityMapper.toModel(rbacEntity1)).thenReturn(createRbacRuleDomain(rbacEntity1));
    when(rbacRuleEntityMapper.toModel(rbacEntity3)).thenReturn(createRbacRuleDomain(rbacEntity3));
    when(rbacRuleEntityMapper.toModel(rbacEntity4)).thenReturn(createRbacRuleDomain(rbacEntity4));

    //Act
    var response = rbacRepositoryCacheOutPort.findAllRbacRuleByRoles(rbacRequest);

    //Assert
    assertThat(response).isNotNull();
    assertThat(response.size()).isSameAs(3);
    verifyNoMoreInteractions(rbacRuleEntityMapper);
    verifyNoMoreInteractions(rbacCacheRepository);
  }

  @Test
  void findHasAcces_withRoleIDList_shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var rbacRequest = List.of("AGENT_ROLE", "MANAGER_ROLE");
    var rbacResourceid = "CCUI_RES1";
    var rbacEntity1 = createRRE(1, true, "CCUI_RES1", "AGENT_ROLE", 1);
    var rbacEntity2 = createRRE(2, false, "CCUI_RES1", "AGENT_ROLE", 2);
    setCachedRules(List.of(rbacEntity1, rbacEntity2));

    //Act
    var response = rbacRepositoryCacheOutPort.getRbacHasAccess(rbacRequest, rbacResourceid);

    //Assert
    assertThat(response).isNotNull();
    assertThat(response).isEqualTo(Optional.of(Boolean.TRUE));
    verifyNoMoreInteractions(rbacRuleEntityMapper);
    verifyNoMoreInteractions(rbacCacheRepository);
  }

  @Test
  void findHasAcces_withRoleIDList_shouldReturnOptionalEmpty() throws IllegalAccessException {
    //Arrange
    var rbacRequest = List.of("AGENT_ROLE", "MANAGER_ROLE");
    var rbacResourceid = "CCUI_RES145";
    var rbacEntity1 = createRRE(1, true, "CCUI_RES1", "AGENT_ROLE", 1);
    var rbacEntity2 = createRRE(2, false, "CCUI_RES1", "AGENT_ROLE", 2);
    setCachedRules(List.of(rbacEntity1, rbacEntity2));

    //Act
    var response = rbacRepositoryCacheOutPort.getRbacHasAccess(rbacRequest, rbacResourceid);

    //Assert
    assertThat(response).isNotNull();
    assertThat(response).isEmpty();
    verifyNoMoreInteractions(rbacRuleEntityMapper);
    verifyNoMoreInteractions(rbacCacheRepository);
  }

  @Test
  void findHasAcces_withWrongRoleId_shouldReturnOptionalEmpty() throws IllegalAccessException {
    //Arrange
    var rbacRequest = List.of("VER", "SER");
    var rbacResourceid = "CCUI_RES1";
    var rbacEntity1 = createRRE(1, true, "CCUI_RES1", "AGENT_ROLE", 1);
    var rbacEntity2 = createRRE(2, false, "CCUI_RES1", "AGENT_ROLE", 2);
    setCachedRules(List.of(rbacEntity1, rbacEntity2));

    //Act
    var response = rbacRepositoryCacheOutPort.getRbacHasAccess(rbacRequest, rbacResourceid);

    //Assert
    assertThat(response).isNotNull();
    assertThat(response).isEmpty();
    verifyNoMoreInteractions(rbacRuleEntityMapper);
    verifyNoMoreInteractions(rbacCacheRepository);
  }


  private RbacRuleEntity createRbacRuleEntity(Integer ruleId, String status) {
    var rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRuleId(ruleId);
    rbacRuleEntity.setStatus(status);
    return rbacRuleEntity;
  }

  private RbacRuleEntity createRRE(Integer ruleId, Boolean hasAcces, String resourceId,
      String roleId, Integer delayTime) {
    var time = LocalDateTime.now();
    var rre = new RbacRuleEntity();
    rre.setRuleId(ruleId);
    rre.setHasAccess(hasAcces);
    rre.setResourceId(resourceId);
    rre.setRoleId(roleId);
    rre.setLastModifiedAt(time.minusMinutes(delayTime));
    return rre;
  }

  private RbacRule createRbacRuleDomain(RbacRuleEntity rbacRuleEntity) {
    var time = LocalDateTime.now();
    return RbacRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .hasAccess(rbacRuleEntity.getHasAccess())
        .roleId(rbacRuleEntity.getRoleId())
        .resourceId(rbacRuleEntity.getResourceId())
        .build();
  }

  private void setCachedRules(List<RbacRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, RbacRuleEntity> cachedRules = (Map<Integer, RbacRuleEntity>) FieldUtils.readField(
        rbacRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
