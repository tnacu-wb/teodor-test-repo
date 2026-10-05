package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomsRuleEntity;

class MaxRoomsRuleEntityMapperTest {

  private final MaxRoomsRuleEntityMapper maxRoomsRuleEntityMapper = new MaxRoomsRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRuleId(1);
    maxRoomsRuleEntity.setCreatedAt(time);
    maxRoomsRuleEntity.setLastModifiedAt(time);
    maxRoomsRuleEntity.setStatus("ACTIVE");
    maxRoomsRuleEntity.setChannelId("CCUI");
    maxRoomsRuleEntity.setMaxRooms(9);
    var maxRoomsRule = MaxRoomsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .channelId("CCUI")
        .maxRooms(9)
        .build();

    //Act
    var result = maxRoomsRuleEntityMapper.toModel(maxRoomsRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomsRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRuleId(1);
    maxRoomsRuleEntity.setStatus(null);
    maxRoomsRuleEntity.setCreatedAt(time);
    maxRoomsRuleEntity.setLastModifiedAt(time);
    maxRoomsRuleEntity.setChannelId("CCUI");
    maxRoomsRuleEntity.setMaxRooms(9);
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> maxRoomsRuleEntityMapper.toModel(maxRoomsRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = maxRoomsRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
