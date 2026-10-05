package uk.co.whitbread.rules.agent.utils;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.function.Executable;

public class TestUtils {

  public static void checkErrorThrown(Executable executable, String[] errors) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    Arrays.stream(errors)
        .forEach(error -> assertTrue(thrownException.getMessage().contains(error)));
  }

}
