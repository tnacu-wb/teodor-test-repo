package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.RateSuppressionRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RateSuppressionRuleEntity;

public class RateSuppressionRuleEntityMapperTest {

  private final RateSuppressionRuleEntityMapper rateSuppressionRuleEntityMapper = new RateSuppressionRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());


  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange

    var time = LocalDateTime.now();
    var rateSuppressionRuleEntity = createRateSuppressionRuleEntity(time);
    var maxArrivalDateRule = createRateSuppressionRule(time);

    //Act
    var result = rateSuppressionRuleEntityMapper.toModel(rateSuppressionRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxArrivalDateRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var rateSuppressionRuleEntity = createRateSuppressionRuleEntity(time);
    rateSuppressionRuleEntity.setStatus(null);
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> rateSuppressionRuleEntityMapper.toModel(rateSuppressionRuleEntity),
        expectedMessage);
  }

  @Test
  void toModel_givenNullRateType_shouldTransformToModelObjectWithNullRateTypeAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var rateSuppressionRuleEntity = createRateSuppressionRuleEntity(time);
    rateSuppressionRuleEntity.setRateType(null);
    var expectedMessage = "rateType: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> rateSuppressionRuleEntityMapper.toModel(rateSuppressionRuleEntity),
        expectedMessage);
  }


  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = rateSuppressionRuleEntityMapper.toModel(null);

    //Assert
    AssertionsForClassTypes.assertThat(result).isNull();
  }


  private RateSuppressionRuleEntity createRateSuppressionRuleEntity(LocalDateTime time) {

    var rateSuppressionRuleEntity = new RateSuppressionRuleEntity();
    rateSuppressionRuleEntity.setRuleId(1);
    rateSuppressionRuleEntity.setCreatedAt(time);
    rateSuppressionRuleEntity.setLastModifiedAt(time);
    rateSuppressionRuleEntity.setStatus("ACTIVE");
    rateSuppressionRuleEntity.setRateType("FLEX");
    rateSuppressionRuleEntity.setPriority((short) 10);
    return rateSuppressionRuleEntity;
  }

  private RateSuppressionRule createRateSuppressionRule(LocalDateTime time) {

    return RateSuppressionRule.builder()
        .ruleId(1)
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .rateType("FLEX")
        .priority((short) 10)
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
