package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RbacRuleEntity;

class RbacRuleEntityMapperTest {

  private final RbacRuleEntityMapper rbacRuleEntityMapper = new RbacRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRuleId(1);
    rbacRuleEntity.setCreatedAt(time);
    rbacRuleEntity.setLastModifiedAt(time);
    rbacRuleEntity.setStatus("ACTIVE");
    rbacRuleEntity.setResourceId("CCUI_RES1");
    rbacRuleEntity.setRoleId("AGENT");
    rbacRuleEntity.setHasAccess(true);
    var rbacRule = RbacRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .resourceId("CCUI_RES1")
        .roleId("AGENT")
        .hasAccess(true)
        .build();

    //Act
    var result = rbacRuleEntityMapper.toModel(rbacRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(rbacRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleEntity = new RbacRuleEntity();
    rbacRuleEntity.setRuleId(1);
    rbacRuleEntity.setStatus(null);
    rbacRuleEntity.setCreatedAt(time);
    rbacRuleEntity.setLastModifiedAt(time);
    rbacRuleEntity.setResourceId("CCUI_RES1");
    rbacRuleEntity.setRoleId("AGENT");
    rbacRuleEntity.setHasAccess(true);
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> rbacRuleEntityMapper.toModel(rbacRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = rbacRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
