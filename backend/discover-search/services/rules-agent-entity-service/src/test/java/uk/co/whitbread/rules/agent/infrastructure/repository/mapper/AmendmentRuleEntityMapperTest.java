package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.AmendmentRuleEntity;

class AmendmentRuleEntityMapperTest {

  private final AmendmentRuleEntityMapper amendmentRuleEntityMapper = new AmendmentRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(1);
    amendmentRuleEntity.setCreatedAt(time);
    amendmentRuleEntity.setLastModifiedAt(time);
    amendmentRuleEntity.setStatus("ACTIVE");
    amendmentRuleEntity.setRateType("Flex");
    amendmentRuleEntity.setCountryCode("GB");
    amendmentRuleEntity.setArrivalDateLimit(3900);
    var amendmentRule = AmendmentRule.builder()
        .ruleId(1)
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .rateType("Flex")
        .countryCode("GB")
        .arrivalDateLimit(3900)
        .build();

    //Act
    var result = amendmentRuleEntityMapper.toModel(amendmentRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(amendmentRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(1);
    amendmentRuleEntity.setCreatedAt(time);
    amendmentRuleEntity.setLastModifiedAt(time);
    amendmentRuleEntity.setStatus(null);
    amendmentRuleEntity.setRateType("Flex");
    amendmentRuleEntity.setCountryCode("GB");
    amendmentRuleEntity.setArrivalDateLimit(3900);
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> amendmentRuleEntityMapper.toModel(amendmentRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullRateType_shouldTransformToModelObjectWithNullRateTypeAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(1);
    amendmentRuleEntity.setCreatedAt(time);
    amendmentRuleEntity.setLastModifiedAt(time);
    amendmentRuleEntity.setStatus("ACTIVE");
    amendmentRuleEntity.setRateType(null);
    amendmentRuleEntity.setCountryCode("GB");
    amendmentRuleEntity.setArrivalDateLimit(3900);
    var expectedMessage = "rateType: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> amendmentRuleEntityMapper.toModel(amendmentRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = amendmentRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
