package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MaxRoomOccupancyRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var maxRoomsOccupancyRule = createValidMaxRoomOccupancyRule();
    maxRoomsOccupancyRule.setRuleId(null);

    checkErrorThrown(maxRoomsOccupancyRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var maxRoomsOccupancyRule = createValidMaxRoomOccupancyRule();
    maxRoomsOccupancyRule.setStatus(null);

    checkErrorThrown(maxRoomsOccupancyRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var maxRoomsOccupancyRule = createValidMaxRoomOccupancyRule();
    maxRoomsOccupancyRule.setCreatedAt(null);

    checkErrorThrown(maxRoomsOccupancyRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var maxRoomsOccupancyRule = createValidMaxRoomOccupancyRule();
    maxRoomsOccupancyRule.setLastModifiedAt(null);

    checkErrorThrown(maxRoomsOccupancyRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullChannelId_shouldValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().channelId(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullAdults_shouldValidateAndThrow() {
    String expectedMessage = "adults: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().adults(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullChildren_shouldValidateAndThrow() {
    String expectedMessage = "children: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().children(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullSingleRoom_shouldValidateAndThrow() {
    String expectedMessage = "singleRoom: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().singleRoom(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullDoubleRoom_shouldValidateAndThrow() {
    String expectedMessage = "doubleRoom: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().doubleRoom(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullTwinRoom_shouldValidateAndThrow() {
    String expectedMessage = "twinRoom: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().twinRoom(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullAccessibleRoom_shouldValidateAndThrow() {
    String expectedMessage = "accessibleRoom: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().accessibleRoom(null).build()
            .validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullFamilyRoom_shouldValidateAndThrow() {
    String expectedMessage = "familyRoom: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomOccupancyRule().toBuilder().familyRoom(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidMaxRoomOccupancyRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  private MaxRoomOccupancyRule createValidMaxRoomOccupancyRule() {
    return MaxRoomOccupancyRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(TIME)
        .lastModifiedAt(TIME)
        .channelId("channel")
        .adults(1)
        .children(0)
        .singleRoom(true)
        .doubleRoom(true)
        .twinRoom(false)
        .accessibleRoom(true)
        .familyRoom(false)
        .build();
  }

}
