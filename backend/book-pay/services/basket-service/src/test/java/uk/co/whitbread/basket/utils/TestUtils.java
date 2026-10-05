package uk.co.whitbread.basket.utils;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Arrays;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

public class TestUtils {

  public static void checkErrorThrown(Executable executable, String[] errors) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    Arrays.stream(errors)
        .forEach(error -> Assertions.assertTrue(thrownException.getMessage().contains(error)));
  }

  public static void checkErrorThrown(Executable executable, String error) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    Assertions.assertTrue(thrownException.getMessage().contains(error));
  }
}
