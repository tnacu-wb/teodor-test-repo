package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static uk.co.whitbread.rules.agent.domain.model.out.RuleStatus.ACTIVE;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RoomSubstitutionRuleEntity;

class RoomSubstitutionRuleEntityMapperTest {

  private static final String DOUBLE = "Double";
  private static final String OP = "OP";
  private final RoomSubstitutionRuleEntityMapper roomSubstitutionRuleEntityMapper = new RoomSubstitutionRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setAdults(2);
    roomSubstitutionRuleEntity.setChildren(0);
    roomSubstitutionRuleEntity.setCreatedAt(time);
    roomSubstitutionRuleEntity.setLastModifiedAt(time);
    roomSubstitutionRuleEntity.setPms(OP);
    roomSubstitutionRuleEntity.setRoomType(DOUBLE);
    roomSubstitutionRuleEntity.setRuleId(1);
    roomSubstitutionRuleEntity.setStatus(ACTIVE.name());
    var roomSubstitutionRule = RoomSubstitutionRule.builder()
        .adults(2)
        .children(0)
        .createdAt(time)
        .lastModifiedAt(time)
        .pms(OP)
        .roomType(DOUBLE)
        .ruleId(1)
        .status(ACTIVE)
        .build();

    //Act
    var result = roomSubstitutionRuleEntityMapper.toModel(roomSubstitutionRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(roomSubstitutionRule);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = roomSubstitutionRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
