package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.RbacRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RbacRuleEntity;

@ExtendWith(MockitoExtension.class)
class RbacRuleMapperTest {

  @InjectMocks
  private RbacRuleMapperImpl rbacRuleMapper;

  @Spy
  private RbacRuleTransformer rbacRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var rbacRuleDomain = RbacRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .roleId("some role")
        .resourceId("some resource")
        .hasAccess(Boolean.TRUE)
        .build();
    var rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRuleId(1234);
    rbacRuleEntity.setRefRuleId(0);
    rbacRuleEntity.setStatus("nEw");
    rbacRuleEntity.setCreatedAt(now);
    rbacRuleEntity.setLastModifiedAt(now);
    rbacRuleEntity.setEnableTimestamp(null);
    rbacRuleEntity.setDisableTimestamp(now.plusDays(30));
    rbacRuleEntity.setRoleId("some role");
    rbacRuleEntity.setResourceId("some resource");
    rbacRuleEntity.setHasAccess(Boolean.TRUE);

    //Act
    var result = rbacRuleMapper.toDomainModel(rbacRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(rbacRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = rbacRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var rbacRuleDomain = RbacRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .roleId("some role")
        .resourceId("some resource")
        .hasAccess(Boolean.TRUE)
        .build();
    var rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRuleId(1234);
    rbacRuleEntity.setRefRuleId(0);
    rbacRuleEntity.setStatus("NEW");
    rbacRuleEntity.setCreatedAt(now);
    rbacRuleEntity.setLastModifiedAt(now);
    rbacRuleEntity.setEnableTimestamp(null);
    rbacRuleEntity.setDisableTimestamp(now.plusDays(30));
    rbacRuleEntity.setRoleId("some role");
    rbacRuleEntity.setResourceId("some resource");
    rbacRuleEntity.setHasAccess(Boolean.TRUE);

    //Act
    var result = rbacRuleMapper.toEntityDto(rbacRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(rbacRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = rbacRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var rbacRuleDomain = RbacRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .roleId("some role")
        .resourceId("some resource")
        .hasAccess(Boolean.TRUE)
        .build();
    var rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRuleId(1234);
    rbacRuleEntity.setRefRuleId(0);
    rbacRuleEntity.setStatus(null);
    rbacRuleEntity.setCreatedAt(now);
    rbacRuleEntity.setLastModifiedAt(now);
    rbacRuleEntity.setEnableTimestamp(null);
    rbacRuleEntity.setDisableTimestamp(now.plusDays(30));
    rbacRuleEntity.setRoleId("some role");
    rbacRuleEntity.setResourceId("some resource");
    rbacRuleEntity.setHasAccess(Boolean.TRUE);

    //Act
    var result = rbacRuleMapper.toEntityDto(rbacRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }
}
