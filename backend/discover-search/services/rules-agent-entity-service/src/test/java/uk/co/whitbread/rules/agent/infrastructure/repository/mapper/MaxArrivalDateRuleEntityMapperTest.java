package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxArrivalDateRuleEntity;

class MaxArrivalDateRuleEntityMapperTest {

  private final MaxArrivalDateRuleEntityMapper maxArrivalDateRuleEntityMapper = new MaxArrivalDateRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity.setRuleId(1);
    maxArrivalDateRuleEntity.setCreatedAt(time);
    maxArrivalDateRuleEntity.setLastModifiedAt(time);
    maxArrivalDateRuleEntity.setStatus("ACTIVE");
    maxArrivalDateRuleEntity.setChannelId("CCUI");
    maxArrivalDateRuleEntity.setMaxArrivalDate(9);
    var maxArrivalDateRule = MaxArrivalDateRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .channelId("CCUI")
        .maxArrivalDate(9)
        .build();

    //Act
    var result = maxArrivalDateRuleEntityMapper.toModel(maxArrivalDateRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxArrivalDateRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var maxArrivalDateRuleEntity = new MaxArrivalDateRuleEntity();
    maxArrivalDateRuleEntity.setRuleId(1);
    maxArrivalDateRuleEntity.setStatus(null);
    maxArrivalDateRuleEntity.setCreatedAt(time);
    maxArrivalDateRuleEntity.setLastModifiedAt(time);
    maxArrivalDateRuleEntity.setChannelId("CCUI");
    maxArrivalDateRuleEntity.setMaxArrivalDate(9);
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> maxArrivalDateRuleEntityMapper.toModel(maxArrivalDateRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxArrivalDateRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
