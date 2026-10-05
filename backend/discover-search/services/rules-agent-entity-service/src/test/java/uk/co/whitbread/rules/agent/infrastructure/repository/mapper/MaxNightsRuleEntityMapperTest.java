package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxNightsRuleEntity;

class MaxNightsRuleEntityMapperTest {

  private final MaxNightsRuleEntityMapper maxNightsRuleEntityMapper = new MaxNightsRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var maxNightsRuleEntity = new MaxNightsRuleEntity();
    maxNightsRuleEntity.setRuleId(1);
    maxNightsRuleEntity.setCreatedAt(time);
    maxNightsRuleEntity.setLastModifiedAt(time);
    maxNightsRuleEntity.setStatus("ACTIVE");
    maxNightsRuleEntity.setChannelId("CCUI");
    maxNightsRuleEntity.setMaxNights(9);
    var maxNightsRule = MaxNightsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .channelId("CCUI")
        .maxNights(9)
        .build();

    //Act
    var result = maxNightsRuleEntityMapper.toModel(maxNightsRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxNightsRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var maxNightsRuleEntity = new MaxNightsRuleEntity();
    maxNightsRuleEntity.setRuleId(1);
    maxNightsRuleEntity.setStatus(null);
    maxNightsRuleEntity.setCreatedAt(time);
    maxNightsRuleEntity.setLastModifiedAt(time);
    maxNightsRuleEntity.setChannelId("CCUI");
    maxNightsRuleEntity.setMaxNights(9);
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> maxNightsRuleEntityMapper.toModel(maxNightsRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxNightsRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
