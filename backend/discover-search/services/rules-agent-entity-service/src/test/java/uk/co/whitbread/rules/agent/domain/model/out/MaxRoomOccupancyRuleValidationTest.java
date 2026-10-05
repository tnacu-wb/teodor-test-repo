package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class MaxRoomOccupancyRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var roomOccupancyData = createValidMaxRoomOccupancyRule();
    roomOccupancyData.setRuleId(null);

    checkErrorThrown(roomOccupancyData::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var roomOccupancyData = createValidMaxRoomOccupancyRule();
    roomOccupancyData.setStatus(null);

    checkErrorThrown(roomOccupancyData::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var roomOccupancyData = createValidMaxRoomOccupancyRule();
    roomOccupancyData.setCreatedAt(null);

    checkErrorThrown(roomOccupancyData::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var roomOccupancyData = createValidMaxRoomOccupancyRule();
    roomOccupancyData.setLastModifiedAt(null);

    checkErrorThrown(roomOccupancyData::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullAdults_shouldValidateAndThrow() {
    String expectedMessage = "adults: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().adults(null).build(),
        expectedMessage);
  }

  @Test
  void validate_nullChildren_shouldValidateAndThrow() {
    String expectedMessage = "children: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().children(null).build(), expectedMessage);
  }

  @Test
  void validate_nullSingleRoom_shouldValidateAndThrow() {
    String expectedMessage = "singleRoom: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().singleRoom(null).build(),
        expectedMessage);
  }

  @Test
  void validate_nullDoubleRoom_shouldValidateAndThrow() {
    String expectedMessage = "doubleRoom: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().doubleRoom(null).build(),
        expectedMessage);
  }

  @Test
  void validate_nullTwinRoom_shouldValidateAndThrow() {
    String expectedMessage = "twinRoom: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().twinRoom(null).build(),
        expectedMessage);
  }

  @Test
  void validate_nullAccessibleRoom_shouldValidateAndThrow() {
    String expectedMessage = "accessibleRoom: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().accessibleRoom(null).build(),
        expectedMessage);
  }

  @Test
  void validate_nullFamilyRoom_shouldValidateAndThrow() {
    String expectedMessage = "familyRoom: must not be null";

    checkErrorThrown(() -> createValidMaxRoomOccupancyRule().toBuilder().familyRoom(null).build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidMaxRoomOccupancyRule());
  }

  private MaxRoomOccupancyRule createValidMaxRoomOccupancyRule() {
    return MaxRoomOccupancyRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
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
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
