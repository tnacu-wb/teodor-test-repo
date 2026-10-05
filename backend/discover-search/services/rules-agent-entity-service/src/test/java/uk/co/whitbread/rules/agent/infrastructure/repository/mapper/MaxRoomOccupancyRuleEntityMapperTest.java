package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomOccupancyRuleEntity;

class MaxRoomOccupancyRuleEntityMapperTest {

  private final MaxRoomOccupancyRuleEntityMapper mapper = new MaxRoomOccupancyRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(1);
    roomOccupancyRuleEntity.setCreatedAt(time);
    roomOccupancyRuleEntity.setLastModifiedAt(time);
    roomOccupancyRuleEntity.setStatus("ACTIVE");
    roomOccupancyRuleEntity.setChannelId("CCUI");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);
    roomOccupancyRuleEntity.setBrand("PI");

    var maxRoomOccupancyRule = MaxRoomOccupancyRule.builder()
        .ruleId(1)
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .channelId("CCUI")
        .adults(1)
        .children(0)
        .singleRoom(true)
        .doubleRoom(true)
        .twinRoom(false)
        .accessibleRoom(true)
        .familyRoom(false)
        .brand("PI")
        .build();

    //Act
    var result = mapper.toModel(roomOccupancyRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomOccupancyRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var roomOccupancyRuleEntity = new MaxRoomOccupancyRuleEntity();
    roomOccupancyRuleEntity.setRuleId(1);
    roomOccupancyRuleEntity.setCreatedAt(time);
    roomOccupancyRuleEntity.setLastModifiedAt(time);
    roomOccupancyRuleEntity.setStatus(null);
    roomOccupancyRuleEntity.setChannelId("CCUI");
    roomOccupancyRuleEntity.setAdults(1);
    roomOccupancyRuleEntity.setChildren(0);
    roomOccupancyRuleEntity.setSingleRoom(true);
    roomOccupancyRuleEntity.setDoubleRoom(true);
    roomOccupancyRuleEntity.setTwinRoom(false);
    roomOccupancyRuleEntity.setAccessibleRoom(true);
    roomOccupancyRuleEntity.setFamilyRoom(false);
    roomOccupancyRuleEntity.setBrand("PI");
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> mapper.toModel(roomOccupancyRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = mapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
