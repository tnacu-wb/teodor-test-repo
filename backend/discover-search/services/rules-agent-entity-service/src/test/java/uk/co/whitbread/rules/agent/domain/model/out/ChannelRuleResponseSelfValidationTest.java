package uk.co.whitbread.rules.agent.domain.model.out;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class ChannelRuleResponseSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());
  private final LocalDateTime time = LocalDateTime.now();

  @Test
  void constructor_nullSourceId_shouldSelfValidateAndThrow() {
    String expectedMessage = "sourceId: must not be null";

    checkErrorThrown(() -> ChannelRuleResponse.builder()
        .ratePlanSets(List.of("PBN"))
        .sourceId(null)
        .requestDetails(createRequestDetails())
        .generatedAt(time)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyRatePlanSets_shouldSelfValidateAndThrow() {
    String expectedMessage = "ratePlanSets: must not be empty";

    checkErrorThrown(() -> ChannelRuleResponse.builder()
        .ratePlanSets(emptyList())
        .sourceId("11")
        .requestDetails(createRequestDetails())
        .generatedAt(time)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullRequestDetails_shouldSelfValidateAndThrow() {
    String expectedMessage = "requestDetails: must not be null";

    checkErrorThrown(() -> ChannelRuleResponse.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBF"))
        .requestDetails(null)
        .generatedAt(time)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullGeneratedAt_shouldSelfValidateAndThrow() {
    String expectedMessage = "generatedAt: must not be null";

    checkErrorThrown(() -> ChannelRuleResponse.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBF"))
        .requestDetails(createRequestDetails())
        .generatedAt(null)
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      ChannelRuleResponse.builder()
          .sourceId("11")
          .ratePlanSets(List.of("PBF"))
          .requestDetails(createRequestDetails())
          .generatedAt(time)
          .build();
    });
  }

  private ChannelRuleRequestDetails createRequestDetails() {
    return ChannelRuleRequestDetails.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
