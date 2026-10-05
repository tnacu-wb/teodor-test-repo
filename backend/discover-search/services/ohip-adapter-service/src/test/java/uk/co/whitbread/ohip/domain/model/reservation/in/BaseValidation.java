/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.Config;
import uk.co.whitbread.ohip.domain.model.validation.ValidatorFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Config.class })
public class BaseValidation {
  @Autowired
  protected ValidatorFactory hibernateValidator;

  protected void checkErrorThrown(final Executable executable, final String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();
    assertEquals(expectedMessage, actualMessage);
  }

  protected void checkErrorThrownContainsMessage(final Executable executable, final String expectedMessage) {
    checkErrorThrownContainsMessage(executable, new String[]{expectedMessage});
  }

  protected void checkErrorThrownContainsMessage(final Executable executable, final String... possibleExpectedMessages) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();
    assertTrue(actualMessage != null && Arrays.stream(possibleExpectedMessages)
        .anyMatch(actualMessage::contains));
  }

}
