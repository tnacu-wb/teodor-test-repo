package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class RoomSubstitutionRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var roomSubstitutionRule = createValidRoomSubstitutionRule();
    roomSubstitutionRule.setRuleId(null);

    checkErrorThrown(roomSubstitutionRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var roomSubstitutionRule = createValidRoomSubstitutionRule();
    roomSubstitutionRule.setStatus(null);

    checkErrorThrown(roomSubstitutionRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var roomSubstitutionRule = createValidRoomSubstitutionRule();
    roomSubstitutionRule.setCreatedAt(null);

    checkErrorThrown(roomSubstitutionRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var roomSubstitutionRule = createValidRoomSubstitutionRule();
    roomSubstitutionRule.setLastModifiedAt(null);

    checkErrorThrown(roomSubstitutionRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullPms_shouldValidateAndThrow() {
    String expectedMessage = "pms: must not be empty";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().pms(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullAdults_shouldValidateAndThrow() {
    String expectedMessage = "adults: must not be null";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().adults(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullChildren_shouldValidateAndThrow() {
    String expectedMessage = "children: must not be null";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().children(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullRoomType_shouldValidateAndThrow() {
    String expectedMessage = "roomType: must not be empty";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().roomType(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullPmsRoomType_shouldValidateAndThrow() {
    String expectedMessage = "pmsRoomType: must not be empty";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().pmsRoomType(null).build()
            .validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullOfferOrder_shouldValidateAndThrow() {
    String expectedMessage = "offerOrder: must not be null";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().offerOrder(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullSpecialRequest_shouldValidateAndThrow() {
    String expectedMessage = "specialRequest: must not be empty";

    checkErrorThrown(
        () -> createValidRoomSubstitutionRule().toBuilder().specialRequest(null).build()
            .validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidRoomSubstitutionRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  RoomSubstitutionRule createValidRoomSubstitutionRule() {
    return RoomSubstitutionRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(TIME)
        .lastModifiedAt(TIME)
        .pms("OP")
        .adults(2)
        .children(0)
        .roomType("DB")
        .pmsRoomType("DBLWIN")
        .offerOrder(1)
        .specialRequest("DBLE")
        .channel("PI")
        .build();
  }
}
