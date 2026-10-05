package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MaxRoomOccupancyRequestSelfValidationTest {

  @Test
  void constructor_emptyChannelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(() -> MaxRoomOccupancyRequest.builder().channelId("").brand("PI")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyBrand_shouldSelfValidateAndThrow() {
    String expectedMessage = "brand: must not be empty";

    checkErrorThrown(() -> MaxRoomOccupancyRequest.builder().channelId("PI").brand("")
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      MaxRoomOccupancyRequest.builder().channelId("CCUI").brand("PI")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
